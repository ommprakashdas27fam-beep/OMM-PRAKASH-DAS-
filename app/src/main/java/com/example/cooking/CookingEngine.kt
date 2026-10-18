package com.example.cooking

import com.example.data.models.CookedDish
import com.example.data.models.CookingEffect
import com.example.data.models.Ingredient
import com.example.data.models.IngredientCategory
import kotlin.math.min

object CookingEngine {

    fun cook(ingredients: List<Ingredient>): CookedDish {
        if (ingredients.isEmpty()) {
            return CookedDish(
                name = "Empty Pot",
                hearts = 0f,
                description = "You placed nothing in the pot. The flame flickers quietly."
            )
        }

        val hasCritter = ingredients.any { it.category == IngredientCategory.CRITTER }
        val hasMonsterPart = ingredients.any { it.category == IngredientCategory.MONSTER_PART }
        val hasMineral = ingredients.any { it.category == IngredientCategory.MINERAL }
        val foodCount = ingredients.count {
            it.category != IngredientCategory.CRITTER &&
                    it.category != IngredientCategory.MONSTER_PART &&
                    it.category != IngredientCategory.MINERAL
        }

        // 1. Rock-Hard Food check
        if (hasMineral) {
            return CookedDish(
                name = "Rock-Hard Food",
                hearts = 0.25f,
                isRockHard = true,
                ingredientsUsed = ingredients.map { it.name },
                description = "A dish so hard you could break a tooth. It provides almost no nutrition, but it is technically edible."
            )
        }

        // 2. Dubious Food checks:
        // A) Has critter but no monster part
        // B) Has monster part but no critter
        // C) Both critter + monster part, but ALSO mixed with general food
        if (hasCritter && !hasMonsterPart) {
            return CookedDish(
                name = "Dubious Food",
                hearts = 1.0f,
                isDubious = true,
                ingredientsUsed = ingredients.map { it.name },
                description = "It's too gross to look at or smell. You cannot mix bugs directly into regular food without alchemy monster parts."
            )
        }

        if (hasMonsterPart && !hasCritter) {
            return CookedDish(
                name = "Dubious Food",
                hearts = 1.0f,
                isDubious = true,
                ingredientsUsed = ingredients.map { it.name },
                description = "Monster parts cannot be eaten as ordinary food! You must brew them with critters to make elixirs."
            )
        }

        // 3. Elixir check (Critter + Monster Part)
        val isElixir = hasCritter && hasMonsterPart

        // Determine effect
        val effectIngredients = ingredients.filter { it.effect != CookingEffect.NONE }
        val distinctEffects = effectIngredients.map { it.effect }.distinct()

        val activeEffect: CookingEffect
        val effectLevel: Int
        var bonusHearts = 0
        var staminaWheels = 0f
        var bonusStamina = 0f
        var durationSeconds = 0

        if (distinctEffects.size == 1) {
            activeEffect = distinctEffects.first()
            val totalPoints = effectIngredients.sumOf { it.effectPoints }
            val baseSeconds = effectIngredients.sumOf { it.baseDurationSeconds }
            // Additional bonus time for extra ingredients
            val ingredientCountBonus = (ingredients.size - 1) * 30
            durationSeconds = baseSeconds + ingredientCountBonus

            effectLevel = when {
                totalPoints >= 7 -> 3
                totalPoints >= 5 -> 2
                else -> 1
            }

            when (activeEffect) {
                CookingEffect.HEARTY -> {
                    bonusHearts = effectIngredients.sumOf { it.effectPoints }
                    durationSeconds = 0 // Hearty has no timer
                }
                CookingEffect.ENERGIZING -> {
                    staminaWheels = min(3.0f, totalPoints * 0.4f)
                    durationSeconds = 0
                }
                CookingEffect.ENDURING -> {
                    staminaWheels = 3.0f // Fully restores regular wheels
                    bonusStamina = min(2.0f, totalPoints * 0.4f)
                    durationSeconds = 0
                }
                else -> {
                    // Timed buff
                }
            }
        } else {
            // Conflicting effects cancel out into neutral
            activeEffect = CookingEffect.NONE
            effectLevel = 0
        }

        // Calculate health restored
        val baseHearts = if (activeEffect == CookingEffect.HEARTY) {
            30f // Full recovery in BotW
        } else {
            // Raw hearts * 2 when cooked, rounded to nearest 0.5
            val sumRaw = ingredients.sumOf { (it.hearts * 2.0).toDouble() }
            sumRaw.toFloat().coerceAtLeast(1.0f)
        }

        // Generate Dish / Elixir Name
        val dishName = if (isElixir) {
            val prefix = when (activeEffect) {
                CookingEffect.SPICY -> "Spicy"
                CookingEffect.CHILLY -> "Chilly"
                CookingEffect.ELECTRO -> "Electro"
                CookingEffect.FIREPROOF -> "Fireproof"
                CookingEffect.HASTY -> "Hasty"
                CookingEffect.SNEAKY -> "Sneaky"
                CookingEffect.MIGHTY -> "Mighty"
                CookingEffect.TOUGH -> "Tough"
                CookingEffect.ENERGIZING -> "Energizing"
                CookingEffect.ENDURING -> "Enduring"
                CookingEffect.HEARTY -> "Hearty"
                CookingEffect.NONE -> "Strange"
            }
            "$prefix Elixir"
        } else {
            generateFoodName(ingredients, activeEffect)
        }

        val description = when {
            activeEffect == CookingEffect.HEARTY ->
                "Instantly and completely restores all hearts, and grants +$bonusHearts temporary yellow hearts beyond your maximum health!"
            activeEffect == CookingEffect.ENDURING ->
                "Completely refills stamina and grants +${String.format("%.1f", bonusStamina)} extra yellow stamina wheels."
            activeEffect == CookingEffect.ENERGIZING ->
                "Refills ${String.format("%.1f", staminaWheels)} wheels of your stamina gauge."
            activeEffect != CookingEffect.NONE ->
                "Grants ${activeEffect.displayName} (Level $effectLevel) for ${formatDuration(durationSeconds)}."
            else ->
                "A delicious, nourishing meal lovingly prepared over a campfire in Hyrule."
        }

        return CookedDish(
            name = dishName,
            hearts = baseHearts,
            bonusHearts = bonusHearts,
            staminaWheels = staminaWheels,
            bonusStaminaWheels = bonusStamina,
            effect = activeEffect,
            effectLevel = effectLevel,
            durationSeconds = durationSeconds,
            ingredientsUsed = ingredients.map { it.name },
            description = description
        )
    }

