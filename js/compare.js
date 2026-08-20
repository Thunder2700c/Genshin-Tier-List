/**
 * Genshin DPS & Pull Value Archive — Comparison Engine
 */

const ARCHIVE_DATABASE = {
  "mavuika": {
    id: "mavuika",
    name: "Mavuika",
    element: "Pyro",
    weapon: "Claymore",
    tier: "SS",
    role: "On-Field Carry / Off-Field Enabler",
    emoji: "🔥",
    image: "assets/images/characters/mavuika.webp",
    pageUrl: "characters/mavuika.html",
    ratings: {
      overall: 5.0,
      singleTarget: 5.0,
      aoe: 5.0,
      burst: 5.0,
      sustained: 4.8,
      f2p: 4.5,
      ease: 4.2
    },
    specs: {
      sigReliance: "Medium (Has strong craftable Earth Shaker)",
      bestF2P: "Earth Shaker (Craftable) / Serpent Spine",
      artifactSet: "Obsidian Codex (4pc)",
      teamRole: "Vape / Melt / Nightsoul Anchor",
      contestedSupports: "Xilonen, Citlali, Bennett, Furina",
      abyssRole: "Universal On/Off-Field DPS"
    },
    verdict: "Pinnacle Pyro DPS with unprecedented sub-DPS utility. Should be prioritized over almost every single-target Pyro carry unless you already own hyper-invested alternatives."
  },
  "neuvillette": {
    id: "neuvillette",
    name: "Neuvillette",
    element: "Hydro",
    weapon: "Catalyst",
    tier: "SS",
    role: "Self-Sustaining Hypercarry",
    emoji: "💧",
    image: "assets/images/characters/neuvillette.webp",
    pageUrl: "characters/neuvillette.html",
    ratings: {
      overall: 5.0,
      singleTarget: 4.8,
      aoe: 5.0,
      burst: 4.6,
      sustained: 5.0,
      f2p: 5.0,
      ease: 5.0
    },
    specs: {
      sigReliance: "Low (Prototype Amber is elite)",
      bestF2P: "Prototype Amber (Craftable) / Sacrificial Jade",
      artifactSet: "Marechaussee Hunter (4pc)",
      teamRole: "Hypercarry / Solo Floor 12",
      contestedSupports: "Furina, Kazuha, Zhongli, Baizhu",
      abyssRole: "Highest Floor Sweeper"
    },
    verdict: "The highest floor in Genshin Impact. High AoE beam damage, self-healing, and craftable weapon scaling make him the safest investment on any account."
  },
  "arlecchino": {
    id: "arlecchino",
    name: "Arlecchino",
    element: "Pyro",
    weapon: "Polearm",
    tier: "S",
    role: "Sustained On-Field Normal Attacker",
    emoji: "🔥",
    image: "assets/images/characters/arlecchino.webp",
    pageUrl: "characters/arlecchino.html",
    ratings: {
      overall: 4.9,
      singleTarget: 5.0,
      aoe: 4.5,
      burst: 4.6,
      sustained: 5.0,
      f2p: 4.5,
      ease: 4.0
    },
    specs: {
      sigReliance: "Medium (White Tassel R5 is great)",
      bestF2P: "White Tassel (3★ R5) / Deathmatch",
      artifactSet: "Fragment of Harmonic Whimsy / Gladiator (4pc)",
      teamRole: "Vaporize / Chevreuse Overload Carry",
      contestedSupports: "Bennett, Chevreuse, Yelan, Kazuha",
      abyssRole: "Single-Target Boss Eraser"
    },
    verdict: "Elite sustained normal attacker with zero energy reliance. Redundant if you already own Mavuika or Hu Tao, but top-tier if you need a dedicated on-field boss killer."
  },
  "hu-tao": {
    id: "hu-tao",
    name: "Hu Tao",
    element: "Pyro",
    weapon: "Polearm",
    tier: "S",
    role: "Single-Target Hypercarry",
    emoji: "👻",
    image: "assets/images/characters/hutao.webp",
    pageUrl: "characters/hu-tao.html",
    ratings: {
      overall: 4.7,
      singleTarget: 5.0,
      aoe: 3.5,
      burst: 4.8,
      sustained: 4.5,
      f2p: 3.8,
      ease: 3.2
    },
    specs: {
      sigReliance: "High (Staff of Homa is massive)",
      bestF2P: "Dragon's Bane (R5) / Ballad of the Fjords",
      artifactSet: "Crimson Witch of Flames / Shimenawa (4pc)",
      teamRole: "Double Hydro Vaporize / Plunge",
      contestedSupports: "Yelan, Xingqiu, Furina, Xianyun, Zhongli",
      abyssRole: "Single-Target Vaporize Specialist"
    },
    verdict: "Phenomenal single-target damage ceiling, but mechanically demanding with stamina/jump-cancels at C0. Best reserved for players willing to master her execution."
  },
  "alhaitham": {
    id: "alhaitham",
    name: "Alhaitham",
    element: "Dendro",
    weapon: "Sword",
    tier: "S",
    role: "Reaction Driver / Quickbloom DPS",
    emoji: "🌱",
    image: "assets/images/characters/alhaitham.webp",
    pageUrl: "characters/alhaitham.html",
    ratings: {
      overall: 4.8,
      singleTarget: 4.8,
      aoe: 4.6,
      burst: 4.5,
      sustained: 4.9,
      f2p: 4.6,
      ease: 3.8
    },
    specs: {
      sigReliance: "Low (Iron Sting / Harbinger work well)",
      bestF2P: "Iron Sting (Craftable) / Toukabou Shigure",
      artifactSet: "Gilded Dreams / Deepwood Memories (4pc)",
      teamRole: "Spread / Quickbloom Driver",
      contestedSupports: "Nahida, Kuki Shinobu, Yelan, Furina",
      abyssRole: "Premier Dendro Core Driver"
    },
    verdict: "The undisputed anchor for Dendro reaction teams. Extremely high baseline DPS floor without needing signature weapons or high constellation investments."
  },
  "ayato": {
    id: "ayato",
    name: "Kamisato Ayato",
    element: "Hydro",
    weapon: "Sword",
    tier: "B",
    role: "Sustained On-Field Normal Attacker",
    emoji: "💧",
    image: "assets/images/characters/ayato.webp",
    pageUrl: "characters/ayato.html",
    ratings: {
      overall: 3.9,
      singleTarget: 3.8,
      aoe: 4.2,
      burst: 4.0,
      sustained: 4.0,
      f2p: 4.2,
      ease: 5.0
    },
    specs: {
      sigReliance: "Low (Amenoma / Lion's Roar / Isshin)",
      bestF2P: "Amenoma Kageuchi (Craftable) / The Black Sword",
      artifactSet: "Heart of Depth / Gladiator (4pc)",
      teamRole: "Hyperbloom Driver / Mono Hydro / Taser",
      contestedSupports: "Fischl, Kuki Shinobu, Bennett, Yunjin",
      abyssRole: "Flexible Reaction Normal Attacker"
    },
    verdict: "Very simple and comfortable playstyle, but directly outscaled in raw numbers and utility by Neuvillette. Pull primarily for playstyle preference."
  },
  "cyno": {
    id: "cyno",
    name: "Cyno",
    element: "Electro",
    weapon: "Polearm",
    tier: "C",
    role: "Extended On-Field Quickbloom Carry",
    emoji: "🐺",
    image: "assets/images/characters/cyno.webp",
    pageUrl: "characters/cyno.html",
    ratings: {
      overall: 3.6,
      singleTarget: 4.0,
      aoe: 3.6,
      burst: 4.4,
      sustained: 3.8,
      f2p: 3.5,
      ease: 3.5
    },
    specs: {
      sigReliance: "Medium (Staff of the Scarlet Sands)",
      bestF2P: "White Tassel (3★) / Missive Windspear",
      artifactSet: "Gilded Dreams / Thundering Fury (4pc)",
      teamRole: "Extended Quickbloom Carry",
      contestedSupports: "Nahida, Baizhu, Furina, Xingqiu",
      abyssRole: "Long-Rotation Hypercarry"
    },
    verdict: "High damage potential during burst, but plagued by an overly long field time that causes support buffs to expire mid-rotation. Demands specialized teammates."
  }
};

