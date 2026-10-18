package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CompendiumCategory
import com.example.data.models.CompendiumEntry
import com.example.ui.HyruleViewModel
import com.example.ui.components.HeartsDisplay
import com.example.ui.components.SheikahCard
import com.example.ui.theme.*

@Composable
fun CompendiumScreen(
    viewModel: HyruleViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.compendiumCategory.collectAsState()
    val searchQuery by viewModel.compendiumSearch.collectAsState()
    val filterFavorites by viewModel.compendiumFilterFavorites.collectAsState()

    val photographedItems by viewModel.repository.photographedItems.collectAsState()
    val favoriteItems by viewModel.repository.favoriteItems.collectAsState()

    var expandedItemId by remember { mutableStateOf<Int?>(null) }

    val filteredList = remember(selectedCategory, searchQuery, filterFavorites, favoriteItems) {
        viewModel.repository.compendiumEntries.filter { item ->
            val matchesCategory = selectedCategory == CompendiumCategory.ALL || item.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.locations.any { it.contains(searchQuery, ignoreCase = true) } ||
                    item.drops.any { it.contains(searchQuery, ignoreCase = true) }
            val matchesFav = !filterFavorites || favoriteItems.contains(item.id)
            matchesCategory && matchesSearch && matchesFav
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SheikahSlateBg)
    ) {
        // Header & Search
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
                        text = "HYRULE COMPENDIUM",
                        color = SheikahCyanBright,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${photographedItems.size} Photographed / ${viewModel.repository.compendiumEntries.size} Cataloged",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                FilterChip(
                    selected = filterFavorites,
                    onClick = { viewModel.toggleCompendiumFavoritesFilter() },
                    label = { Text("⭐ Favorites", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = HylianGold.copy(alpha = 0.25f),
                        selectedLabelColor = HylianGold
                    ),
                    modifier = Modifier.testTag("filter_favorites")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setCompendiumSearch(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compendium_search_input"),
                placeholder = { Text("Search creature, monster, material, drop...", color = Color.Gray, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SheikahCyan) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setCompendiumSearch("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SheikahCyan,
                    unfocusedBorderColor = SheikahSlateBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CompendiumCategory.entries) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCompendiumCategory(cat) },
                        label = { Text("${cat.icon} ${cat.displayName}", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SheikahCyan.copy(alpha = 0.25f),
                            selectedLabelColor = SheikahCyanBright,
                            containerColor = SheikahSlateCard,
                            labelColor = Color.White.copy(alpha = 0.7f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) SheikahCyan else SheikahSlateBorder
                        ),
                        modifier = Modifier.testTag("category_${cat.name.lowercase()}")
                    )
                }
            }
        }

        // Entries List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No compendium records match your search.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            items(filteredList, key = { it.id }) { item ->
                val isPhotographed = photographedItems.contains(item.id)
                val isFavorite = favoriteItems.contains(item.id)
                val isExpanded = expandedItemId == item.id

                SheikahCard(
                    modifier = Modifier
                        .clickable { expandedItemId = if (isExpanded) null else item.id }
                        .testTag("compendium_item_${item.id}"),
                    borderColor = if (isPhotographed) SheikahCyan.copy(alpha = 0.6f) else SheikahSlateBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            // Number badge
                            Surface(
                                color = SheikahCyan.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SheikahCyan.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "#%03d".format(item.id),
                                    color = SheikahCyanBright,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = item.name,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${item.category.displayName} • ${item.subCategory}",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Actions (Photographed & Favorite)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.toggleItemPhotographed(item.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Text(if (isPhotographed) "📸" else "📷", fontSize = 16.sp)
                            }
                            IconButton(
                                onClick = { viewModel.toggleItemFavorite(item.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Text(if (isFavorite) "⭐" else "☆", fontSize = 16.sp, color = if (isFavorite) HylianGold else Color.Gray)
                            }
                        }
                    }

                    // Quick stats line
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.attack != null) {
                            Text("⚔️ ATK ${item.attack}", color = AncientOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        if (item.defense != null) {
                            Text("🛡️ DEF ${item.defense}", color = SheikahCyanBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        if (item.healingHearts != null) {
                            HeartsDisplay(hearts = item.healingHearts)
                        }
                        if (item.effect != null && item.attack == null && item.defense == null) {
                            Text("✨ ${item.effect}", color = HylianGold, fontSize = 11.sp, maxLines = 1)
                        }
                    }

                    // Expanded lore & location details
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            HorizontalDivider(color = SheikahSlateBorder, modifier = Modifier.padding(bottom = 8.dp))

                            Text(
                                text = item.description,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )

                            if (item.locations.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "📍 Common Locations: ${item.locations.joinToString(", ")}",
                                    color = SheikahCyanBright,
                                    fontSize = 11.sp
                                )
                            }

                            if (item.drops.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "🎒 Recoverable Drops: ${item.drops.joinToString(", ")}",
                                    color = AncientAmber,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
