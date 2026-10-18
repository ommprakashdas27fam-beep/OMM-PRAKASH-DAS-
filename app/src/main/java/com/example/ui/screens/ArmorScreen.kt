package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HyruleViewModel
import com.example.ui.components.SheikahCard
import com.example.ui.theme.*

@Composable
fun ArmorScreen(
    viewModel: HyruleViewModel,
    modifier: Modifier = Modifier
) {
    val armorLevels by viewModel.repository.armorStarLevels.collectAsState()
    var expandedArmorId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SheikahSlateBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Great Fairy Fountains Header
        item {
            SheikahCard(borderColor = HylianGold.copy(alpha = 0.6f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GREAT FAIRY FOUNTAINS",
                            color = HylianGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Cotera (Kakariko) • Mija (Akkala) • Kaysa (Tabantha) • Tera (Gerudo)",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )
                    }
                    Text("🧚‍♀️", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Offer rupees to awaken all 4 Great Fairies to unlock up to ★★★★ maximum armor tier upgrades. Level 2 sets unlock unique passive Set Bonuses!",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        item {
            Text(
                text = "ARMOR SETS & UPGRADE TRACKER",
                color = SheikahCyanBright,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Armor Sets
        items(viewModel.repository.armorSets, key = { it.id }) { armor ->
            val currentLevel = armorLevels[armor.id] ?: 0
            val isExpanded = expandedArmorId == armor.id
            val currentDef = armor.defensePerTier.getOrElse(currentLevel) { armor.defensePerTier.last() }

            SheikahCard(
                modifier = Modifier
                    .clickable { expandedArmorId = if (isExpanded) null else armor.id }
                    .testTag("armor_card_${armor.id}"),
                borderColor = if (currentLevel >= 4) HylianGold.copy(alpha = 0.8f) else SheikahCyan.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = armor.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Set Bonus: ${armor.setBonus}",
                            color = StaminaGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Defense Badge
                    Surface(
                        color = SheikahCyan.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SheikahCyan),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "🛡️ $currentDef DEF",
                            color = SheikahCyanBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Star Level Selector (0 to 4 stars)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Upgrade Tier:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (lvl in 0..4) {
                            val isSelected = currentLevel == lvl
                            Surface(
                                modifier = Modifier
                                    .clickable { viewModel.updateArmorStarLevel(armor.id, lvl) }
                                    .testTag("armor_${armor.id}_tier_$lvl"),
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) HylianGold.copy(alpha = 0.3f) else SheikahSlateCardElevated,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) HylianGold else SheikahSlateBorder
                                )
                            ) {
                                Text(
                                    text = if (lvl == 0) "Base" else "★".repeat(lvl),
                                    color = if (isSelected) HylianGold else Color.Gray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Expanded Set Details & Materials Guide
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        HorizontalDivider(color = SheikahSlateBorder, modifier = Modifier.padding(bottom = 8.dp))

                        Text(
                            text = armor.description,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Equipped Pieces: ${armor.pieces.joinToString(", ")}",
                            color = SheikahCyanBright,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Key Upgrade Materials: ${armor.materialsGuide.joinToString(", ")}",
                            color = AncientAmber,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Defense Progression: ${armor.defensePerTier.joinToString(" → ")} DEF",
                            color = HylianGold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
