"""Smoke tests for migration from the website's static tier-card markup."""
import unittest
import tempfile
from pathlib import Path

from import_website_data import TierCardParser, detail_portrait_hint


class TierCardParserTests(unittest.TestCase):

    def test_detail_poster_is_read_from_character_profile(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / "tiers").mkdir()
            profiles = root / "characters"
            profiles.mkdir()
            profile = profiles / "demo-character.html"
            profile.write_text(
                '<div class="char-portrait-frame">'
                '<img src="../assets/images/characters/demo-character-1.webp" alt="Demo">'
                '</div>',
                encoding="utf-8",
            )

            self.assertEqual(
                detail_portrait_hint(root, "../characters/demo-character.html"),
                "demo-character-1.webp",
            )

    def test_detail_poster_import_rejects_path_outside_source_root(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            outside = root.parent / "outside-profile.html"
            self.assertEqual(detail_portrait_hint(root, "../../outside-profile.html"), "")

    def test_nested_character_card_fields_are_preserved(self):
        html = """
        <a href="../characters/demo-character.html" class="char-card"
           data-name="demo character" data-element="cryo">
          <div class="char-avatar-wrap">
            <img src="../assets/images/characters/demo-character.webp" alt="Demo Character">
          </div>
          <span class="rarity-tag">★★★★★</span>
          <span class="weapon-role-tag">• Sword (C0)</span>
          <h2 class="char-name">Demo Character</h2>
          <div class="metric-score"><span class="stars">★★★★☆</span> 4.8</div>
          <span class="pull-badge">4/5 • Test Pull Value</span>
          <p class="char-card-summary">A sample <strong>nested</strong> summary.</p>
          <div class="char-card-footer">
            <span>Primary Role: Test Carry</span>
            <span>Full Analysis →</span>
          </div>
        </a>
        """

        parser = TierCardParser("SS")
        parser.feed(html)

        self.assertEqual(len(parser.cards), 1)
        character = parser.cards[0]
        self.assertEqual(character["slug"], "demo-character")
        self.assertEqual(character["name"], "Demo Character")
        self.assertEqual(character["tier"], "SS")
        self.assertEqual(character["element"], "cryo")
        self.assertEqual(character["rarity"], 5)
        self.assertEqual(character["weapon"], "Sword")
        self.assertEqual(character["constellation"], "C0")
        self.assertEqual(character["combatPower"], "4.8")
        self.assertEqual(character["pullValue"], "4/5 • Test Pull Value")
        self.assertEqual(character["summary"], "A sample nested summary.")
        self.assertEqual(character["role"], "Test Carry")
        self.assertEqual(character["sourceImage"], "demo-character.webp")


if __name__ == "__main__":
    unittest.main()
