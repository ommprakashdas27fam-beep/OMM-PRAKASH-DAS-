package com.example.data.models

enum class CompendiumCategory(val displayName: String, val icon: String) {
    ALL("All", "◈"),
    CREATURES("Creatures", "🦊"),
    MONSTERS("Monsters", "👹"),
    MATERIALS("Materials", "🌿"),
    EQUIPMENT("Equipment", "⚔️"),
    TREASURE("Treasure", "💎")
}

data class CompendiumEntry(
    val id: Int,
    val name: String,
    val category: CompendiumCategory,
    val subCategory: String,
    val description: String,
    val locations: List<String>,
    val drops: List<String> = emptyList(),
    val attack: Int? = null,
    val defense: Int? = null,
    val healingHearts: Float? = null,
    val effect: String? = null
)

enum class CookingEffect(
    val displayName: String,
    val colorHex: Long,
    val description: String
) {
    NONE("Normal", 0xFFE0E0E0, "Restores health"),
    HEARTY("Hearty", 0xFFFFD700, "Full recovery & temporary yellow hearts"),
    ENERGIZING("Energizing", 0xFF00E676, "Instantly restores stamina wheels"),
    ENDURING("Enduring", 0xFF81C784, "Restores stamina & grants yellow bonus stamina"),
    FIREPROOF("Fireproof", 0xFFFF6D00, "Protects against burning in volcanic heat"),
    SPICY("Spicy (Cold Resist)", 0xFFFF5722, "Warms body in freezing temperatures"),
    CHILLY("Chilly (Heat Resist)", 0xFF29B6F6, "Cools body in scorching desert heat"),
    ELECTRO("Electro (Shock Resist)", 0xFFFFEB3B, "Insulates against electric damage & lightning"),
    HASTY("Hasty (Speed Up)", 0xFF00E5FF, "Boosts running, climbing, and swimming speed"),
    SNEAKY("Sneaky (Stealth Up)", 0xFFCE93D8, "Reduces noise made while moving"),
    MIGHTY("Mighty (Attack Up)", 0xFFFF3D00, "Increases weapon damage dealt to foes"),
    TOUGH("Tough (Defense Up)", 0xFFFFAB00, "Reduces damage taken from enemies")
}

enum class IngredientCategory(val label: String) {
    FRUIT("Fruit"),
    MUSHROOM("Mushroom"),
    MEAT("Meat"),
    FISH("Fish/Seafood"),
    PLANT("Plant/Herb"),
    MONSTER_PART("Monster Part"),
    CRITTER("Critter/Bug"),
    MINERAL("Ore/Mineral"),
    OTHER("Spice/Other")
}

data class Ingredient(
    val id: String,
    val name: String,
    val category: IngredientCategory,
    val hearts: Float,
    val effect: CookingEffect = CookingEffect.NONE,
    val effectPoints: Int = 0,
    val baseDurationSeconds: Int = 0,
    val iconSymbol: String,
    val description: String
)

data class CookedDish(
    val name: String,
    val hearts: Float,
    val bonusHearts: Int = 0,
    val staminaWheels: Float = 0f,
    val bonusStaminaWheels: Float = 0f,
    val effect: CookingEffect = CookingEffect.NONE,
    val effectLevel: Int = 0,
    val durationSeconds: Int = 0,
    val isDubious: Boolean = false,
    val isRockHard: Boolean = false,
    val ingredientsUsed: List<String> = emptyList(),
    val description: String
)

data class CookbookRecipe(
    val id: String,
    val name: String,
    val effect: CookingEffect,
    val category: String,
    val ingredientNames: List<String>,
    val description: String,
    val heartsPreview: Float
)

data class ShrineInfo(
    val id: Int,
    val name: String,
    val trialName: String,
    val region: String,
    val hasQuest: Boolean = false,
    val questName: String? = null,
    val guideHint: String
)

data class ArmorSet(
    val id: String,
    val name: String,
    val pieces: List<String>,
    val setBonus: String,
    val defensePerTier: List<Int>, // Tier 0, 1, 2, 3, 4
    val materialsGuide: List<String>,
    val description: String
)

data class ChampionAbility(
    val name: String,
    val champion: String,
    val divineBeast: String,
    val region: String,
    val cooldownMinutes: Int,
    val charges: Int,
    val effect: String,
    val quote: String
)
