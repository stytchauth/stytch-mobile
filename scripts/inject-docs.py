#!/usr/bin/env python3
"""
Inject doc comments from ObjC header into Swift symbol graph JSON.

Usage:
  python3 scripts/inject-docs.py <header.h> <input.symbols.json> <output.symbols.json>

Two lookup strategies:
  1. ObjC symbols (c:objc(...) identifiers): matched directly by type name + selector/property name
  2. SKIE-generated Swift extension methods (s:So... identifiers): matched via `memberOf`
     relationship to parent ObjC type, then by method base name from swift_name attribute
"""

import json
import re
import sys

# --- Header parsing ---


def extract_selector(line):
    """Extract ObjC selector from a method declaration line."""
    line = line.strip().lstrip("+-").strip()
    line = re.sub(r"^\([^)]+\)", "", line).strip()
    parts = re.findall(r"(\w+)\s*:", line)
    if parts:
        return ":".join(parts) + ":"
    m = re.match(r"^(\w+)", line)
    return m.group(1) if m else None


def extract_swift_name(line):
    """Extract the swift_name attribute value from a declaration line."""
    m = re.search(r'swift_name\("([^"]+)"\)', line)
    return m.group(1) if m else None


def swift_base_name(swift_name_str):
    """Extract base method name from swift_name value.

    e.g. 'authenticate(request:completionHandler__:)' -> 'authenticate'
    """
    return swift_name_str.split("(")[0]


def extract_property_name(line):
    """Extract property name from @property line."""
    line = re.sub(r"__attribute__.*", "", line).strip().rstrip(";").strip()
    parts = line.split()
    return parts[-1].lstrip("*") if parts else None


def parse_header(path):
    """Parse an ObjC header and return doc comment maps.

    Returns:
      objc_docs:  {type_name: doc} | {(type_name, selector_or_prop): doc}
      swift_docs: {(type_name, swift_base_method_name): doc}
    """
    with open(path) as f:
        lines = f.read().split("\n")

    objc_docs = {}
    swift_docs = {}
    current_type = None
    i = 0

    while i < len(lines):
        line = lines[i]

        m = re.match(r"@(?:interface|protocol)\s+(SCSDK\w+|SBBSDK\w+)", line)
        if m:
            current_type = m.group(1)
            i += 1
            continue
        if line.strip() == "@end":
            current_type = None
            i += 1
            continue

        if "/**" not in line:
            i += 1
            continue

        # Collect doc block
        doc_lines = []
        if "*/" in line:
            text = re.sub(r"/\*\*\s*|\s*\*/", "", line).strip()
            doc_lines = [text] if text else []
            i += 1
        else:
            opening_text = re.sub(r"/\*\*\s*", "", line).strip()
            if opening_text:
                doc_lines.append(opening_text)
            i += 1
            while i < len(lines) and "*/" not in lines[i]:
                text = lines[i].strip().lstrip("*").strip()
                if text:
                    doc_lines.append(text)
                i += 1
            i += 1  # skip */

        # Skip blanks and attributes to reach the declaration
        while i < len(lines):
            s = lines[i].strip()
            if not s or s.startswith("__attribute__") or s.startswith("@available"):
                i += 1
            else:
                break

        if i >= len(lines):
            continue

        decl = lines[i].strip()
        doc_text = "\n".join(doc_lines)

        if decl.startswith("@interface") or decl.startswith("@protocol"):
            m = re.match(r"@(?:interface|protocol)\s+(SCSDK\w+|SBBSDK\w+)", decl)
            if m:
                objc_docs[m.group(1)] = doc_text
                current_type = m.group(1)

        elif (decl.startswith("-") or decl.startswith("+")) and current_type:
            sel = extract_selector(decl)
            if sel:
                objc_docs[(current_type, sel)] = doc_text
            # Collect full declaration (may span lines) to extract swift_name
            full_decl = decl
            j = i
            while ";" not in full_decl and j + 1 < len(lines):
                j += 1
                full_decl += " " + lines[j].strip()
            sn = extract_swift_name(full_decl)
            if sn and current_type:
                swift_docs[(current_type, swift_base_name(sn))] = doc_text

        elif decl.startswith("@property") and current_type:
            name = extract_property_name(decl)
            if name:
                objc_docs[(current_type, name)] = doc_text

    return objc_docs, swift_docs


