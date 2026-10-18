package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
fun ShrinesScreen(
    viewModel: HyruleViewModel,
    modifier: Modifier = Modifier
) {
    val completedShrines by viewModel.repository.completedShrines.collectAsState()
    val regionFilter by viewModel.shrineRegionFilter.collectAsState()
    val searchQuery by viewModel.shrineSearch.collectAsState()
    var expandedShrineId by remember { mutableStateOf<Int?>(null) }

    val regions = listOf(
        "All", "Great Plateau", "Dueling Peaks", "Hateno", "Lanayru",
        "Central Hyrule", "Akkala", "Eldin", "Woodland", "Hebra", "Gerudo", "Faron"
    )

    val filteredShrines = remember(regionFilter, searchQuery) {
        viewModel.repository.shrines.filter { shrine ->
            val matchesRegion = regionFilter == "All" || shrine.region.contains(regionFilter, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    shrine.name.contains(searchQuery, ignoreCase = true) ||
                    shrine.trialName.contains(searchQuery, ignoreCase = true) ||
                    shrine.region.contains(searchQuery, ignoreCase = true) ||
                    (shrine.questName?.contains(searchQuery, ignoreCase = true) == true)
            matchesRegion && matchesSearch
        }
    }

    val totalCompleted = completedShrines.size
    val totalAvailable = viewModel.repository.shrines.size
    val upgradesAvailable = totalCompleted / 4
    val orbsToNext = 4 - (totalCompleted % 4)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SheikahSlateBg)
    ) {
        // Summary & Spirit Orb Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ANCIENT SHEIKAH SHRINES",
                        color = HylianGold,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "$totalCompleted / $totalAvailable Shrines Cleared",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }

                Surface(
                    color = HylianGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HylianGold)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔮 $totalCompleted Orbs", color = HylianGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$upgradesAvailable Stat Upgrades", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setShrineSearch(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shrine_search_input"),
                placeholder = { Text("Search shrine, puzzle trial, quest...", color = Color.Gray, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = HylianGold) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setShrineSearch("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HylianGold,
                    unfocusedBorderColor = SheikahSlateBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Region Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(regions) { reg ->
                    val isSelected = reg == regionFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setShrineRegionFilter(reg) },
                        label = { Text(reg, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HylianGold.copy(alpha = 0.25f),
                            selectedLabelColor = HylianGold
                        ),
                        modifier = Modifier.testTag("shrine_region_$reg")
                    )
                }
            }
        }

        // Shrines List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            items(filteredShrines, key = { it.id }) { shrine ->
                val isCompleted = completedShrines.contains(shrine.id)
                val isExpanded = expandedShrineId == shrine.id

                SheikahCard(
                    modifier = Modifier
                        .clickable { expandedShrineId = if (isExpanded) null else shrine.id }
                        .testTag("shrine_card_${shrine.id}"),
                    borderColor = if (isCompleted) SheikahCyan.copy(alpha = 0.8f) else SheikahSlateBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Checkbox(
                                checked = isCompleted,
                                onCheckedChange = { viewModel.toggleShrineCompletion(shrine.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = SheikahCyan,
                                    uncheckedColor = Color.Gray
                                ),
                                modifier = Modifier.testTag("shrine_checkbox_${shrine.id}")
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = shrine.name,
                                        color = if (isCompleted) SheikahCyanBright else Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color.White.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = shrine.region,
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = shrine.trialName,
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (shrine.hasQuest) {
                            Surface(
                                color = HylianGold.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, HylianGold)
                            ) {
                                Text(
                                    text = "📜 Quest",
                                    color = HylianGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Expanded Walkthrough / Hint
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            HorizontalDivider(color = SheikahSlateBorder, modifier = Modifier.padding(bottom = 8.dp))

                            if (shrine.questName != null) {
                                Text(
                                    text = "Shrine Quest: ${shrine.questName}",
                                    color = HylianGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            Text(
                                text = "💡 Solution & Guide: ${shrine.guideHint}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
