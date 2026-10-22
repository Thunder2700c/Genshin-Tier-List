package com.thunder2700c.genshindpsarchive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

private val Void = Color(0xFF070910)
private val SurfaceOne = Color(0xFF111725)
private val SurfaceTwo = Color(0xFF171F30)
private val Hairline = Color(0xFF293449)
private val TextMain = Color(0xFFF4F6FC)
private val TextMuted = Color(0xFF9BA8BE)
private val Gold = Color(0xFFF5BD55)
private val Cyan = Color(0xFF5DD7E8)
private val TierOrder = listOf("SS", "S", "A+", "A", "B", "C")
private val Elements = listOf("All", "Pyro", "Hydro", "Electro", "Dendro", "Cryo", "Anemo", "Geo")

private fun tierColor(tier: String): Color = when (tier.uppercase()) {
    "SS" -> Color(0xFFFFC45D)
    "S" -> Color(0xFFFF676F)
    "A+" -> Color(0xFFFF974D)
    "A" -> Color(0xFFB48CFF)
    "B" -> Color(0xFF69A7FF)
    "C" -> Color(0xFF4DDBA2)
    else -> Cyan
}

private fun tierRank(tier: String): Int = TierOrder.indexOf(tier.uppercase()).let {
    if (it < 0) TierOrder.size else it
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Gold,
                secondary = Cyan,
                background = Void,
                surface = SurfaceOne,
                onPrimary = Void,
                onBackground = TextMain,
                onSurface = TextMain
            )) {
                GenshinArchiveApp()
            }
        }
    }
}

