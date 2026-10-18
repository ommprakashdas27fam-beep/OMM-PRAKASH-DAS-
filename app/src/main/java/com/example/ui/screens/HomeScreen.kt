package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SheikahAudio
import com.example.ui.AppTab
import com.example.ui.HyruleViewModel
import com.example.ui.components.HeartsDisplay
import com.example.ui.components.SheikahCard
import com.example.ui.components.StaminaWheelMeter
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HyruleViewModel,
    modifier: Modifier = Modifier
) {
    val completedShrines by viewModel.repository.completedShrines.collectAsState()
    val photographedItems by viewModel.repository.photographedItems.collectAsState()
    var bloodMoonActive by remember { mutableStateOf(false) }
    var activeQuote by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (bloodMoonActive) Color(0xFF1A050B) else SheikahSlateBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.5.dp,
                        if (bloodMoonActive) GuardianRed else SheikahCyan,
                        RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.hyrule_landscape_1792326295125),
                        contentDescription = "The Kingdom of Hyrule",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        if (bloodMoonActive) GuardianRed.copy(alpha = 0.5f) else Color(0xCC091220)
                                    )
                                )
                            )
                    )
                    // Title badge
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = SheikahCyan.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SheikahCyan)
                        ) {
                            Text(
                                text = "THE LEGEND OF ZELDA: BREATH OF THE WILD",
                                color = SheikahCyanBright,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (bloodMoonActive) "THE BLOOD MOON RISES" else "SHEIKAH SLATE v2.0",
                            color = if (bloodMoonActive) GuardianRed else Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Blood moon toggle button
                    IconButton(
                        onClick = {
                            bloodMoonActive = !bloodMoonActive
                            if (bloodMoonActive) {
                                SheikahAudio.playDetonation()
                            } else {
                                SheikahAudio.playShrineChime()
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color(0x99000000), CircleShape)
                            .testTag("blood_moon_toggle")
                    ) {
                        Text(if (bloodMoonActive) "🌕" else "🌑", fontSize = 18.sp)
                    }
                }
            }
        }

        // Blood Moon Banner
        if (bloodMoonActive) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GuardianRed.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GuardianRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚠️", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "The Blood Moon rises once again...",
                                color = GuardianRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "When the clock strikes midnight, all defeated monsters across Hyrule are revived by Ganon's dark malice. Cooking during this window guarantees Critical Success!",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Sheikah Slate Status Bar (Hearts, Stamina, Time, Weather, Sensor)
        item {
            SheikahCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("LINK'S HEALTH GAUGE", color = SheikahCyanBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        HeartsDisplay(hearts = 15f, bonusHearts = 5)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("STAMINA WHEELS", color = StaminaGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        StaminaWheelMeter(wheels = 3.0f)
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = SheikahSlateBorder
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌡️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Temp: 68°F (Temperate)", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sensor+: Active", color = SheikahCyanBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick Navigation Tiles
        item {
            Text(
                text = "SHEIKAH SYSTEM MODULES",
                color = SheikahCyanBright,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NavModuleCard(
                        title = "Hyrule Compendium",
                        subtitle = "${photographedItems.size} Photographed",
                        icon = "📖",
                        color = SheikahCyan,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_compendium",
                        onClick = { viewModel.selectTab(AppTab.COMPENDIUM) }
                    )
                    NavModuleCard(
                        title = "Campfire Cooking",
                        subtitle = "5-Slot Pot & Cookbook",
                        icon = "🍲",
                        color = AncientOrange,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_cooking",
                        onClick = { viewModel.selectTab(AppTab.COOKING) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NavModuleCard(
                        title = "Shrines & Quests",
                        subtitle = "${completedShrines.size} / 120 Cleared",
                        icon = "⛩️",
                        color = HylianGold,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_shrines",
                        onClick = { viewModel.selectTab(AppTab.SHRINES) }
                    )
                    NavModuleCard(
                        title = "Sheikah Runes",
                        subtitle = "Bombs, Stasis, Radar",
                        icon = "⚡",
                        color = SheikahCyanBright,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_runes",
                        onClick = { viewModel.selectTab(AppTab.RUNES) }
                    )
                }

                NavModuleCard(
                    title = "Armor Vault & Great Fairy",
                    subtitle = "Barbarian, Ancient, Champion's Tunic (★★★★)",
                    icon = "🛡️",
                    color = StaminaGreen,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "nav_armor",
                    onClick = { viewModel.selectTab(AppTab.ARMOR) }
                )
            }
        }

        // Champion's Divine Gifts
        item {
            Text(
                text = "CHAMPIONS' DIVINE POWERS",
                color = SheikahCyanBright,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(viewModel.repository.championAbilities.size) { idx ->
            val ability = viewModel.repository.championAbilities[idx]
            SheikahCard(
                borderColor = when (idx) {
                    0 -> Color(0xFF4FC3F7) // Mipha (Cyan/Water)
                    1 -> Color(0xFF81C784) // Revali (Green/Wind)
                    2 -> AncientOrange    // Daruk (Orange/Fire)
                    else -> HylianGold    // Urbosa (Yellow/Lightning)
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (idx) {
                                    0 -> "💧"
                                    1 -> "🦅"
                                    2 -> "🛡️"
                                    else -> "⚡"
                                },
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = ability.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "${ability.champion} • ${ability.divineBeast}",
                            color = SheikahCyanBright,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ability.effect,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            activeQuote = ability.quote
                            when (idx) {
                                0 -> SheikahAudio.playShrineChime()
                                1 -> SheikahAudio.playRuneSelect()
                                2 -> SheikahAudio.playBeep(400f, 120)
                                else -> SheikahAudio.playDetonation()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SheikahCyan.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SheikahCyan),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Invoke", color = SheikahCyanBright, fontSize = 12.sp)
                    }
                }
            }
        }

        // Active Quote Popup
        if (activeQuote != null) {
            item {
                Surface(
                    color = SheikahSlateCardElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HylianGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = activeQuote!!,
                            color = HylianGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { activeQuote = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Hyrule Combat & Survival Codex
        item {
            Text(
                text = "HYRULE SURVIVAL & COMBAT CODEX",
                color = SheikahCyanBright,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            SheikahCard {
                CombatTipRow(
                    icon = "⚡",
                    title = "Flurry Rush",
                    description = "Dodge horizontal sweeps with a Backflip or vertical slams with a Side Hop at the precise strike moment to trigger bullet-time counter attacks."
                )
                CombatTipRow(
                    icon = "🛡️",
                    title = "Perfect Guard (Shield Parry)",
                    description = "Press A (Shield Parry) right when a Guardian's red laser eye flashes blue to reflect the blast directly back into its core for an instant kill!"
                )
                CombatTipRow(
                    icon = "🗡️",
                    title = "Sneakstrike (8x Damage)",
                    description = "Crouch and quietly approach sleeping monsters from behind to deal a devastating 800% damage sneak attack with any melee weapon."
                )
                CombatTipRow(
                    icon = "❄️",
                    title = "Frozen Shatter (3x Damage)",
                    description = "Freezing an enemy with an Ice Arrow or Blizzard Rod turns their body into solid ice. The subsequent physical blow deals 3x triple damage!"
                )
            }
        }
    }
}

@Composable
private fun NavModuleCard(
    title: String,
    subtitle: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick)
            .testTag(testTag)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SheikahSlateCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(icon, fontSize = 24.sp)
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    color = color,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun CombatTipRow(icon: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, color = SheikahCyanBright, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(description, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}