    private fun generateFoodName(ingredients: List<Ingredient>, effect: CookingEffect): String {
        val meatCount = ingredients.count { it.category == IngredientCategory.MEAT }
        val fishCount = ingredients.count { it.category == IngredientCategory.FISH }
        val fruitCount = ingredients.count { it.category == IngredientCategory.FRUIT }
        val shroomCount = ingredients.count { it.category == IngredientCategory.MUSHROOM }
        val plantCount = ingredients.count { it.category == IngredientCategory.PLANT }

        val baseDish = when {
            meatCount > 0 && fishCount > 0 -> "Meat and Seafood Fry"
            meatCount > 0 && shroomCount > 0 -> "Meat and Mushroom Skewer"
            fishCount > 0 && shroomCount > 0 -> "Fish and Mushroom Skewer"
            fruitCount > 0 && shroomCount > 0 -> "Fruit and Mushroom Mix"
            meatCount >= 3 -> "Copious Meat Skewer"
            meatCount > 0 -> "Meat Skewer"
            fishCount >= 3 -> "Copious Fish Skewer"
            fishCount > 0 && plantCount > 0 -> "Steamed Fish"
            fishCount > 0 -> "Fish Skewer"
            shroomCount >= 4 -> "Copious Mushroom Skewer"
            shroomCount > 0 && plantCount > 0 -> "Steamed Mushrooms"
            shroomCount > 0 -> "Mushroom Skewer"
            fruitCount >= 4 -> "Copious Simmered Fruit"
            fruitCount > 0 -> "Simmered Fruit"
            plantCount > 0 -> "Fried Wild Greens"
            else -> "Simmered Dish"
        }

        val effectPrefix = when (effect) {
            CookingEffect.HEARTY -> "Hearty "
            CookingEffect.MIGHTY -> "Mighty "
            CookingEffect.TOUGH -> "Tough "
            CookingEffect.SPICY -> "Spicy "
            CookingEffect.CHILLY -> "Chilly "
            CookingEffect.ELECTRO -> "Electro "
            CookingEffect.HASTY -> "Hasty "
            CookingEffect.SNEAKY -> "Sneaky "
            CookingEffect.ENDURING -> "Enduring "
            CookingEffect.ENERGIZING -> "Energizing "
            CookingEffect.FIREPROOF -> "Fireproof "
            CookingEffect.NONE -> ""
        }

        return "$effectPrefix$baseDish"
    }

    private fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%d:%02d".format(mins, secs)
    }
}