@Composable
private fun GenshinArchiveApp() {
    val context = LocalContext.current
    val characters by produceState(initialValue = emptyList<Character>()) {
        value = withContext(Dispatchers.IO) { CharacterRepository.load(context) }
    }
    var tab by rememberSaveable { mutableStateOf("HOME") }
    var chosenTier by rememberSaveable { mutableStateOf("ALL") }
    var chosenElement by rememberSaveable { mutableStateOf("All") }
    var query by rememberSaveable { mutableStateOf("") }
    var selectedSlug by rememberSaveable { mutableStateOf<String?>(null) }
    var favorites by remember { mutableStateOf(emptySet<String>()) }
    var owned by remember { mutableStateOf(emptySet<String>()) }

    LaunchedEffect(Unit) {
        favorites = PreferenceStore.read(context, "favorites")
        owned = PreferenceStore.read(context, "owned")
    }

    fun toggleFavorite(slug: String) {
        val next = if (favorites.contains(slug)) favorites - slug else favorites + slug
        favorites = next
        PreferenceStore.write(context, "favorites", next)
    }

    fun toggleOwned(slug: String) {
        val next = if (owned.contains(slug)) owned - slug else owned + slug
        owned = next
        PreferenceStore.write(context, "owned", next)
    }

    BackHandler(enabled = selectedSlug != null) {
        selectedSlug = null
    }

    val selectedCharacter = characters.firstOrNull { it.slug == selectedSlug }

    Scaffold(
        containerColor = Void,
        bottomBar = {
            if (selectedCharacter == null) {
                NavigationBar(containerColor = Color(0xFF0D121D), contentColor = TextMain) {
                    NavigationBarItem(
                        selected = tab == "HOME",
                        onClick = { tab = "HOME" },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = tab == "ROSTER",
                        onClick = { tab = "ROSTER" },
                        icon = { Icon(Icons.Filled.Search, contentDescription = "Tier list") },
                        label = { Text("Tier list") }
                    )
                    NavigationBarItem(
                        selected = tab == "SAVED",
                        onClick = { tab = "SAVED" },
                        icon = { Icon(Icons.Filled.Bookmark, contentDescription = "Saved") },
                        label = { Text("My roster") }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFF0A0E18), Void, Color(0xFF080B12))))
        ) {
            when {
                selectedCharacter != null -> CharacterDetailScreen(
                    character = selectedCharacter,
                    isFavorite = favorites.contains(selectedCharacter.slug),
                    isOwned = owned.contains(selectedCharacter.slug),
                    onBack = { selectedSlug = null },
                    onToggleFavorite = { toggleFavorite(selectedCharacter.slug) },
                    onToggleOwned = { toggleOwned(selectedCharacter.slug) }
                )
                tab == "HOME" -> HomeScreen(
                    characters = characters,
                    favorites = favorites,
                    owned = owned,
                    onOpenTier = {
                        chosenTier = it
                        tab = "ROSTER"
                    },
                    onOpenCharacter = { selectedSlug = it },
                    onOpenRoster = { tab = "ROSTER" },
                    onToggleFavorite = { toggleFavorite(it) }
                )
                tab == "ROSTER" -> RosterScreen(
                    characters = characters,
                    selectedTier = chosenTier,
                    onTierChange = { chosenTier = it },
                    selectedElement = chosenElement,
                    onElementChange = { chosenElement = it },
                    query = query,
                    onQueryChange = { query = it },
                    favorites = favorites,
                    onOpenCharacter = { selectedSlug = it },
                    onToggleFavorite = { toggleFavorite(it) }
                )
                else -> SavedScreen(
                    characters = characters,
                    favorites = favorites,
                    owned = owned,
                    onOpenCharacter = { selectedSlug = it },
                    onToggleFavorite = { toggleFavorite(it) },
                    onToggleOwned = { toggleOwned(it) }
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(
    characters: List<Character>,
    favorites: Set<String>,
    owned: Set<String>,
    onOpenTier: (String) -> Unit,
    onOpenCharacter: (String) -> Unit,
    onOpenRoster: () -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val ranked = remember(characters) {
        characters.sortedWith(compareBy<Character>({ tierRank(it.tier) }, { -(it.combatPower.toFloatOrNull() ?: 0f) }, { it.name }))
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(9.dp).clip(RoundedCornerShape(50)).background(Color(0xFF4DDBA2)))
                    Text("ARCHIVE  /  OFFLINE READY", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                }
                Text("V7.0", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("GENSHIN DPS", fontSize = 34.sp, lineHeight = 38.sp, fontWeight = FontWeight.Black, color = TextMain, letterSpacing = (-1.2).sp)
                Text("ARCHIVE", fontSize = 34.sp, lineHeight = 38.sp, fontWeight = FontWeight.Black, color = Cyan, letterSpacing = (-1.2).sp)
                Spacer(Modifier.height(7.dp))
                Text(
                    "Combat rankings, realistic baselines, and account-aware pull value — built to work offline.",
                    color = TextMuted,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }
        }
        item {
            Surface(
                color = Color(0xFF211D17),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color(0xFF6B5129).copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(17.dp))
                        Text("CORE RULE  /  POWER ≠ PULL VALUE", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                    }
                    Text(
                        "A top-tier carry can still be a low-priority pull when your account already has the same role covered.",
                        color = Color(0xFFE8D9B7),
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                MetricTile("CHARACTERS", characters.size.toString(), Gold, Modifier.weight(1f))
                MetricTile("SAVED", favorites.size.toString(), Cyan, Modifier.weight(1f))
                MetricTile("OWNED", owned.size.toString(), Color(0xFF65D79D), Modifier.weight(1f))
            }
        }
        item {
            SectionHeading("TIER INDEX", "Browse by combat ranking", onAction = onOpenRoster)
        }
        items(TierOrder.chunked(2)) { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                pair.forEach { tier ->
                    val count = characters.count { it.tier.equals(tier, ignoreCase = true) }
                    TierTile(tier = tier, count = count, modifier = Modifier.weight(1f), onClick = { onOpenTier(tier) })
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        item {
            SectionHeading("FEATURED ROSTER", "From the current archive", onAction = onOpenRoster)
        }
        if (ranked.isEmpty()) {
            item {
                EmptyState(
                    title = "Content is being prepared",
                    message = "The build imports character rankings and portraits from the website in this repository."
                )
            }
        } else {
            items(ranked.take(6), key = { it.slug }) { character ->
                CharacterRow(
                    character = character,
                    isFavorite = favorites.contains(character.slug),
                    isOwned = owned.contains(character.slug),
                    onClick = { onOpenCharacter(character.slug) },
                    onToggleFavorite = { onToggleFavorite(character.slug) }
                )
            }
        }
        item {
            Text(
                "Independent theorycrafting project. Not affiliated with HoYoverse.",
                color = TextMuted.copy(alpha = 0.75f),
                fontSize = 10.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun MetricTile(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceOne),
        border = BorderStroke(1.dp, Hairline)
    ) {
        Column(Modifier.padding(vertical = 14.dp, horizontal = 11.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(value, color = accent, fontSize = 23.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.7.sp, maxLines = 1)
        }
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String, onAction: (() -> Unit)? = null) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.7.sp)
            Text(subtitle, color = TextMuted, fontSize = 11.sp)
        }
        if (onAction != null) {
            Text("VIEW  →", color = Cyan, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onAction).padding(7.dp))
        }
    }
}

@Composable
private fun TierTile(tier: String, count: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val accent = tierColor(tier)
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceOne),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.48f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.12f), Color.Transparent)))
                .padding(15.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(tier, color = accent, fontSize = 25.sp, fontWeight = FontWeight.Black)
                    Text(count.toString().padStart(2, '0'), color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    when (tier) {
                        "SS" -> "EXCEPTIONAL"
                        "S" -> "TOP TIER"
                        "A+" -> "CONDITIONAL PEAK"
                        "A" -> "VERY STRONG"
                        "B" -> "VIABLE"
                        else -> "NICHE"
                    },
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    maxLines = 1
                )
                Box(Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(8.dp)).background(Hairline)) {
                    val fraction = (6 - tierRank(tier)).coerceAtLeast(1) / 6f
                    Box(Modifier.fillMaxWidth(fraction).height(3.dp).clip(RoundedCornerShape(8.dp)).background(accent))
                }
            }
        }
    }
}