# --- Symbol graph processing ---


def precise_to_objc_key(precise):
    """Map c:objc(...) precise identifier to (type_name, member_or_None)."""
    m = re.match(r"c:objc\((?:cs|pl)\)(SCSDK\w+|SBBSDK\w+)$", precise)
    if m:
        return m.group(1), None

    m = re.match(r"c:objc\((?:cs|pl)\)(SCSDK\w+|SBBSDK\w+)\((?:im|cm)\)(.*)", precise)
    if m:
        return m.group(1), m.group(2)

    m = re.match(r"c:objc\((?:cs|pl)\)(SCSDK\w+|SBBSDK\w+)\((?:py|cpy)\)(.*)", precise)
    if m:
        return m.group(1), m.group(2)

    return None, None


def extract_objc_type_from_precise(precise):
    """Extract ObjC type name from c:objc(pl|cs)SCSDKFoo precise identifier."""
    m = re.match(r"c:objc\((?:cs|pl)\)(SCSDK\w+|SBBSDK\w+)", precise)
    return m.group(1) if m else None


def make_doc_comment(text):
    result_lines = []
    for idx, line in enumerate(text.split("\n")):
        if line:
            result_lines.append(
                {
                    "range": {
                        "start": {"line": idx, "character": 0},
                        "end": {"line": idx, "character": len(line)},
                    },
                    "text": line,
                }
            )
    return {"lines": result_lines}


def main():
    if len(sys.argv) != 4:
        print("Usage: inject-docs.py <header.h> <input.symbols.json> <output.symbols.json>")
        sys.exit(1)

    header_path, graph_path, output_path = sys.argv[1], sys.argv[2], sys.argv[3]

    print(f"Parsing header: {header_path}")
    objc_docs, swift_docs = parse_header(header_path)
    print(f"  ObjC symbol docs: {len(objc_docs)}")
    print(f"  Swift method docs (by base name): {len(swift_docs)}")

    print(f"Loading symbol graph: {graph_path}")
    with open(graph_path) as f:
        graph = json.load(f)

    symbols = graph.get("symbols", [])
    relationships = graph.get("relationships", [])

    # Build memberOf map: child_precise -> parent_precise
    member_of = {}
    for rel in relationships:
        if rel.get("kind") == "memberOf":
            member_of[rel["source"]] = rel["target"]

    injected_objc = injected_swift = skipped = 0

    for symbol in symbols:
        if symbol.get("docComment"):
            skipped += 1
            continue

        precise = symbol.get("identifier", {}).get("precise", "")

        # Strategy 1: ObjC identifier
        type_name, member = precise_to_objc_key(precise)
        if type_name is not None:
            doc_text = objc_docs.get(type_name) if member is None else objc_docs.get((type_name, member))
            if doc_text:
                symbol["docComment"] = make_doc_comment(doc_text)
                injected_objc += 1
                continue

        # Strategy 2: SKIE Swift extension method (s:So... identifier)
        if precise.startswith("s:So") and precise in member_of:
            parent_precise = member_of[precise]
            parent_objc_name = extract_objc_type_from_precise(parent_precise)
            if parent_objc_name:
                title = symbol.get("names", {}).get("title", "")
                method_base = title.split("(")[0]
                doc_text = swift_docs.get((parent_objc_name, method_base))
                if doc_text:
                    symbol["docComment"] = make_doc_comment(doc_text)
                    injected_swift += 1

    total_injected = injected_objc + injected_swift
    print(f"  Injected ObjC symbols: {injected_objc}")
    print(f"  Injected SKIE Swift methods: {injected_swift}")
    print(
        f"  Total injected: {total_injected}"
        f"  |  Already had docs: {skipped}"
        f"  |  Total symbols: {len(symbols)}"
    )

    with open(output_path, "w") as f:
        json.dump(graph, f)
    print(f"Written to {output_path}")


if __name__ == "__main__":
    main()