document.addEventListener('DOMContentLoaded', () => {
  const selectA = document.getElementById('selectCharA');
  const selectB = document.getElementById('selectCharB');
  const comparisonView = document.getElementById('comparisonView');
  const presetButtons = document.querySelectorAll('.preset-btn');

  // Populate Dropdowns
  function populateDropdowns() {
    const keys = Object.keys(ARCHIVE_DATABASE);
    keys.forEach((key, index) => {
      const char = ARCHIVE_DATABASE[key];
      const optA = new Option(`${char.name} (${char.tier} Tier • ${char.element})`, key);
      const optB = new Option(`${char.name} (${char.tier} Tier • ${char.element})`, key);
      
      selectA.add(optA);
      selectB.add(optB);
    });

    // Default Selection: Mavuika vs Arlecchino
    selectA.value = 'mavuika';
    selectB.value = 'arlecchino';
  }

  // Render Function
  function renderComparison() {
    const charA = ARCHIVE_DATABASE[selectA.value];
    const charB = ARCHIVE_DATABASE[selectB.value];

    if (!charA || !charB) return;

    // Check Overlap
    const isSameElement = charA.element === charB.element;
    const isSameRole = charA.role.toLowerCase().includes('carry') && charB.role.toLowerCase().includes('carry');
    const isDirectRedundancy = isSameElement && isSameRole;

    comparisonView.innerHTML = `
      <!-- 1. Side-by-Side Hero Banners -->
      <div class="comparison-hero-grid">
        <div class="comp-hero-card">
          <div class="comp-hero-left">
            <div class="comp-avatar">
              <img src="${charA.image}" alt="${charA.name}" onerror="this.parentElement.innerHTML='${charA.emoji}'">
            </div>
            <div>
              <div class="comp-meta">${charA.element} • ${charA.weapon}</div>
              <h2 class="comp-name">${charA.name}</h2>
              <div class="comp-meta">${charA.role}</div>
            </div>
          </div>
          <span class="comp-tier-badge tier-${charA.tier.toLowerCase()}">${charA.tier} TIER</span>
        </div>

        <div class="comp-hero-card">
          <div class="comp-hero-left">
            <div class="comp-avatar">
              <img src="${charB.image}" alt="${charB.name}" onerror="this.parentElement.innerHTML='${charB.emoji}'">
            </div>
            <div>
              <div class="comp-meta">${charB.element} • ${charB.weapon}</div>
              <h2 class="comp-name">${charB.name}</h2>
              <div class="comp-meta">${charB.role}</div>
            </div>
          </div>
          <span class="comp-tier-badge tier-${charB.tier.toLowerCase()}">${charB.tier} TIER</span>
        </div>
      </div>

      <!-- 2. Role Overlap Banner (If applicable) -->
      ${isDirectRedundancy ? `
        <div class="overlap-box">
          <div class="overlap-title">
            <span>⚠️</span> Account Role Redundancy Detected
          </div>
          <p class="overlap-text">Both <strong>${charA.name}</strong> and <strong>${charB.name}</strong> compete for the exact same <strong>On-Field ${charA.element} Carry</strong> slot in Spiral Abyss Floor 12. If your account already owns one at full investment, pulling the other is a lateral upgrade / luxury choice rather than a roster expansion.</p>
        </div>
      ` : ''}

      <!-- 3. Combat Ratings Comparison -->
      <section class="section-block">
        <h2>⚔️ Combat Power & Usability Metrics</h2>
        <div class="metrics-comparison-list">
          ${renderMetricBar('Overall DPS Ceiling', charA.ratings.overall, charB.ratings.overall, charA.name, charB.name)}
          ${renderMetricBar('Single-Target Bossing', charA.ratings.singleTarget, charB.ratings.singleTarget, charA.name, charB.name)}
          ${renderMetricBar('AoE Mob Coverage', charA.ratings.aoe, charB.ratings.aoe, charA.name, charB.name)}
          ${renderMetricBar('Burst / Frontloaded Damage', charA.ratings.burst, charB.ratings.burst, charA.name, charB.name)}
          ${renderMetricBar('Sustained Rotation Output', charA.ratings.sustained, charB.ratings.sustained, charA.name, charB.name)}
          ${renderMetricBar('F2P Friendliness (Weapon/Artifacts)', charA.ratings.f2p, charB.ratings.f2p, charA.name, charB.name)}
          ${renderMetricBar('Ease of Execution / Gameplay', charA.ratings.ease, charB.ratings.ease, charA.name, charB.name)}
        </div>
      </section>

      <!-- 4. Detailed Specification Table -->
      <section class="section-block">
        <h2>📊 Build, Weapons & Support Breakdown</h2>
        <div class="data-table-wrap">
          <table class="data-table">
            <thead>
              <tr>
                <th style="width: 25%;">Evaluation Metric</th>
                <th style="width: 37.5%; color: #38bdf8;">${charA.name}</th>
                <th style="width: 37.5%; color: #f59e0b;">${charB.name}</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td class="highlight-row">Signature Dependency</td>
                <td>${charA.specs.sigReliance}</td>
                <td>${charB.specs.sigReliance}</td>
              </tr>
              <tr>
                <td class="highlight-row">Top F2P / Craftable Weapon</td>
                <td>${charA.specs.bestF2P}</td>
                <td>${charB.specs.bestF2P}</td>
              </tr>
              <tr>
                <td class="highlight-row">Best Artifact Set</td>
                <td>${charA.specs.artifactSet}</td>
                <td>${charB.specs.artifactSet}</td>
              </tr>
              <tr>
                <td class="highlight-row">Contested Supports</td>
                <td>${charA.specs.contestedSupports}</td>
                <td>${charB.specs.contestedSupports}</td>
              </tr>
              <tr>
                <td class="highlight-row">Primary Abyss Role</td>
                <td>${charA.specs.abyssRole}</td>
                <td>${charB.specs.abyssRole}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- 5. Which Should You Pull? Synthesis -->
      <section class="section-block">
        <h2>💡 Head-to-Head Pull Verdict</h2>
        <div class="verdict-grid">
          <div class="verdict-card">
            <h3>When to Pull ${charA.name}</h3>
            <p>${charA.verdict}</p>
            <a href="${charA.pageUrl}" class="view-full-btn">Read Full ${charA.name} Analysis →</a>
          </div>
          <div class="verdict-card">
            <h3>When to Pull ${charB.name}</h3>
            <p>${charB.verdict}</p>
            <a href="${charB.pageUrl}" class="view-full-btn">Read Full ${charB.name} Analysis →</a>
          </div>
        </div>
      </section>
    `;
  }

  function renderMetricBar(label, valA, valB, nameA, nameB) {
    const pctA = Math.round((valA / 5.0) * 100);
    const pctB = Math.round((valB / 5.0) * 100);

    return `
      <div class="metric-bar-group">
        <div class="metric-label-row">
          <span>${nameA}: <strong>${valA.toFixed(1)}</strong></span>
          <span style="color: var(--text-main); font-weight: 700;">${label}</span>
          <span>${nameB}: <strong>${valB.toFixed(1)}</strong></span>
        </div>
        <div class="metric-bars-track">
          <div class="bar-wrap">
            <div class="bar-fill bar-fill-a" style="width: ${pctA}%;"></div>
          </div>
          <div class="bar-wrap">
            <div class="bar-fill bar-fill-b" style="width: ${pctB}%;"></div>
          </div>
        </div>
      </div>
    `;
  }

  // Event Listeners
  selectA.addEventListener('change', renderComparison);
  selectB.addEventListener('change', renderComparison);

  presetButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const c1 = btn.getAttribute('data-char1');
      const c2 = btn.getAttribute('data-char2');
      if (c1 && c2 && ARCHIVE_DATABASE[c1] && ARCHIVE_DATABASE[c2]) {
        selectA.value = c1;
        selectB.value = c2;
        renderComparison();
      }
    });
  });

  // Init
  populateDropdowns();
  renderComparison();
});