@Composable
private fun RosterScreen(
    characters: List<Character>,
    selectedTier: String,
    onTierChange: (String) -> Unit,
    selectedElement: String,
    onElementChange: (String) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    favorites: Set<String>,
    onOpenCharacter: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val filtered = remember(characters, selectedTier, selectedElement, query) {
        characters.filter { character ->
            (selectedTier == "ALL" || character.tier.equals(selectedTier, ignoreCase = true)) &&
                (selectedElement == "All" || character.element.equals(selectedElement, ignoreCase = true)) &&
                (query.isBlank() || listOf(character.name, character.element, character.weapon, character.role, character.summary)
                    .any { it.contains(query.trim(), ignoreCase = true) })
        }.sortedWith(compareBy<Character>({ tierRank(it.tier) }, { it.name.lowercase() }))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 22.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("CHARACTER DIRECTORY", color = Cyan, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp)
                Text("Tier list", color = TextMain, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Text(filtered.size.toString() + " characters in this view", color = TextMuted, fontSize = 12.sp)
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Search characters, roles, elements…", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Cyan) },
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cyan,
                    unfocusedBorderColor = Hairline,
                    focusedTextColor = TextMain,
                    unfocusedTextColor = TextMain,
                    cursorColor = Gold,
                    focusedContainerColor = SurfaceOne,
                    unfocusedContainerColor = SurfaceOne
                )
            )
        }
        item {
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                FilterChip(
                    selected = selectedTier == "ALL",
                    onClick = { onTierChange("ALL") },
                    label = { Text("All tiers") }
                )
                TierOrder.forEach { tier ->
                    FilterChip(
                        selected = selectedTier == tier,
                        onClick = { onTierChange(if (selectedTier == tier) "ALL" else tier) },
                        label = { Text("$tier tier") }
                    )
                }
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Elements.forEach { element ->
                    FilterChip(
                        selected = selectedElement == element,
                        onClick = { onElementChange(element) },
                        label = { Text(element) }
                    )
                }
            }
        }
        if (filtered.isEmpty()) {
            item {
                EmptyState(
                    title = "No matching characters",
                    message = "Try another name, tier, or element filter. The directory works offline."
                )
            }
        } else {
            items(filtered, key = { it.slug }) { character ->
                CharacterRow(
                    character = character,
                    isFavorite = favorites.contains(character.slug),
                    isOwned = false,
                    onClick = { onOpenCharacter(character.slug) },
                    onToggleFavorite = { onToggleFavorite(character.slug) }
                )
            }
        }
    }
}

