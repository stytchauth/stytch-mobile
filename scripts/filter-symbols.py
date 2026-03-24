#!/usr/bin/env python3
"""
Remove internal/third-party symbols from a Swift symbol graph JSON before DocC processing.

Filters out:
  - Generated API network models (ApiXxx types from the networking package)
  - Ktor / Kotlinx / Ktorfit third-party library types
  - Kotlin stdlib types (KotlinAnnotation, KotlinThrowable, etc.)
  - SKIE infrastructure types (Skie_*, but NOT SkieKotlin* Flow wrappers)
  - Internal HTTP request body types (*Request)

Usage:
  python3 scripts/filter-symbols.py <input.symbols.json> <output.symbols.json>

Input and output may be the same path (in-place filtering).
"""

import json
import re
import sys
import os

# Each pattern is matched against the ObjC type name AFTER stripping the SCSDK/SBBSDK prefix.
EXCLUDE_PATTERNS = [
    r"^Ktor_",           # Ktor HTTP client library internals
    r"^Kotlinx_",        # Kotlinx coroutines / serialization / IO internals
    r"^Ktorfit_lib_",    # Ktorfit code-generation library internals
    r"^Api[A-Z]",        # Generated openapi network models (ApiUserV1*, ApiB2bRbacV1*, etc.)
    r"^Skie_",           # SKIE suspend/flow infrastructure (not the public SkieKotlin* wrappers)
    r"^Kotlin[A-Z]",     # Kotlin stdlib types (KotlinAnnotation, KotlinThrowable, KotlinEnum, …)
    r"Request$",         # Internal HTTP request body objects (*Request suffix)
    r"^JsCleanup",       # JS runtime cleanup hook, not relevant to iOS consumers
]


def should_exclude(type_name: str) -> bool:
    # SkieKotlin* types (SkieKotlinFlow, SkieKotlinStateFlow, etc.) are user-visible
    # Swift wrappers around Kotlin Flows — keep them.
    if type_name.startswith("SkieKotlin"):
        return False
    return any(re.search(p, type_name) for p in EXCLUDE_PATTERNS)


def extract_objc_type(precise: str) -> str | None:
    """Return the part of the ObjC type name after the SCSDK/SBBSDK prefix, or None."""
    m = re.match(r"c:objc\((?:cs|pl)\)(?:SCSDK|SBBSDK)(\w+)", precise)
    return m.group(1) if m else None


def filter_graph(graph: dict) -> tuple[dict, int]:
    symbols = graph.get("symbols", [])
    relationships = graph.get("relationships", [])

    # Pass 1: mark top-level excluded symbols.
    excluded: set[str] = set()
    for sym in symbols:
        precise = sym.get("identifier", {}).get("precise", "")
        type_name = extract_objc_type(precise)
        if type_name and should_exclude(type_name):
            excluded.add(precise)

    # Pass 2: cascade — also exclude any symbol that is a member of an excluded type.
    # Repeat until stable (handles nested companions, inner classes, etc.)
    member_of: dict[str, str] = {
        r["source"]: r["target"]
        for r in relationships
        if r.get("kind") == "memberOf"
    }
    changed = True
    while changed:
        changed = False
        for child, parent in member_of.items():
            if parent in excluded and child not in excluded:
                excluded.add(child)
                changed = True

    original_count = len(symbols)
    graph["symbols"] = [
        s for s in symbols
        if s.get("identifier", {}).get("precise", "") not in excluded
    ]
    graph["relationships"] = [
        r for r in relationships
        if r.get("source") not in excluded and r.get("target") not in excluded
    ]

    return graph, len(excluded)


def main() -> None:
    if len(sys.argv) != 3:
        print("Usage: filter-symbols.py <input.symbols.json> <output.symbols.json>")
        sys.exit(1)

    input_path, output_path = sys.argv[1], sys.argv[2]

    with open(input_path) as f:
        graph = json.load(f)

    total_before = len(graph.get("symbols", []))
    graph, removed = filter_graph(graph)
    total_after = len(graph.get("symbols", []))

    print(
        f"  {os.path.basename(input_path)}: "
        f"removed {removed} symbols ({total_after} remaining of {total_before})"
    )

    with open(output_path, "w") as f:
        json.dump(graph, f)


if __name__ == "__main__":
    main()
