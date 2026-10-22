#!/usr/bin/env python3
"""Convert the existing static tier pages into a compact offline Android asset bundle.

Only Python's standard library is required. The app never loads the hosted website at
runtime: this script copies local character portraits and writes characters.json during
the Android build.
"""
from __future__ import annotations

import argparse
import json
import re
import shutil
from html.parser import HTMLParser
from pathlib import Path

TIER_FILES = [
    ("ss-tier.html", "SS"),
    ("s-tier.html", "S"),
    ("a-plus-tier.html", "A+"),
    ("a-tier.html", "A"),
    ("b-tier.html", "B"),
    ("c-tier.html", "C"),
]
VOID_TAGS = {"area", "base", "br", "col", "embed", "hr", "img", "input",
             "link", "meta", "param", "source", "track", "wbr"}
FIELD_CLASSES = {
    "char-name": "name",
    "char-card-summary": "summary",
    "pull-badge": "pullValue",
    "metric-score": "combatPower",
    "char-card-footer": "footer",
    "weapon-role-tag": "weaponRole",
    "rarity-tag": "rarityText",
}


def clean(value: str) -> str:
    return re.sub(r"\s+", " ", value).strip()


def slug_from_href(href: str, fallback: str) -> str:
    candidate = Path(href.split("?", 1)[0]).stem if href else ""
    if candidate and candidate not in {".", ".."}:
        return candidate.lower()
    return re.sub(r"[^a-z0-9]+", "-", fallback.lower()).strip("-")