@Composable
private fun SavedScreen(
    characters: List<Character>,
    favorites: Set<String>,
    owned: Set<String>,
    onOpenCharacter: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleOwned: (String) -> Unit
) {
    val saved = remember(characters, favorites, owned) {
        characters.filter { favorites.contains(it.slug) || owned.contains(it.slug) }
            .sortedWith(compareBy<Character>({ tierRank(it.tier) }, { it.name.lowercase() }))
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("YOUR ACCOUNT", color = Cyan, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp)
                Text("My roster", color = TextMain, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Text("Your choices stay on this device.", color = TextMuted, fontSize = 12.sp)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                MetricTile("FAVOURITES", favorites.size.toString(), Gold, Modifier.weight(1f))
                MetricTile("OWNED", owned.size.toString(), Color(0xFF65D79D), Modifier.weight(1f))
            }
        }
        if (saved.isEmpty()) {
            item {
                EmptyState(
                    title = "Start building your roster",
                    message = "Open any character and mark it as a favourite or owned. Your list will remain available offline."
                )
            }
        } else {
            items(saved, key = { it.slug }) { character ->
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    CharacterRow(
                        character = character,
                        isFavorite = favorites.contains(character.slug),
                        isOwned = owned.contains(character.slug),
                        onClick = { onOpenCharacter(character.slug) },
                        onToggleFavorite = { onToggleFavorite(character.slug) }
                    )
                    Button(
                        onClick = { onToggleOwned(character.slug) },
                        colors = ButtonDefaults.buttonColors(containerColor = if (owned.contains(character.slug)) Color(0xFF1C4937) else SurfaceTwo),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(if (owned.contains(character.slug)) "Owned ✓" else "Mark owned", fontSize = 11.sp, color = if (owned.contains(character.slug)) Color(0xFF91EDB8) else TextMain)
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterDetailScreen(
    character: Character,
    isFavorite: Boolean,
    isOwned: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleOwned: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextMain)
                }
                Text("CHARACTER INTELLIGENCE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceOne,
                border = BorderStroke(1.dp, tierColor(character.tier).copy(alpha = 0.65f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(listOf(tierColor(character.tier).copy(alpha = 0.18f), SurfaceOne)))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CharacterPortrait(character.detailImage, character.name, Modifier.size(118.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        TierBadge(character.tier)
                        Text(character.name, color = TextMain, fontSize = 22.sp, lineHeight = 25.sp, fontWeight = FontWeight.Black)
                        Text(character.element.replaceFirstChar { it.uppercase() } + "  •  " + character.weapon, color = TextMuted, fontSize = 12.sp)
                        val stars = "★".repeat(character.rarity.coerceIn(1, 5))
                        Text(stars + "  " + character.constellation, color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            SectionHeading("PERFORMANCE SNAPSHOT", "Values imported from the source tier cards")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                MetricTile("COMBAT POWER", character.combatPower.ifBlank { "—" }, tierColor(character.tier), Modifier.weight(1f))
                MetricTile("PULL VALUE", character.pullValue.substringBefore("•").trim().ifBlank { "—" }, Gold, Modifier.weight(1f))
            }
            if (character.pullValue.contains("•")) {
                Text(character.pullValue.substringAfter("•").trim(), color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("FIELD NOTES", color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                Surface(color = SurfaceOne, shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Hairline)) {
                    Text(
                        character.summary.ifBlank { "No summary was present on the source tier card yet." },
                        color = TextMain,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                if (character.role.isNotBlank()) {
                    Text("PRIMARY ROLE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(character.role, color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        item {
            Button(
                onClick = onToggleFavorite,
                colors = ButtonDefaults.buttonColors(containerColor = if (isFavorite) Color(0xFF553C1C) else SurfaceTwo),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (isFavorite) Gold else TextMain)
                Spacer(Modifier.width(8.dp))
                Text(if (isFavorite) "Remove favourite" else "Add to favourites", color = TextMain, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onToggleOwned,
                colors = ButtonDefaults.buttonColors(containerColor = if (isOwned) Color(0xFF1D4937) else SurfaceTwo),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (isOwned) Color(0xFF91EDB8) else TextMain)
                Spacer(Modifier.width(8.dp))
                Text(if (isOwned) "Owned by you" else "Mark as owned", color = TextMain, fontWeight = FontWeight.Bold)
            }
        }
        item {
            Text(
                "Ranking reflects this project's theorycrafting criteria, not an official HoYoverse ranking.",
                color = TextMuted,
                fontSize = 10.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun CharacterRow(
    character: Character,
    isFavorite: Boolean,
    isOwned: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceOne),
        border = BorderStroke(1.dp, if (isFavorite) Gold.copy(alpha = 0.65f) else Hairline)
    ) {
        Row(
            modifier = Modifier.padding(11.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CharacterPortrait(character.image, character.name, Modifier.size(70.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(character.name, color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    TierBadge(character.tier)
                }
                Text(character.element.replaceFirstChar { it.uppercase() } + "  •  " + character.weapon + "  •  " + character.constellation, color = TextMuted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(character.summary.ifBlank { character.role.ifBlank { "Open profile for ranking details." } }, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (isOwned) {
                    Text("OWNED", color = Color(0xFF91EDB8), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.6.sp)
                }
            }
            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(35.dp)) {
                Icon(
                    if (isFavorite) Icons.Filled.Favorite else Icons.Filled.Bookmark,
                    contentDescription = if (isFavorite) "Remove favourite" else "Add favourite",
                    tint = if (isFavorite) Gold else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun TierBadge(tier: String) {
    Surface(
        color = tierColor(tier).copy(alpha = 0.14f),
        shape = RoundedCornerShape(7.dp),
        border = BorderStroke(1.dp, tierColor(tier).copy(alpha = 0.55f))
    ) {
        Text(tier, color = tierColor(tier), fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp))
    }
}

@Composable
private fun CharacterPortrait(path: String, name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(initialValue = null, key1 = path) {
        value = if (path.isBlank()) null else withContext(Dispatchers.IO) {
            loadPortrait(context, path)
        }
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF263044), Color(0xFF151B28)))),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "$name portrait",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(name.take(1).uppercase(), color = Gold, fontSize = 27.sp, fontWeight = FontWeight.Black)
        }
    }
}

private fun loadPortrait(context: Context, path: String): Bitmap? {
    return try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.assets.open(path).use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (bounds.outWidth / sample > 700 || bounds.outHeight / sample > 700) sample *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sample.coerceAtLeast(1) }
        context.assets.open(path).use { BitmapFactory.decodeStream(it, null, options) }
    } catch (_: Exception) {
        null
    }
}

@Composable
private fun EmptyState(title: String, message: String) {
    Surface(
        color = SurfaceOne,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Hairline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(title, color = TextMain, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(message, color = TextMuted, fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}

private data class Character(
    val slug: String,
    val name: String,
    val tier: String,
    val element: String,
    val rarity: Int,
    val weapon: String,
    val constellation: String,
    val combatPower: String,
    val pullValue: String,
    val summary: String,
    val role: String,
    val image: String,
    val detailImage: String = image
)

private object CharacterRepository {
    fun load(context: Context): List<Character> {
        return try {
            val jsonText = context.assets.open("characters.json").bufferedReader().use { it.readText() }
            val array = JSONObject(jsonText).getJSONArray("characters")
            val result = buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    add(
                        Character(
                            slug = item.optString("slug", "character-$index"),
                            name = item.optString("name", "Unknown character"),
                            tier = item.optString("tier", "B"),
                            element = item.optString("element", "unknown"),
                            rarity = item.optInt("rarity", 5),
                            weapon = item.optString("weapon", "Unknown"),
                            constellation = item.optString("constellation", "C0"),
                            combatPower = item.optString("combatPower", ""),
                            pullValue = item.optString("pullValue", ""),
                            summary = item.optString("summary", ""),
                            role = item.optString("role", ""),
                            image = item.optString("image", ""),
                            detailImage = item.optString("detailImage", item.optString("image", ""))
                        )
                    )
                }
            }
            result.ifEmpty { fallbackCharacters() }
        } catch (_: Exception) {
            fallbackCharacters()
        }
    }

    private fun fallbackCharacters() = listOf(
        Character("sandrone", "Sandrone", "SS", "cryo", 5, "Claymore", "C0", "5.0", "5/5 • Automaton Carry", "Heavy automaton-assisted Cryo claymore swings and sustained off-field payload strikes.", "Automaton Carry", "characters/sandrone.webp"),
        Character("mavuika", "Mavuika", "SS", "pyro", 5, "Claymore", "C0", "", "", "Pinnacle on-field damage potential under the site's current ranking framework.", "On-field carry", "characters/mavuika.webp"),
        Character("skirk", "Skirk", "S", "cryo", 5, "Sword", "C0", "", "", "A high-ceiling on-field carry with strong dedicated support synergies.", "On-field carry", "characters/skirk.webp"),
        Character("mizuki", "Yumemizuki Mizuki", "S", "anemo", 5, "Catalyst", "C0", "", "", "An Anemo driver evaluated around Swirl teams and reaction support.", "Swirl driver", "characters/mizuki.webp"),
        Character("ayaka", "Kamisato Ayaka", "B", "cryo", 5, "Sword", "C0", "4.2", "3.5/5 • Freeze Dependent", "Enormous frontloaded Cryo burst; heavily reliant on freezable targets and grouping.", "Freeze Burst Carry", "characters/ayaka.webp"),
        Character("clorinde", "Clorinde", "A", "electro", 5, "Sword", "C0", "4.3", "3.5/5 • Short Uptime", "Fast-paced pistol-and-thrust lunges with built-in healing during short Skill windows.", "Quickswap Electro DPS", "characters/clorinde.webp"),
        Character("gaming", "Gaming", "B", "pyro", 4, "Claymore", "C6", "4.4", "4/5 • Top 4★ Plunge", "C6 enables major CRIT bonuses on repeated plunge attacks alongside Xianyun.", "Plunge Hypercarry", "characters/gaming.webp"),
        Character("yoimiya", "Yoimiya", "C", "pyro", 5, "Bow", "C0", "3.7", "2.5/5 • Pyro Redundancy", "Simple single-target normal attacks; the source tier card places her in C tier.", "Single-target attacker", "characters/yoimiya.webp")
    )
}

private object PreferenceStore {
    private const val FILE_NAME = "genshin_archive_preferences"

    fun read(context: Context, key: String): Set<String> =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getStringSet(key, emptySet())?.toSet() ?: emptySet()

    fun write(context: Context, key: String, values: Set<String>) {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit().putStringSet(key, values.toSet()).apply()
    }
}
