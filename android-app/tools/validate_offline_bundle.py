#!/usr/bin/env python3
"""Validate the generated offline bundle before Android packaging."""
from __future__ import annotations

import argparse
import json
from collections import Counter
from pathlib import Path

EXPECTED_TIERS = {"SS", "S", "A+", "A", "B", "C"}


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--bundle-dir", required=True, type=Path)
    args = parser.parse_args()

    bundle_dir = args.bundle_dir.resolve()
    json_file = bundle_dir / "characters.json"
    if not json_file.is_file():
        raise SystemExit(f"Missing generated data file: {json_file}")

    payload = json.loads(json_file.read_text(encoding="utf-8"))
    characters = payload.get("characters", [])
    if not isinstance(characters, list) or not characters:
        raise SystemExit("Offline character list is empty or invalid.")
    if payload.get("characterCount") != len(characters):
        raise SystemExit("characterCount does not match the number of serialized characters.")

    slugs = [str(item.get("slug", "")).strip() for item in characters]
    if any(not slug for slug in slugs):
        raise SystemExit("Found a character without a slug.")
    duplicate_slugs = [slug for slug, count in Counter(slugs).items() if count > 1]
    if duplicate_slugs:
        raise SystemExit("Duplicate character slugs: " + ", ".join(sorted(duplicate_slugs)))

    tiers = {str(item.get("tier", "")).upper() for item in characters}
    missing_tiers = EXPECTED_TIERS - tiers
    if missing_tiers:
        raise SystemExit("Missing tier content: " + ", ".join(sorted(missing_tiers)))

    broken_images = []
    missing_images = 0
    for item in characters:
        image = str(item.get("image", "")).strip()
        if not image:
            missing_images += 1
            continue
        image_file = (bundle_dir / image).resolve()
        if bundle_dir not in image_file.parents or not image_file.is_file():
            broken_images.append(f"{item.get('slug', '?')}: {image}")

    if broken_images:
        raise SystemExit("Broken offline portrait references:\n" + "\n".join(broken_images))

    print(
        "Offline bundle valid: "
        f"{len(characters)} characters, tiers {', '.join(tier for tier in ('SS', 'S', 'A+', 'A', 'B', 'C') if tier in tiers)}, "
        f"{len(characters) - missing_images} bundled portraits, {missing_images} portrait placeholders."
    )


if __name__ == "__main__":
    main()
