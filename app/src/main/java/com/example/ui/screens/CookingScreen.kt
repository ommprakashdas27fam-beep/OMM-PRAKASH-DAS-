package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CookedDish
import com.example.data.models.CookingEffect
import com.example.data.models.Ingredient
import com.example.data.models.IngredientCategory
import com.example.ui.HyruleViewModel
import com.example.ui.components.EffectBadge
import com.example.ui.components.HeartsDisplay
import com.example.ui.components.SheikahCard
import com.example.ui.components.StaminaWheelMeter
import com.example.ui.theme.*

@Composable
fun CookingScreen(
    viewModel: HyruleViewModel,
    modifier: Modifier = Modifier
) {
    val potIngredients by viewModel.potIngredients.collectAsState()
    val lastCookedDish by viewModel.lastCookedDish.collectAsState()
    val isCooking by viewModel.isCooking.collectAsState()
    val cookingHistory by viewModel.cookingHistory.collectAsState()

    var activeSubTab by remember { mutableStateOf("pot") } // "pot", "cookbook", "history"
    var selectedCategoryFilter by remember { mutableStateOf<IngredientCategory?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SheikahSlateBg)
    ) {
        // Top Sub-tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CookingSubTab(
                label = "🍲 Campfire Pot (${potIngredients.size}/5)",
                isSelected = activeSubTab == "pot",
                modifier = Modifier.weight(1f),
                testTag = "subtab_pot",
                onClick = { activeSubTab = "pot" }
            )
            CookingSubTab(
                label = "📖 Cookbook",
                isSelected = activeSubTab == "cookbook",
                modifier = Modifier.weight(1f),
                testTag = "subtab_cookbook",
                onClick = { activeSubTab = "cookbook" }
            )
            CookingSubTab(
                label = "📜 History (${cookingHistory.size})",
                isSelected = activeSubTab == "history",
                modifier = Modifier.weight(1f),
                testTag = "subtab_history",
                onClick = { activeSubTab = "history" }
            )
        }

        when (activeSubTab) {
            "pot" -> CookingPotView(
                potIngredients = potIngredients,
                isCooking = isCooking,
                selectedCategory = selectedCategoryFilter,
                onCategorySelect = { selectedCategoryFilter = it },
                onAddIngredient = { viewModel.addIngredientToPot(it) },
                onRemoveIngredient = { viewModel.removeIngredientFromPot(it) },
                onClear = { viewModel.clearPot() },
                onCook = { viewModel.cookCurrentPot() },
                allIngredients = viewModel.repository.ingredients
            )
            "cookbook" -> CookbookView(
                recipes = viewModel.repository.cookbookRecipes,
                onLoadRecipe = {
                    viewModel.loadRecipe(it)
                    activeSubTab = "pot"
                }
            )
            "history" -> CookingHistoryView(history = cookingHistory)
        }
    }

    // Cook Result Modal Dialog
    if (lastCookedDish != null) {
        CookResultDialog(
            dish = lastCookedDish!!,
            onDismiss = { viewModel.dismissCookResult() }
        )
    }
}

@Composable
private fun CookingSubTab(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = if (isSelected) AncientOrange.copy(alpha = 0.2f) else SheikahSlateCard,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) AncientOrange else SheikahSlateBorder
        )
    ) {
        Text(
            text = label,
            color = if (isSelected) AncientAmber else Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            maxLines = 1
        )
    }
}

