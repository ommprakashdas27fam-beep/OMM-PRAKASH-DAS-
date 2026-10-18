package com.example

import com.example.cooking.CookingEngine
import com.example.data.models.CookingEffect
import com.example.data.models.Ingredient
import com.example.data.models.IngredientCategory
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun cookingDurians_yieldsHeartyDish() {
        val durian = Ingredient(
            id = "durian",
            name = "Hearty Durian",
            category = IngredientCategory.FRUIT,
            hearts = 3.0f,
            effect = CookingEffect.HEARTY,
            effectPoints = 4,
            iconSymbol = "🍈",
            description = "King of fruits"
        )

        val dish = CookingEngine.cook(listOf(durian, durian, durian, durian, durian))
        assertEquals("Hearty Copious Simmered Fruit", dish.name)
        assertEquals(CookingEffect.HEARTY, dish.effect)
        assertEquals(20, dish.bonusHearts) // 5 durians * 4 points = 20 bonus hearts
        assertFalse(dish.isDubious)
        assertFalse(dish.isRockHard)
    }

    @Test
    fun critterAndMonsterPart_yieldsElixir() {
        val frog = Ingredient(
            id = "frog",
            name = "Hot-Footed Frog",
            category = IngredientCategory.CRITTER,
            hearts = 0.0f,
            effect = CookingEffect.HASTY,
            effectPoints = 2,
            baseDurationSeconds = 60,
            iconSymbol = "🐸",
            description = "Swift frog"
        )
        val guts = Ingredient(
            id = "guts",
            name = "Bokoblin Guts",
            category = IngredientCategory.MONSTER_PART,
            hearts = 0.0f,
            iconSymbol = "🫀",
            description = "Monster guts"
        )

        val dish = CookingEngine.cook(listOf(frog, guts))
        assertEquals("Hasty Elixir", dish.name)
        assertEquals(CookingEffect.HASTY, dish.effect)
        assertTrue(dish.durationSeconds > 0)
        assertFalse(dish.isDubious)
    }

    @Test
    fun critterWithoutMonsterPart_yieldsDubiousFood() {
        val frog = Ingredient(
            id = "frog",
            name = "Hot-Footed Frog",
            category = IngredientCategory.CRITTER,
            hearts = 0.0f,
            effect = CookingEffect.HASTY,
            iconSymbol = "🐸",
            description = "Swift frog"
        )
        val apple = Ingredient(
            id = "apple",
            name = "Apple",
            category = IngredientCategory.FRUIT,
            hearts = 0.5f,
            iconSymbol = "🍎",
            description = "Apple"
        )

        val dish = CookingEngine.cook(listOf(frog, apple))
        assertEquals("Dubious Food", dish.name)
        assertTrue(dish.isDubious)
    }

    @Test
    fun cookingMineral_yieldsRockHardFood() {
        val amber = Ingredient(
            id = "amber",
            name = "Amber",
            category = IngredientCategory.MINERAL,
            hearts = 0.0f,
            iconSymbol = "🔶",
            description = "Amber ore"
        )

        val dish = CookingEngine.cook(listOf(amber))
        assertEquals("Rock-Hard Food", dish.name)
        assertTrue(dish.isRockHard)
    }

    @Test
    fun conflictingEffects_neutralize() {
        val banana = Ingredient(
            id = "banana",
            name = "Mighty Bananas",
            category = IngredientCategory.FRUIT,
            hearts = 0.5f,
            effect = CookingEffect.MIGHTY,
            effectPoints = 2,
            baseDurationSeconds = 50,
            iconSymbol = "🍌",
            description = "Attack up"
        )
        val ironshroom = Ingredient(
            id = "ironshroom",
            name = "Ironshroom",
            category = IngredientCategory.MUSHROOM,
            hearts = 0.5f,
            effect = CookingEffect.TOUGH,
            effectPoints = 1,
            baseDurationSeconds = 50,
            iconSymbol = "🍄",
            description = "Defense up"
        )

        val dish = CookingEngine.cook(listOf(banana, ironshroom))
        assertEquals(CookingEffect.NONE, dish.effect)
        assertEquals("Fruit and Mushroom Mix", dish.name)
    }
}