class TierCardParser(HTMLParser):
    def __init__(self, tier: str) -> None:
        super().__init__(convert_charrefs=True)
        self.tier = tier
        self.card: dict | None = None
        self.stack: list[str] = []
        self.fields: list[dict] = []
        self.cards: list[dict] = []

    def handle_starttag(self, tag: str, attrs) -> None:
        attrs = dict(attrs)
        classes = set((attrs.get("class") or "").split())

        if self.card is None:
            if tag == "a" and "char-card" in classes:
                href = attrs.get("href", "")
                name_hint = attrs.get("data-name", "")
                self.card = {
                    "tier": self.tier,
                    "slug": slug_from_href(href, name_hint or "character"),
                    "element": (attrs.get("data-element") or "").lower(),
                    "rarityAttribute": attrs.get("data-rarity", ""),
                    "href": href,
                    "imageSource": "",
                    "fields": {},
                }
                self.stack = ["a"]
                self.fields = []
            return

        if tag == "img":
            src = attrs.get("src", "")
            match = re.search(r"(?:^|/)characters/([^/?#]+\.(?:webp|png|jpe?g))", src, re.I)
            if match and not self.card["imageSource"]:
                self.card["imageSource"] = match.group(1)

        depth = len(self.stack) + (0 if tag in VOID_TAGS else 1)
        for class_name, field_name in FIELD_CLASSES.items():
            if class_name in classes and tag not in VOID_TAGS:
                self.fields.append({
                    "field": field_name,
                    "tag": tag,
                    "depth": depth,
                    "text": "",
                })
        if tag not in VOID_TAGS:
            self.stack.append(tag)

    def handle_data(self, data: str) -> None:
        if self.card is None:
            return
        for field in self.fields:
            field["text"] += data

    def handle_endtag(self, tag: str) -> None:
        if self.card is None or not self.stack:
            return

        depth = len(self.stack)
        completed = [
            field for field in self.fields
            if field["tag"] == tag and field["depth"] == depth
        ]
        for field in completed:
            value = clean(field["text"])
            if value:
                self.card["fields"][field["field"]] = value
            self.fields.remove(field)

        if tag == "a" and depth == 1:
            self._finish_card()
            return

        # Recover gracefully from minor malformed markup in hand-edited static pages.
        if tag in self.stack:
            index = len(self.stack) - 1 - self.stack[::-1].index(tag)
            self.stack = self.stack[:index]

    def _finish_card(self) -> None:
        if self.card is None:
            return
        card = self.card
        fields = card.pop("fields", {})
        name = fields.get("name") or card["slug"].replace("-", " ").title()
        rarity_text = fields.get("rarityText", "")
        rarity_match = re.search(r"(\d)", card.get("rarityAttribute", ""))
        star_count = rarity_text.count("★")
        rarity = int(rarity_match.group(1)) if rarity_match else (4 if star_count == 4 else 5)

        metric = fields.get("combatPower", "")
        score_matches = re.findall(r"\d+(?:\.\d+)?", metric)
        combat_power = score_matches[-1] if score_matches else ""

        weapon_role = fields.get("weaponRole", "").replace("•", "").strip()
        role_match = re.match(r"([A-Za-z-]+)\s*(?:\((C\d+)\))?", weapon_role)
        weapon = role_match.group(1) if role_match else weapon_role
        constellation = role_match.group(2) if role_match and role_match.group(2) else "C0"

        footer = fields.get("footer", "")
        role_text = re.search(r"Primary Role:\s*(.*?)(?:\s*Full Analysis\b|$)", footer, re.I)
        role = role_text.group(1).strip() if role_text else ""

        pull_value = fields.get("pullValue", "")
        summary = fields.get("summary", "")
        card.update({
            "name": name,
            "rarity": rarity,
            "weapon": weapon,
            "constellation": constellation,
            "combatPower": combat_power,
            "pullValue": pull_value,
            "summary": summary,
            "role": role,
            "image": "",
            "sourceImage": card.get("imageSource", ""),
            "detailPath": card.get("href", ""),
        })
        card.pop("rarityAttribute", None)
        card.pop("imageSource", None)
        self.cards.append(card)
        self.card = None
        self.stack = []
        self.fields = []


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source-root", required=True, type=Path)
    parser.add_argument("--output-dir", required=True, type=Path)
    args = parser.parse_args()

    source_root = args.source_root.resolve()
    output_dir = args.output_dir.resolve()
    portraits_source = source_root / "assets" / "images" / "characters"
    portraits_output = output_dir / "characters"
    output_dir.mkdir(parents=True, exist_ok=True)
    portraits_output.mkdir(parents=True, exist_ok=True)

    characters: list[dict] = []
    seen: set[str] = set()

    tiers_dir = source_root / "tiers"
    for filename, tier in TIER_FILES:
        page = tiers_dir / filename
        if not page.is_file():
            print(f"Skipping missing tier page: {page}")
            continue
        card_parser = TierCardParser(tier)
        card_parser.feed(page.read_text(encoding="utf-8", errors="replace"))
        for character in card_parser.cards:
            slug = character["slug"]
            if slug in seen:
                continue
            seen.add(slug)

            # The tier page stores a website-relative portrait path. Import that file
            # into the generated APK assets so portraits are available without a network.
            source_parser = Path(character.get("detailPath") or "")
            fields_image = ""
            # Match by the source card's character slug first; this mirrors the site's
            # current naming convention and avoids relying on remote URLs.
            candidates = []
            if portraits_source.is_dir():
                image_hint = Path(character.get("sourceImage") or "").name
                if image_hint:
                    hinted = portraits_source / image_hint
                    if hinted.is_file():
                        candidates.append(hinted)
                if not candidates:
                    # Some detail pages use a -1 suffix for the only available portrait.
                    for stem in (slug, slug + "-1"):
                        for ext in (".webp", ".png", ".jpg", ".jpeg"):
                            candidate = portraits_source / (stem + ext)
                            if candidate.is_file():
                                candidates.append(candidate)
                        if candidates:
                            break
                if not candidates:
                    candidates = [
                        p for p in portraits_source.iterdir()
                        if p.is_file() and p.stem.lower() == slug.lower()
                    ]
            character.pop("sourceImage", None)
            if candidates:
                chosen = candidates[0]
                fields_image = chosen.name
                destination = portraits_output / chosen.name
                if not destination.exists() or destination.stat().st_size != chosen.stat().st_size:
                    shutil.copy2(chosen, destination)
                character["image"] = "characters/" + fields_image
            else:
                character["image"] = ""
            characters.append(character)

    payload = {
        "schemaVersion": 1,
        "source": "Thunder2700c/Genshin-Tier-List",
        "characterCount": len(characters),
        "characters": characters,
    }
    (output_dir / "characters.json").write_text(
        json.dumps(payload, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    print(f"Imported {len(characters)} characters and bundled {len(list(portraits_output.iterdir()))} portraits.")
    if not characters:
        raise SystemExit("No character cards found. Check the website tier HTML structure.")


if __name__ == "__main__":
    main()