@Composable
private fun CookingPotView(
    potIngredients: List<Ingredient>,
    isCooking: Boolean,
    selectedCategory: IngredientCategory?,
    onCategorySelect: (IngredientCategory?) -> Unit,
    onAddIngredient: (Ingredient) -> Unit,
    onRemoveIngredient: (Int) -> Unit,
    onClear: () -> Unit,
    onCook: () -> Unit,
    allIngredients: List<Ingredient>
) {
    val filteredIngredients = remember(selectedCategory) {
        if (selectedCategory == null) allIngredients
        else allIngredients.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // The Pot & 5 Slots
        item {
            SheikahCard(borderColor = AncientOrange.copy(alpha = 0.6f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "THE COOKING POT",
                        color = AncientAmber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (potIngredients.isNotEmpty()) {
                        TextButton(onClick = onClear, modifier = Modifier.testTag("clear_pot_button")) {
                            Text("Clear Pot", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 5 Slots Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0 until 5) {
                        val ingredient = potIngredients.getOrNull(i)
                        PotSlot(
                            ingredient = ingredient,
                            slotNumber = i + 1,
                            onClick = {
                                if (ingredient != null) onRemoveIngredient(i)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Cooking Action Button
                Button(
                    onClick = onCook,
                    enabled = potIngredients.isNotEmpty() && !isCooking,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("cook_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AncientOrange,
                        disabledContainerColor = SheikahSlateCardElevated
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isCooking) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Sizzling over flames... 🔥", fontWeight = FontWeight.Bold, color = Color.White)
                    } else {
                        Text(
                            text = if (potIngredients.isEmpty()) "Add up to 5 ingredients to cook" else "Cook Dish! 🔥",
                            fontWeight = FontWeight.Bold,
                            color = if (potIngredients.isEmpty()) Color.Gray else Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Shelf Category Filter
        item {
            Column {
                Text(
                    text = "SELECT INGREDIENTS TO ADD",
                    color = SheikahCyanBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { onCategorySelect(null) },
                            label = { Text("All", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SheikahCyan.copy(alpha = 0.2f),
                                selectedLabelColor = SheikahCyanBright
                            )
                        )
                    }
                    items(IngredientCategory.entries) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { onCategorySelect(cat) },
                            label = { Text(cat.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SheikahCyan.copy(alpha = 0.2f),
                                selectedLabelColor = SheikahCyanBright
                            )
                        )
                    }
                }
            }
        }

        // Ingredient Cards
        items(filteredIngredients) { ing ->
            SheikahCard(
                modifier = Modifier
                    .clickable { onAddIngredient(ing) }
                    .testTag("ingredient_${ing.id}"),
                borderColor = SheikahSlateBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(ing.iconSymbol, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ing.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${ing.category.label})",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = ing.description,
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (ing.effect != CookingEffect.NONE) {
                        EffectBadge(effect = ing.effect)
                    } else if (ing.hearts > 0f) {
                        HeartsDisplay(hearts = ing.hearts)
                    }
                }
            }
        }
    }
}

@Composable
private fun PotSlot(
    ingredient: Ingredient?,
    slotNumber: Int,
    onClick: () -> Unit
) {
    val isFilled = ingredient != null
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clickable(enabled = isFilled, onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isFilled) AncientOrange.copy(alpha = 0.15f) else SheikahSlateCard,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isFilled) AncientOrange else SheikahSlateBorder
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (ingredient != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(ingredient.iconSymbol, fontSize = 20.sp)
                    Text(
                        text = ingredient.name.take(6),
                        color = AncientAmber,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            } else {
                Text(
                    text = "$slotNumber",
                    color = Color.White.copy(alpha = 0.25f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CookbookView(
    recipes: List<com.example.data.models.CookbookRecipe>,
    onLoadRecipe: (com.example.data.models.CookbookRecipe) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        items(recipes) { recipe ->
            SheikahCard(borderColor = AncientOrange.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = recipe.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = recipe.description,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ingredients: ${recipe.ingredientNames.joinToString(", ")}",
                            color = SheikahCyanBright,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        EffectBadge(effect = recipe.effect)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onLoadRecipe(recipe) },
                            colors = ButtonDefaults.buttonColors(containerColor = AncientOrange.copy(alpha = 0.25f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AncientOrange),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("load_recipe_${recipe.id}")
                        ) {
                            Text("Load into Pot", color = AncientAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CookingHistoryView(history: List<CookedDish>) {
    if (history.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No dishes cooked yet. Add ingredients to the pot!", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            items(history) { dish ->
                SheikahCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(dish.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(dish.description, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Text("Ingredients: ${dish.ingredientsUsed.joinToString(", ")}", color = Color.Gray, fontSize = 10.sp)
                        }
                        if (dish.effect != CookingEffect.NONE) {
                            EffectBadge(effect = dish.effect, level = dish.effectLevel)
                        } else {
                            HeartsDisplay(hearts = dish.hearts)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CookResultDialog(dish: CookedDish, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SheikahSlateCardElevated,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (dish.isDubious) "🤢 DUBIOUS DISH" else if (dish.isRockHard) "🪨 ROCK-HARD DISH" else "✨ COOKING COMPLETE!",
                    color = if (dish.isDubious || dish.isRockHard) Color.Gray else AncientAmber,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dish.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (dish.effect != CookingEffect.NONE) {
                    EffectBadge(effect = dish.effect, level = dish.effectLevel)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (dish.hearts > 0f) {
                    HeartsDisplay(hearts = dish.hearts, bonusHearts = dish.bonusHearts)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (dish.staminaWheels > 0f) {
                    StaminaWheelMeter(wheels = dish.staminaWheels)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = dish.description,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Cooked with: ${dish.ingredientsUsed.joinToString(", ")}",
                    color = SheikahCyanBright,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AncientOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("collect_dish_button")
            ) {
                Text("Place in Inventory", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}
