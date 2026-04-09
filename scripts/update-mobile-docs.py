#!/usr/bin/env python3
"""
Copy generated mobile SDK MDX files into mintlify-docs and update docs.json.

Usage:
  python3 scripts/update-mobile-docs.py \
    --consumer-output source/sdks/sdk/consumer-headless/build/mintlify \
    --b2b-output source/sdks/sdk/b2b-headless/build/mintlify \
    --docs-repo /path/to/mintlify-docs
"""

import argparse
import json
import re
import shutil
import sys
from pathlib import Path



def find_mobile_sdks_tab(docs: dict, product_name: str) -> dict | None:
    """Return the 'Mobile SDKs' tab dict for the given product name."""
    for version in docs.get("navigation", {}).get("versions", []):
        for product in version.get("products", []):
            if product.get("product") == product_name:
                for tab in product.get("tabs", []):
                    if tab.get("tab") == "Mobile SDKs":
                        return tab
    return None


def find_dropdown(tab: dict, dropdown_name: str) -> dict | None:
    for dd in tab.get("dropdowns", []):
        if dd.get("dropdown") == dropdown_name:
            return dd
    return None


def replace_methods_group(pages: list, new_methods: list) -> list:
    """
    Replace the {'group': 'Methods', 'pages': [...]} entry in a pages list.
    Returns a new list with the Methods group replaced (or appended if not found).
    """
    replaced = False
    result = []
    for entry in pages:
        if isinstance(entry, dict) and entry.get("group") == "Methods":
            result.append({"group": "Methods", "pages": new_methods})
            replaced = True
        else:
            result.append(entry)
    if not replaced:
        result.append({"group": "Methods", "pages": new_methods})
    return result



def copy_tree(src: Path, dst: Path):
    """Copy src tree into dst, creating directories as needed."""
    if not src.exists():
        return
    for item in src.rglob("*"):
        if item.is_file():
            rel = item.relative_to(src)
            target = dst / rel
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(item, target)


def process_vertical(
    docs: dict,
    vertical: str,
    product_name: str,
    nav_patch: dict,
    output_dir: Path,
    docs_dir: Path,
):
    """Copy MDX files and update the nav for one vertical (consumer or b2b)."""
    # Copy snippets
    copy_tree(output_dir / "snippets", docs_dir / "snippets")

    tab = find_mobile_sdks_tab(docs, product_name)
    if tab is None:
        print(f"WARNING: could not find Mobile SDKs tab for {product_name}", file=sys.stderr)
        return

    platform_configs = [
        ("react-native", "React Native SDK"),
        ("android", "Android SDK"),
        ("ios", "iOS SDK"),
    ]

    for platform, dropdown_name in platform_configs:
        new_method_groups = nav_patch.get(vertical, {}).get(platform, [])

        # Only replace the methods/ subdirectory; leave overview/installation/etc intact
        platform_dst = docs_dir / f"api-reference/{vertical}/mobile-sdks/{platform}"
        methods_dst = platform_dst / "methods"
        if methods_dst.exists():
            shutil.rmtree(methods_dst)
        copy_tree(output_dir / f"api-reference/{vertical}/mobile-sdks/{platform}/methods", methods_dst)

        dropdown = find_dropdown(tab, dropdown_name)
        if dropdown is None:
            print(f"WARNING: dropdown '{dropdown_name}' not found in {product_name}", file=sys.stderr)
            continue

        dropdown["pages"] = replace_methods_group(dropdown.get("pages", []), new_method_groups)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--consumer-output", required=True)
    parser.add_argument("--b2b-output", required=True)
    parser.add_argument("--docs-repo", required=True)
    args = parser.parse_args()

    consumer_dir = Path(args.consumer_output)
    b2b_dir = Path(args.b2b_output)
    docs_repo = Path(args.docs_repo)
    docs_dir = docs_repo / "docs"
    docs_json_path = docs_dir / "docs.json"

    docs = json.loads(docs_json_path.read_text())

    for vertical, product_name, output_dir in [
        ("consumer", "Consumer API", consumer_dir),
        ("b2b", "B2B API", b2b_dir),
    ]:
        nav_patch_path = output_dir / f"nav-patch-{vertical}.json"
        if not nav_patch_path.exists():
            print(f"ERROR: nav-patch not found at {nav_patch_path}", file=sys.stderr)
            sys.exit(1)
        nav_patch = json.loads(nav_patch_path.read_text())
        process_vertical(docs, vertical, product_name, nav_patch, output_dir, docs_dir)

    docs_json_path.write_text(json.dumps(docs, indent=2, ensure_ascii=False) + "\n")
    print("docs.json updated")


if __name__ == "__main__":
    main()
