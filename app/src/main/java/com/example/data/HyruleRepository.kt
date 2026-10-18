package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HyruleRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sheikah_slate_prefs", Context.MODE_PRIVATE)

    private val _completedShrines = MutableStateFlow<Set<Int>>(emptySet())
    val completedShrines: StateFlow<Set<Int>> = _completedShrines.asStateFlow()

    private val _photographedItems = MutableStateFlow<Set<Int>>(emptySet())
    val photographedItems: StateFlow<Set<Int>> = _photographedItems.asStateFlow()

    private val _favoriteItems = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteItems: StateFlow<Set<Int>> = _favoriteItems.asStateFlow()

    private val _armorStarLevels = MutableStateFlow<Map<String, Int>>(emptyMap())
    val armorStarLevels: StateFlow<Map<String, Int>> = _armorStarLevels.asStateFlow()

    init {
        loadSavedState()
    }

    private fun loadSavedState() {
        val shrineStrings = prefs.getStringSet("completed_shrines", emptySet()) ?: emptySet()
        _completedShrines.value = shrineStrings.mapNotNull { it.toIntOrNull() }.toSet()

        val photoStrings = prefs.getStringSet("photographed_items", emptySet()) ?: emptySet()
        _photographedItems.value = photoStrings.mapNotNull { it.toIntOrNull() }.toSet()

        val favStrings = prefs.getStringSet("favorite_items", emptySet()) ?: emptySet()
        _favoriteItems.value = favStrings.mapNotNull { it.toIntOrNull() }.toSet()

        val armorMap = mutableMapOf<String, Int>()
        armorSets.forEach { armor ->
            val level = prefs.getInt("armor_${armor.id}", 0)
            armorMap[armor.id] = level
        }
        _armorStarLevels.value = armorMap
    }

    fun toggleShrineCompleted(shrineId: Int) {
        val current = _completedShrines.value.toMutableSet()
        if (current.contains(shrineId)) {
            current.remove(shrineId)
        } else {
            current.add(shrineId)
        }
        _completedShrines.value = current
        prefs.edit().putStringSet("completed_shrines", current.map { it.toString() }.toSet()).apply()
    }

    fun togglePhotographed(itemId: Int) {
        val current = _photographedItems.value.toMutableSet()
        if (current.contains(itemId)) {
            current.remove(itemId)
        } else {
            current.add(itemId)
        }
        _photographedItems.value = current
        prefs.edit().putStringSet("photographed_items", current.map { it.toString() }.toSet()).apply()
    }

    fun toggleFavorite(itemId: Int) {
        val current = _favoriteItems.value.toMutableSet()
        if (current.contains(itemId)) {
            current.remove(itemId)
        } else {
            current.add(itemId)
        }
        _favoriteItems.value = current
        prefs.edit().putStringSet("favorite_items", current.map { it.toString() }.toSet()).apply()
    }

    fun setArmorStarLevel(armorId: String, level: Int) {
        val current = _armorStarLevels.value.toMutableMap()
        current[armorId] = level.coerceIn(0, 4)
        _armorStarLevels.value = current
        prefs.edit().putInt("armor_$armorId", current[armorId] ?: 0).apply()
    }

    // --- Hyrule Compendium Data ---
    val compendiumEntries: List<CompendiumEntry> = listOf(
        // Creatures
        CompendiumEntry(
            id = 1,
            name = "Horse",
            category = CompendiumCategory.CREATURES,
            subCategory = "Fauna",
            description = "These can be found all over Hyrule. Taming one requires sneaking up quietly from behind and mounting it, then soothing it to build bond.",
            locations = listOf("Hyrule Ridge", "Faron Grasslands", "Central Hyrule"),
            drops = listOf(),
            effect = "Mountable"
        ),
        CompendiumEntry(
            id = 2,
            name = "Fairy",
            category = CompendiumCategory.CREATURES,
            subCategory = "Magical",
            description = "This mystical creature will heal you the moment you lose all your hearts. Unlike elixirs and food, it activates automatically upon falling in battle.",
            locations = listOf("Great Fairy Fountains", "Hyrule Ridge"),
            drops = listOf(),
            healingHearts = 5.0f,
            effect = "Revives Link instantly with 5 Hearts"
        ),
        CompendiumEntry(
            id = 3,
            name = "Hearty Blueshell Snail",
            category = CompendiumCategory.CREATURES,
            subCategory = "Water",
            description = "This snail lives in coastal areas and tide pools. When cooked into a dish, it maximizes your hearts and temporarily grants bonus yellow hearts.",
            locations = listOf("Lanayru Sea", "Necluda Sea"),
            drops = listOf(),
            healingHearts = 3.0f,
            effect = "Hearty (Bonus Hearts)"
        ),
        CompendiumEntry(
            id = 4,
            name = "Smotherwing Butterfly",
            category = CompendiumCategory.CREATURES,
            subCategory = "Insect",
            description = "A rare butterfly that lives in volcanic areas. Its heat-dissipating liquid can be brewed with monster parts to create a flame-resistant elixir.",
            locations = listOf("Eldin Canyon", "Death Mountain"),
            drops = listOf(),
            effect = "Fireproof (Heat Immunity)"
        ),
        CompendiumEntry(
            id = 5,
            name = "Energetic Rhino Beetle",
            category = CompendiumCategory.CREATURES,
            subCategory = "Insect",
            description = "This valuable beetle can live up to ten years. Cook it with monster parts for an elixir that greatly restores and increases stamina.",
            locations = listOf("Akkala Highlands", "West Necluda"),
            drops = listOf(),
            effect = "Energizing (Stamina Recovery)"
        ),
        CompendiumEntry(
            id = 6,
            name = "Sunset Firefly",
            category = CompendiumCategory.CREATURES,
            subCategory = "Insect",
            description = "Glows gently in the dark. Cooking this firefly with monster parts produces an elixir that silences footfalls and enhances stealth.",
            locations = listOf("Kakariko Village", "Faron Woods"),
            drops = listOf(),
            effect = "Sneaky (Stealth Up)"
        ),
        CompendiumEntry(
            id = 7,
            name = "Warm Darner",
            category = CompendiumCategory.CREATURES,
            subCategory = "Insect",
            description = "This dragonfly has wing scales that generate internal heat. Brew it with monster parts into an elixir to keep your body warm in freezing blizzards.",
            locations = listOf("Hyrule Ridge", "Akkala Highlands"),
            drops = listOf(),
            effect = "Spicy (Cold Resistance)"
        ),

        // Monsters
        CompendiumEntry(
            id = 90,
            name = "Red Bokoblin",
            category = CompendiumCategory.MONSTERS,
            subCategory = "Bokoblin",
            description = "The most common monster in Hyrule. They live in packs and carry crude wooden weapons, but become startled if approached stealthily.",
            locations = listOf("Hyrule Field", "West Necluda"),
            drops = listOf("Bokoblin Horn", "Bokoblin Fang"),
            attack = 1
        ),
        CompendiumEntry(
            id = 92,
            name = "Silver Bokoblin",
            category = CompendiumCategory.MONSTERS,
            subCategory = "Bokoblin",
            description = "White-silver colored Bokoblin carrying Calamity Ganon's malicious power. They boast enormous health and wield high-tier elemental weaponry.",
            locations = listOf("Central Hyrule", "Gerudo Highlands"),
            drops = listOf("Bokoblin Horn", "Bokoblin Fang", "Bokoblin Guts", "Amber", "Opal", "Topaz", "Ruby", "Diamond"),
            attack = 24
        ),
        CompendiumEntry(
            id = 105,
            name = "Moblin",
            category = CompendiumCategory.MONSTERS,
            subCategory = "Moblin",
            description = "A heavyweight humanoid monster that towers over Bokoblins. They possess immense physical strength and will pick up Bokoblins to throw at you.",
            locations = listOf("Hyrule Field", "Eldin"),
            drops = listOf("Moblin Horn", "Moblin Fang"),
            attack = 2
        ),
        CompendiumEntry(
            id = 114,
            name = "Lizalfos",
            category = CompendiumCategory.MONSTERS,
            subCategory = "Lizalfos",
            description = "Quick and agile reptilian monsters that blend into their surroundings like chameleons. They attack swiftly with spear jabs and jumping strikes.",
            locations = listOf("Lanayru Wetlands", "Gerudo Canyon"),
            drops = listOf("Lizalfos Horn", "Lizalfos Talon"),
            attack = 2
        ),
        CompendiumEntry(
            id = 120,
            name = "Lynel",
            category = CompendiumCategory.MONSTERS,
            subCategory = "Lynel",
            description = "Centaur-like beasts that have inhabited Hyrule since ancient times. Equipped with deadly weapon combos, devastating charge attacks, and elemental arrows.",
            locations = listOf("Ploymus Mountain", "Hyrule Field"),
            drops = listOf("Lynel Horn", "Lynel Hoof", "Lynel Guts", "Shock Arrow"),
            attack = 30
        ),
        CompendiumEntry(
            id = 123,
            name = "Silver Lynel",
            category = CompendiumCategory.MONSTERS,
            subCategory = "Lynel",
            description = "The most ferocious Lynel variant created by Ganon's dark magic. Capable of unleashing massive fire explosions and surviving relentless flurry rushes.",
            locations = listOf("Coliseum Ruins", "North Tabantha Snowfield"),
            drops = listOf("Lynel Horn", "Lynel Hoof", "Lynel Guts", "Topaz", "Ruby", "Sapphire", "Star Fragment"),
            attack = 58
        ),
        CompendiumEntry(
            id = 130,
            name = "Guardian Stalker",
            category = CompendiumCategory.MONSTERS,
            subCategory = "Ancient Machine",
            description = "Ancient Sheikah mobile autonomous battle machines corrupted by Calamity Ganon. Their tracking laser charges quickly and fires an explosive lethal beam.",
            locations = listOf("Hyrule Field", "Hyrule Castle"),
            drops = listOf("Ancient Screw", "Ancient Spring", "Ancient Gear", "Ancient Shaft", "Ancient Core", "Giant Ancient Core"),
            attack = 50
        ),
        CompendiumEntry(
            id = 135,
            name = "Hinox",
            category = CompendiumCategory.MONSTERS,
            subCategory = "Giant",
            description = "A massive cyclopean giant sleeping peacefully in forest glades. You can steal weapons directly from its necklace while it slumbers or shoot its eye with an arrow.",
            locations = listOf("Faron Woods", "West Necluda"),
            drops = listOf("Hinox Toenail", "Hinox Tooth", "Hinox Guts", "Apples", "Roasted Bass"),
            attack = 24
        ),
        CompendiumEntry(
            id = 140,
            name = "Stone Talus",
            category = CompendiumCategory.MONSTERS,
            subCategory = "Mineral Giant",
            description = "A giant rock creature disguised as an ordinary boulder. Climb atop its stony body to strike the glowing mineral ore deposit on its back.",
            locations = listOf("Great Plateau", "Dueling Peaks"),
            drops = listOf("Flint", "Amber", "Opal", "Ruby", "Diamond"),
            attack = 20
        ),
        CompendiumEntry(
            id = 145,
            name = "Molduga",
            category = CompendiumCategory.MONSTERS,
            subCategory = "Sand Leviathan",
            description = "A gargantuan monster that swims through the Gerudo Desert sands. Blind but sensitive to ground vibrations, it can be baited into swallowing Remote Bombs.",
            locations = listOf("Gerudo Desert"),
            drops = listOf("Molduga Fin", "Molduga Guts", "Treasure Chest"),
            attack = 60
        ),
        CompendiumEntry(
            id = 150,
            name = "Calamity Ganon",
            category = CompendiumCategory.MONSTERS,
            subCategory = "Incarnation of Malice",
            description = "The primordial evil that destroyed the Kingdom of Hyrule 100 years ago. An amalgamation of Guardian tech, malice, and dark sorcery within Hyrule Castle Sanctum.",
            locations = listOf("Hyrule Castle Sanctum"),
            drops = listOf(),
            attack = 100
        ),

        // Materials
        CompendiumEntry(
            id = 160,
            name = "Apple",
            category = CompendiumCategory.MATERIALS,
            subCategory = "Fruit",
            description = "A common fruit found on trees throughout Hyrule. Restores 0.5 hearts raw, or cook with other ingredients to make delicious pies and simmered dishes.",
            locations = listOf("Hyrule Field", "Great Plateau"),
            healingHearts = 0.5f
        ),
        CompendiumEntry(
            id = 161,
            name = "Hearty Durian",
            category = CompendiumCategory.MATERIALS,
            subCategory = "Fruit",
            description = "The king of hearty fruits. Famed for its pungent smell and dense flesh. Cooking just one completely restores Link's health and grants +4 yellow hearts!",
            locations = listOf("Faron Grasslands", "West Necluda"),
            healingHearts = 3.0f,
            effect = "Hearty (+4 Yellow Hearts per fruit)"
        ),
        CompendiumEntry(
            id = 162,
            name = "Big Hearty Radish",
            category = CompendiumCategory.MATERIALS,
            subCategory = "Plant",
            description = "A rare subterranean vegetable grown over decades. When cooked, it fully restores all hearts and grants a massive +5 extra temporary hearts.",
            locations = listOf("Akkala Highlands", "Lanayru Great Spring"),
            healingHearts = 4.0f,
            effect = "Hearty (+5 Yellow Hearts)"
        ),
        CompendiumEntry(
            id = 163,
            name = "Mighty Bananas",
            category = CompendiumCategory.MATERIALS,
            subCategory = "Fruit",
            description = "Sweet yellow bananas loved by the Yiga Clan. Cooking four of these grants Attack Up Level 3 for over 4 minutes, drastically boosting combat strength.",
            locations = listOf("Faron", "Yiga Clan Hideout"),
            healingHearts = 0.5f,
            effect = "Mighty (Attack Up)"
        ),
        CompendiumEntry(
            id = 164,
            name = "Ironshroom",
            category = CompendiumCategory.MATERIALS,
            subCategory = "Mushroom",
            description = "A metallic-capped mushroom rich in minerals. Fortifies the body with Defense Up Level 3 when cooked in generous batches.",
            locations = listOf("West Necluda", "East Necluda"),
            healingHearts = 0.5f,
            effect = "Tough (Defense Up)"
        ),
        CompendiumEntry(
            id = 165,
            name = "Silent Princess",
            category = CompendiumCategory.MATERIALS,
            subCategory = "Floral Herb",
            description = "Princess Zelda's beloved wild flower. Cannot be cultivated in captivity and only blooms in pristine wilderness. Boosts stealth and upgrades Champion's Tunic.",
            locations = listOf("Kakariko Village Fairy Fountain", "Korok Forest", "Hyrule Ridge"),
            healingHearts = 1.0f,
            effect = "Sneaky (Stealth Up) & Fairy Upgrades"
        ),
        CompendiumEntry(
            id = 166,
            name = "Endura Carrot",
            category = CompendiumCategory.MATERIALS,
            subCategory = "Plant",
            description = "Beloved by horses and travelers alike. Highly potent root that grants yellow bonus stamina wheels beyond the maximum stamina circle.",
            locations = listOf("Great Fairy Fountains", "Satori Mountain"),
            healingHearts = 2.0f,
            effect = "Enduring (Extra Stamina Wheels)"
        ),
        CompendiumEntry(
            id = 167,
            name = "Star Fragment",
            category = CompendiumCategory.MATERIALS,
            subCategory = "Celestial",
            description = "A mysterious incandescent shard that fell from a shooting star across the night sky. Essential for upgrading high-tier armor sets to 4 stars.",
            locations = listOf("Fell from Night Skies across Hyrule"),
            effect = "Ultimate Armor Upgrade Component"
        ),
        CompendiumEntry(
            id = 168,
            name = "Giant Ancient Core",
            category = CompendiumCategory.MATERIALS,
            subCategory = "Ancient Relic",
            description = "An extraordinarily rare power core harvested from fallen Guardian Stalkers and Skywatchers. Used by Cherry at Akkala Tech Lab for Ancient Armor & Weapons.",
            locations = listOf("Guardian Stalkers", "Guardian Skywatchers", "Shrine Chests"),
            effect = "Ancient Weaponry Crafting"
        ),

        // Equipment
        CompendiumEntry(
            id = 200,
            name = "Master Sword",
            category = CompendiumCategory.EQUIPMENT,
            subCategory = "One-Handed Sword",
            description = "The legendary blade that seals the darkness. Rests in the Sacred Korok Forest. Powered up to 60 Attack and glows radiant blue in the presence of Calamity.",
            locations = listOf("Korok Forest (Requires 13 Hearts)"),
            attack = 30,
            effect = "Energy-based durability, Awakens to 60 ATK near Malice, Shoots Sword Beams at full health"
        ),
        CompendiumEntry(
            id = 201,
            name = "Hylian Shield",
            category = CompendiumCategory.EQUIPMENT,
            subCategory = "Shield",
            description = "The ultimate defensive shield handed down through the Royal Family. Possesses an unfathomable 800 durability and 90 Shield Guard power.",
            locations = listOf("Hyrule Castle Lockup (Defeat Stalnox)"),
            defense = 90,
            effect = "800 Durability (Highest in the game)"
        ),
        CompendiumEntry(
            id = 202,
            name = "Bow of Light",
            category = CompendiumCategory.EQUIPMENT,
            subCategory = "Bow",
            description = "Princess Zelda's sacred bow infused with the light of the goddesses. Fires endless radiant light arrows in a flat, straight trajectory with 100 Attack.",
            locations = listOf("Final Battle with Dark Beast Ganon"),
            attack = 100,
            effect = "Infinite Light Arrows, Flat Trajectory"
        ),
        CompendiumEntry(
            id = 203,
            name = "Savage Lynel Crusher",
            category = CompendiumCategory.EQUIPMENT,
            subCategory = "Two-Handed Weapon",
            description = "A brutal bludgeon forged from metal harvested from the highest peaks. Its sheer weight smashes through enemy shields in a single blow.",
            locations = listOf("Wielded by Silver Lynels"),
            attack = 78,
            effect = "Devastating spin attack damage"
        ),
        CompendiumEntry(
            id = 204,
            name = "Savage Lynel Bow",
            category = CompendiumCategory.EQUIPMENT,
            subCategory = "Bow",
            description = "Steel bow capable of firing 3 or 5 arrows simultaneously in a lethal spread. Combined with Bomb Arrows, it obliterates encampments in seconds.",
            locations = listOf("Wielded by Silver Lynels"),
            attack = 32,
            effect = "Fires 3x or 5x arrows per single shot"
        ),
        CompendiumEntry(
            id = 205,
            name = "Ancient Short Sword",
            category = CompendiumCategory.EQUIPMENT,
            subCategory = "One-Handed Sword",
            description = "Crafted by Akkala Ancient Tech Lab using lost Sheikah science. Deals +80% amplified damage when paired with the upgraded Ancient Armor set.",
            locations = listOf("Akkala Ancient Tech Lab"),
            attack = 40,
            effect = "Ancient Proficiency (+80% Damage Bonus)"
        ),

        // Treasure
        CompendiumEntry(
            id = 350,
            name = "Treasure Chest",
            category = CompendiumCategory.TREASURE,
            subCategory = "Chest",
            description = "Ancient chests containing rupees, gemstones, or rare gear. Often hidden underwater (requiring Magnesis) or buried deep beneath soft soil.",
            locations = listOf("Hyrule Wide", "Shrines"),
            effect = "Contains Rupees, Gems, or Rare Weapons"
        ),
        CompendiumEntry(
            id = 351,
            name = "Rare Ore Deposit",
            category = CompendiumCategory.TREASURE,
            subCategory = "Mineral",
            description = "A sparkling dark stone vein glinting with golden flecks. Shatter it with an Iron Sledgehammer or bomb to yield Rubies, Sapphires, and Diamonds.",
            locations = listOf("Eldin Mountains", "Gerudo Highlands", "Tabantha"),
            drops = listOf("Topaz", "Ruby", "Sapphire", "Diamond")
        ),
        CompendiumEntry(
            id = 352,
            name = "Luminous Stone Deposit",
            category = CompendiumCategory.TREASURE,
            subCategory = "Mineral",
            description = "Glowing turquoise mineral veins that illuminate the night. Can be traded to Ledo in Zora's Domain for precious Diamonds.",
            locations = listOf("Lanayru Great Spring", "Dueling Peaks"),
            drops = listOf("Luminous Stone", "Flint")
        )
    )

    // --- Cooking Ingredients Data ---
    val ingredients: List<Ingredient> = listOf(
        // Fruits
        Ingredient("durian", "Hearty Durian", IngredientCategory.FRUIT, 3.0f, CookingEffect.HEARTY, effectPoints = 4, iconSymbol = "🍈", description = "King of fruits! Grants full recovery + 4 yellow hearts per fruit."),
        Ingredient("apple", "Apple", IngredientCategory.FRUIT, 0.5f, CookingEffect.NONE, iconSymbol = "🍎", description = "Crisp, sweet Hyrule apple."),
        Ingredient("banana", "Mighty Bananas", IngredientCategory.FRUIT, 0.5f, CookingEffect.MIGHTY, effectPoints = 2, baseDurationSeconds = 50, iconSymbol = "🍌", description = "Yiga clan favorite. 4 bananas grant Attack Up Lv 3."),
        Ingredient("palm_fruit", "Palm Fruit", IngredientCategory.FRUIT, 1.0f, CookingEffect.NONE, iconSymbol = "🥥", description = "Coconut harvested from tropical palms in Faron."),
        Ingredient("wildberry", "Wildberry", IngredientCategory.FRUIT, 0.5f, CookingEffect.NONE, iconSymbol = "🫐", description = "Tart sweet berries from the snowy Gerudo Highlands."),
        Ingredient("spicy_pepper", "Spicy Pepper", IngredientCategory.FRUIT, 0.5f, CookingEffect.SPICY, effectPoints = 1, baseDurationSeconds = 150, iconSymbol = "🌶️", description = "Essential for Cold Resistance on the snowy Great Plateau."),
        Ingredient("hydromelon", "Hydromelon", IngredientCategory.FRUIT, 0.5f, CookingEffect.CHILLY, effectPoints = 1, baseDurationSeconds = 150, iconSymbol = "🍉", description = "Cooling melon for surviving the scorching Gerudo desert."),
        Ingredient("voltfruit", "Voltfruit", IngredientCategory.FRUIT, 0.5f, CookingEffect.ELECTRO, effectPoints = 1, baseDurationSeconds = 150, iconSymbol = "🌵", description = "Cactus fruit insulating Link against electric shocks."),

        // Mushrooms
        Ingredient("hearty_truffle", "Hearty Truffle", IngredientCategory.MUSHROOM, 2.0f, CookingEffect.HEARTY, effectPoints = 1, iconSymbol = "🍄", description = "Subterranean mushroom granting full recovery + 1 yellow heart."),
        Ingredient("big_hearty_truffle", "Big Hearty Truffle", IngredientCategory.MUSHROOM, 3.0f, CookingEffect.HEARTY, effectPoints = 4, iconSymbol = "🍄", description = "Giant hearty truffle granting full recovery + 4 yellow hearts."),
        Ingredient("hylian_shroom", "Hylian Shroom", IngredientCategory.MUSHROOM, 0.5f, CookingEffect.NONE, iconSymbol = "🍄", description = "Classic common mushroom found near tree trunks."),
        Ingredient("endura_shroom", "Endura Shroom", IngredientCategory.MUSHROOM, 1.0f, CookingEffect.ENDURING, effectPoints = 1, iconSymbol = "🍄", description = "Yellow-striped cap granting bonus stamina."),
        Ingredient("stamella_shroom", "Stamella Shroom", IngredientCategory.MUSHROOM, 0.5f, CookingEffect.ENERGIZING, effectPoints = 1, iconSymbol = "🍄", description = "Green glowing mushroom restoring green stamina wheels."),
        Ingredient("razorshroom", "Razorshroom", IngredientCategory.MUSHROOM, 0.5f, CookingEffect.MIGHTY, effectPoints = 1, baseDurationSeconds = 50, iconSymbol = "🍄", description = "Red mushroom that sharpens weapon attack power."),
        Ingredient("ironshroom", "Ironshroom", IngredientCategory.MUSHROOM, 0.5f, CookingEffect.TOUGH, effectPoints = 1, baseDurationSeconds = 50, iconSymbol = "🍄", description = "Steel-hard mushroom boosting Link's defense armor."),
        Ingredient("silent_shroom", "Silent Shroom", IngredientCategory.MUSHROOM, 0.5f, CookingEffect.SNEAKY, effectPoints = 1, baseDurationSeconds = 120, iconSymbol = "🍄", description = "Luminescent mushroom damping footsteps for stealth."),
        Ingredient("rushroom", "Rushroom", IngredientCategory.MUSHROOM, 0.5f, CookingEffect.HASTY, effectPoints = 1, baseDurationSeconds = 60, iconSymbol = "🍄", description = "Clings to cliff faces, boosting sprint and climb speed."),

        // Meat & Poultry
        Ingredient("raw_meat", "Raw Meat", IngredientCategory.MEAT, 1.0f, CookingEffect.NONE, iconSymbol = "🥩", description = "Cut of fresh game meat. Cook for a hearty meat skewer."),
        Ingredient("raw_prime_meat", "Raw Prime Meat", IngredientCategory.MEAT, 1.5f, CookingEffect.NONE, iconSymbol = "🥩", description = "High-quality marbled red meat."),
        Ingredient("raw_gourmet_meat", "Raw Gourmet Meat", IngredientCategory.MEAT, 3.0f, CookingEffect.NONE, iconSymbol = "🥩", description = "Finest grade of meat in Hyrule! Restores huge hearts."),
        Ingredient("raw_bird_thigh", "Raw Bird Drumstick", IngredientCategory.MEAT, 1.0f, CookingEffect.NONE, iconSymbol = "🍗", description = "Poultry drumstick from common birds."),

        // Fish & Seafood
        Ingredient("hearty_bass", "Hearty Bass", IngredientCategory.FISH, 2.0f, CookingEffect.HEARTY, effectPoints = 2, iconSymbol = "🐟", description = "Prized fish granting full recovery + 2 yellow hearts."),
        Ingredient("hyrule_bass", "Hyrule Bass", IngredientCategory.FISH, 1.0f, CookingEffect.NONE, iconSymbol = "🐟", description = "Standard freshwater fish of Hyrule's lakes."),
        Ingredient("mighty_carp", "Mighty Carp", IngredientCategory.FISH, 1.0f, CookingEffect.MIGHTY, effectPoints = 1, baseDurationSeconds = 50, iconSymbol = "🐟", description = "River carp that increases Link's attack power."),
        Ingredient("armored_carp", "Armored Carp", IngredientCategory.FISH, 1.0f, CookingEffect.TOUGH, effectPoints = 1, baseDurationSeconds = 50, iconSymbol = "🐟", description = "Sturdy fish boosting defense against monster attacks."),
        Ingredient("sizzlefin_trout", "Sizzlefin Trout", IngredientCategory.FISH, 1.0f, CookingEffect.SPICY, effectPoints = 3, baseDurationSeconds = 150, iconSymbol = "🐟", description = "Warm thermal trout granting Cold Resistance."),
        Ingredient("chillfin_trout", "Chillfin Trout", IngredientCategory.FISH, 1.0f, CookingEffect.CHILLY, effectPoints = 3, baseDurationSeconds = 150, iconSymbol = "🐟", description = "Glacial trout granting Heat Resistance."),
        Ingredient("stealthfin_trout", "Stealthfin Trout", IngredientCategory.FISH, 1.0f, CookingEffect.SNEAKY, effectPoints = 2, baseDurationSeconds = 120, iconSymbol = "🐟", description = "Phosphorescent trout in Lake Saria that boosts stealth."),
        Ingredient("sneaky_river_snail", "Sneaky River Snail", IngredientCategory.FISH, 1.0f, CookingEffect.SNEAKY, effectPoints = 1, baseDurationSeconds = 120, iconSymbol = "🐚", description = "Freshwater snail dampening footsteps."),

        // Plants & Herbs
        Ingredient("hearty_radish", "Hearty Radish", IngredientCategory.PLANT, 2.5f, CookingEffect.HEARTY, effectPoints = 3, iconSymbol = "🥕", description = "Spotted root granting full recovery + 3 extra hearts."),
        Ingredient("big_hearty_radish", "Big Hearty Radish", IngredientCategory.PLANT, 4.0f, CookingEffect.HEARTY, effectPoints = 5, iconSymbol = "🥕", description = "Rare ancient radish granting full recovery + 5 extra hearts!"),
        Ingredient("endura_carrot", "Endura Carrot", IngredientCategory.PLANT, 2.0f, CookingEffect.ENDURING, effectPoints = 2, iconSymbol = "🥕", description = "Beloved root that provides bonus yellow stamina wheels."),
        Ingredient("silent_princess", "Silent Princess", IngredientCategory.PLANT, 1.0f, CookingEffect.SNEAKY, effectPoints = 3, baseDurationSeconds = 120, iconSymbol = "🌸", description = "Zelda's sacred flower. Maximize stealth and purity."),
        Ingredient("mighty_thistle", "Mighty Thistle", IngredientCategory.PLANT, 0.0f, CookingEffect.MIGHTY, effectPoints = 1, baseDurationSeconds = 50, iconSymbol = "🌿", description = "Prickly thistle that sharply boosts attack power."),
        Ingredient("armoranth", "Armoranth", IngredientCategory.PLANT, 0.0f, CookingEffect.TOUGH, effectPoints = 1, baseDurationSeconds = 50, iconSymbol = "🌿", description = "Hard leafy herb granting defense up."),
        Ingredient("warm_safflina", "Warm Safflina", IngredientCategory.PLANT, 0.0f, CookingEffect.SPICY, effectPoints = 1, baseDurationSeconds = 150, iconSymbol = "🌺", description = "Desert flower with warm petals against freezing cold."),
        Ingredient("cool_safflina", "Cool Safflina", IngredientCategory.PLANT, 0.0f, CookingEffect.CHILLY, effectPoints = 1, baseDurationSeconds = 150, iconSymbol = "🌺", description = "Glacial flower cooling Link against desert heat."),
        Ingredient("electric_safflina", "Electric Safflina", IngredientCategory.PLANT, 0.0f, CookingEffect.ELECTRO, effectPoints = 1, baseDurationSeconds = 150, iconSymbol = "🌺", description = "Generates slight static, insulating against shock."),

        // Critters (for Elixirs)
        Ingredient("energetic_rhino_beetle", "Energetic Rhino Beetle", IngredientCategory.CRITTER, 0.0f, CookingEffect.ENERGIZING, effectPoints = 3, iconSymbol = "🪲", description = "Rare beetle. Brew with monster parts for Stamina Elixir."),
        Ingredient("fireproof_lizard", "Fireproof Lizard", IngredientCategory.CRITTER, 0.0f, CookingEffect.FIREPROOF, effectPoints = 1, baseDurationSeconds = 150, iconSymbol = "🦎", description = "Lizard found on Goron trails. Brews Fireproof Elixir!"),
        Ingredient("smotherwing_butterfly", "Smotherwing Butterfly", IngredientCategory.CRITTER, 0.0f, CookingEffect.FIREPROOF, effectPoints = 2, baseDurationSeconds = 150, iconSymbol = "🦋", description = "Volcanic butterfly for potent Fireproof Elixirs."),
        Ingredient("sunset_firefly", "Sunset Firefly", IngredientCategory.CRITTER, 0.0f, CookingEffect.SNEAKY, effectPoints = 1, baseDurationSeconds = 120, iconSymbol = "✨", description = "Luminescent insect for brewing Sneaky Elixirs."),
        Ingredient("warm_darner", "Warm Darner", IngredientCategory.CRITTER, 0.0f, CookingEffect.SPICY, effectPoints = 2, baseDurationSeconds = 150, iconSymbol = "🪰", description = "Dragonfly generating heat for Spicy Elixirs."),
        Ingredient("cold_darner", "Cold Darner", IngredientCategory.CRITTER, 0.0f, CookingEffect.CHILLY, effectPoints = 2, baseDurationSeconds = 150, iconSymbol = "🪰", description = "Dragonfly absorbing heat for Chilly Elixirs."),
        Ingredient("hot_footed_frog", "Hot-Footed Frog", IngredientCategory.CRITTER, 0.0f, CookingEffect.HASTY, effectPoints = 2, baseDurationSeconds = 60, iconSymbol = "🐸", description = "Swift amphibian for brewing Hasty Speed Elixirs."),

        // Monster Parts (Elixir Alchemical Catalysts)
        Ingredient("bokoblin_horn", "Bokoblin Horn", IngredientCategory.MONSTER_PART, 0.0f, CookingEffect.NONE, iconSymbol = "🦴", description = "Monster catalyst. Extends elixir duration by 70s."),
        Ingredient("bokoblin_guts", "Bokoblin Guts", IngredientCategory.MONSTER_PART, 0.0f, CookingEffect.NONE, iconSymbol = "🫀", description = "Rare organ. Extends elixir duration by 190 seconds!"),
        Ingredient("lynel_horn", "Lynel Horn", IngredientCategory.MONSTER_PART, 0.0f, CookingEffect.NONE, iconSymbol = "📯", description = "Potent catalyst extending elixir duration by 70 seconds."),
        Ingredient("lynel_guts", "Lynel Guts", IngredientCategory.MONSTER_PART, 0.0f, CookingEffect.NONE, iconSymbol = "🫀", description = "Ultimate monster organ. Extends elixir duration by 190s!"),

        // Minerals (causes Rock-Hard food if cooked!)
        Ingredient("amber", "Amber", IngredientCategory.MINERAL, 0.0f, CookingEffect.NONE, iconSymbol = "🔶", description = "Fossilized tree resin. Cooking this creates Rock-Hard Food."),
        Ingredient("diamond", "Diamond", IngredientCategory.MINERAL, 0.0f, CookingEffect.NONE, iconSymbol = "💎", description = "Extremely rare gem used for Great Fairy & Champion weapon repairs.")
    )

    // --- Iconic Canonical Recipes ---
    val cookbookRecipes: List<CookbookRecipe> = listOf(
        CookbookRecipe(
            id = "hearty_durians_simmered",
            name = "Hearty Simmered Fruit (5 Durians)",
            effect = CookingEffect.HEARTY,
            category = "Full Recovery",
            ingredientNames = listOf("Hearty Durian", "Hearty Durian", "Hearty Durian", "Hearty Durian", "Hearty Durian"),
            description = "The most famous dish in Breath of the Wild! Restores ALL hearts completely and grants +20 yellow extra hearts.",
            heartsPreview = 30.0f
        ),
        CookbookRecipe(
            id = "mighty_meat_mushroom",
            name = "Mighty Meat and Mushroom Skewer",
            effect = CookingEffect.MIGHTY,
            category = "Attack Up",
            ingredientNames = listOf("Raw Meat", "Razorshroom", "Razorshroom", "Razorshroom", "Mighty Bananas"),
            description = "Level 3 Attack Up meal. Boosts all damage dealt by 50% for high-intensity boss battles against Lynels and Ganon.",
            heartsPreview = 7.0f
        ),
        CookbookRecipe(
            id = "enduring_wild_greens",
            name = "Enduring Fried Wild Greens",
            effect = CookingEffect.ENDURING,
            category = "Extra Stamina",
            ingredientNames = listOf("Endura Carrot", "Endura Carrot", "Endura Carrot", "Endura Carrot", "Endura Carrot"),
            description = "Instantly refills stamina and adds 2 full extra wheels of yellow stamina for scaling sheer mountain cliffs.",
            heartsPreview = 20.0f
        ),
        CookbookRecipe(
            id = "spicy_meat_seafood",
            name = "Spicy Meat and Seafood Fry",
            effect = CookingEffect.SPICY,
            category = "Cold Resistance",
            ingredientNames = listOf("Hyrule Bass", "Raw Meat", "Spicy Pepper"),
            description = "The Old Man's forgotten recipe on the Great Plateau! Grants Link the Warm Doublet.",
            heartsPreview = 5.0f
        ),
        CookbookRecipe(
            id = "fireproof_elixir",
            name = "Fireproof Elixir",
            effect = CookingEffect.FIREPROOF,
            category = "Volcano Protection",
            ingredientNames = listOf("Fireproof Lizard", "Fireproof Lizard", "Fireproof Lizard", "Bokoblin Guts"),
            description = "Critical elixir for traversing the blazing slopes of Eldin Volcano without catching fire.",
            heartsPreview = 0.0f
        ),
        CookbookRecipe(
            id = "sneaky_steamed_fish",
            name = "Sneaky Steamed Fish",
            effect = CookingEffect.SNEAKY,
            category = "Stealth",
            ingredientNames = listOf("Stealthfin Trout", "Silent Princess", "Silent Shroom"),
            description = "Suppresses footstep sounds completely, enabling easy Sneakstrikes (8x damage multiplier) on sleeping camps.",
            heartsPreview = 5.0f
        ),
        CookbookRecipe(
            id = "tough_mushroom_skewer",
            name = "Tough Mushroom Skewer",
            effect = CookingEffect.TOUGH,
            category = "Defense Up",
            ingredientNames = listOf("Ironshroom", "Ironshroom", "Ironshroom", "Ironshroom", "Ironshroom"),
            description = "Tough Level 3 defense armor meal. Reduces incoming damage by up to 75%.",
            heartsPreview = 5.0f
        ),
        CookbookRecipe(
            id = "hasty_elixir",
            name = "Hasty Elixir (Speed Lv 3)",
            effect = CookingEffect.HASTY,
            category = "Movement Speed",
            ingredientNames = listOf("Hot-Footed Frog", "Hot-Footed Frog", "Hot-Footed Frog", "Lynel Guts"),
            description = "Provides huge speed boost for running, climbing, and swimming across the expansive plains of Hyrule.",
            heartsPreview = 0.0f
        )
    )

    // --- Shrines Data (Key iconic Shrines across all regions) ---
    val shrines: List<ShrineInfo> = listOf(
        // Great Plateau
        ShrineInfo(1, "Oman Au", "Magnesis Trial", "Great Plateau", false, null, "Use Magnesis to lift the metal plates from the floor and clear boulders blockading the passageway."),
        ShrineInfo(2, "Ja Baij", "Bomb Trial", "Great Plateau", false, null, "Switch between Round and Square Remote Bombs to shatter cracked stone walls and launch bombs using catapults."),
        ShrineInfo(3, "Owa Daim", "Stasis Trial", "Great Plateau", false, null, "Freeze moving gears with Stasis, then freeze the giant rolling boulder and strike it with a hammer to store kinetic force."),
        ShrineInfo(4, "Keh Namut", "Cryonis Trial", "Great Plateau", false, null, "Create ice pillars in water surfaces to lift gates, create staircases, and raise up the seesaw ramp."),

        // Dueling Peaks & Kakariko
        ShrineInfo(5, "Bosh Kala", "The Wind Guides You", "Dueling Peaks", false, null, "Glide with your paraglider riding air updrafts across deep ravines. Catch chests blown by wind."),
        ShrineInfo(6, "Ta'lom Naeg", "Teaching of the Combat", "Dueling Peaks", false, null, "Kakariko combat tutorial: master Side Hop Flurry Rush, Backflip Flurry Rush, and Perfect Guard shield parries."),
        ShrineInfo(7, "Ree Dahee", "Timing Is Critical", "Dueling Peaks", false, null, "Step on switches to tilt platforms and roll orange orbs into pressure sockets at the correct split-second."),
        ShrineInfo(8, "Shee Vaneer", "Twin Memories", "Dueling Peaks", true, "Twin Memories", "Look at the orb placement in Shee Venath on the southern peak and replicate the inverted pattern here."),
        ShrineInfo(9, "Shee Venath", "Twin Memories", "Dueling Peaks", true, "Twin Memories", "Mirror the orb arrangement of Shee Vaneer from the twin mountain summit."),

        // Hateno
        ShrineInfo(10, "Myahm Agana", "Myahm Agana Apparatus", "Hateno", false, null, "Tilt the motion-control maze to guide the ball to the exit. Pro tip: flip the controller upside down for a flat surface!"),
        ShrineInfo(11, "Kam Urog", "Trial of Passage", "Hateno", true, "The Cursed Statue", "Shoot the glowing cursed statue eyes at night near Fort Hateno to reveal the hidden entrance."),
        ShrineInfo(12, "Mezza Lo", "Ancient Trifecta", "Hateno", true, "The Crowned Beast", "Mount and ride a wild mountain buck onto the glowing pedestal as sung by Kass."),

        // Lanayru & Zora's Domain
        ShrineInfo(13, "Ne'ez Yohma", "Pushing Power", "Lanayru", false, null, "Zora's Domain central shrine: construct Cryonis ice walls to guide the rolling giant sphere past water spouts."),
        ShrineInfo(14, "Soh Kofi", "A Minor Test of Strength", "Lanayru", false, null, "Guardian Scout III duel: dodge spear lunges and parry laser beam charges with your shield."),
        ShrineInfo(15, "Daka Tuss", "Sunken Scoop", "Lanayru", false, null, "Use Magnesis on the metal basket scoop to catch underwater orbs and drop them into the cage receptacle."),

        // Central Hyrule
        ShrineInfo(16, "Rota Ooh", "Passing of the Gates", "Central Hyrule", false, null, "Strike the crystal switch with arrows to flip the rotating chamber while launching Link with the catapult."),
        ShrineInfo(17, "Kaam Ya'tak", "Trial of Power", "Central Hyrule", false, null, "Solve 6 consecutive kinetic puzzles using Magnesis wrecking balls, Stasis on battering rams, and flame arrows."),
        ShrineInfo(18, "Katah Chuki", "A Minor Test of Strength", "Central Hyrule", false, null, "Located near Quarry Ruins. Defeat Guardian Scout wielding Guardian Sword."),

        // Akkala
        ShrineInfo(19, "Tu Ka'lo", "Tu Ka'lo's Blessing", "Akkala", true, "Trial of the Labyrinth", "Navigate the winding maze of Lomei Labyrinth Island. Contains the Barbarian Helm!"),
        ShrineInfo(20, "Katosa Aug", "Katosa Aug Apparatus", "Akkala", false, null, "Use the Sheikah apparatus putt-putt hammer to strike the sphere across the goal hole."),
        ShrineInfo(21, "Ze Kasho", "Ze Kasho Apparatus", "Akkala", false, null, "Manipulate floating platforms to align pathways and tilt away laser beam obstructions."),

        // Eldin & Death Mountain
        ShrineInfo(22, "Shae Mo'sah", "Swinging Flames", "Eldin", false, null, "Burn foliage ropes with fire arrows to drop suspended lanterns and activate pressure plates."),
        ShrineInfo(23, "Sah Dahaj", "Power of Fire", "Eldin", false, null, "Burn dry leaves and wooden crates with torches to drop stone slabs."),
        ShrineInfo(24, "Gorae Torr", "Gorae Torr's Blessing", "Eldin", true, "The Gut Check Challenge", "Conquer the treacherous vertical climbing cliffs at Gut Check Rock to satisfy the Goron Brothers."),

        // Woodland & Korok Forest
        ShrineInfo(25, "Keo Ruug", "Fateful Stars", "Woodland", false, null, "Inside the Great Deku Tree's roots: count the constellations on the back wall and place orbs in the corresponding numerical rows."),
        ShrineInfo(26, "Daag Chokah", "Daag Chokah's Blessing", "Woodland", true, "The Lost Pilgrimage", "Carefully shadow the little Korok Oaki through the misty Lost Woods without being spotted."),
        ShrineInfo(27, "Kuhn Sidahh", "Kuhn Sidahh's Blessing", "Woodland", true, "Trial of Second Sight", "Follow the iron-eating hollow trees using Magnesis in the foggy woods."),

        // Hebra & Tabantha
        ShrineInfo(28, "Akh Va'quot", "Windmills", "Tabantha", false, null, "Rito Village shrine: spin wind turbines using pinwheels to open the gate."),
        ShrineInfo(29, "Goma Asaagh", "A Major Test of Strength", "Hebra", false, null, "Hidden beneath a colossal glacial ice block near Hebra Peak. Melt ice with fire and fight Guardian Scout IV."),
        ShrineInfo(30, "Mozo Shenno", "A Major Test of Strength", "Hebra", true, "The Bird in the Mountains", "Look down from Biron Snowshelf to spot the shadow bird formation."),

        // Gerudo
        ShrineInfo(31, "Daqo Chisay", "The Whole Picture", "Gerudo", false, null, "Gerudo Town entrance: arrange electric circuits using metal barrels and chests to connect electric currents."),
        ShrineInfo(32, "Kema Zoos", "A Delayed Delivery", "Gerudo", true, "The Silent Swordswomen", "Follow the directional statues of swordswomen pointing through the blinding Gerudo sandstorm."),
        ShrineInfo(33, "Dako Tah", "Electric Path", "Gerudo", true, "The Noble Canteen", "Help Pokki obtain the refreshing Noble Pursuit cocktail to access the shrine pedestal."),

        // Faron & Lake
        ShrineInfo(34, "Shai Utoh", "Halt the Tilt", "Faron", false, null, "Use Stasis on tilting seesaw bridges behind Lakeside Stable to reach the upper ledges."),
        ShrineInfo(35, "Shoqa Tatone", "A Modest Test of Strength", "Faron", true, "Guardian Slideshow", "Photograph three Guardian types (Stalker, Skywatcher, Scout) for Loone."),
        ShrineInfo(36, "Shae Katha", "Shae Katha's Blessing", "Faron", true, "The Serpent's Jaws", "Offer Farosh's Scale at the Spring of Courage in the dense tropical Faron rainforest.")
    )

    // --- Armor Sets Data ---
    val armorSets: List<ArmorSet> = listOf(
        ArmorSet(
            id = "champions_tunic",
            name = "Champion's Tunic",
            pieces = listOf("Champion's Tunic (Inscribed with Princess Zelda's embroidery)"),
            setBonus = "Display exact numeric Enemy HP bars above targets",
            defensePerTier = listOf(5, 8, 14, 22, 32),
            materialsGuide = listOf("Silent Princess flowers", "Farosh / Dinraal / Naydra Dragon Horns"),
            description = "In ancient Hyrule, this garment was worn only by the Champions chosen to pilot the Divine Beasts. Highest single armor defense in BotW (32 DEF at ★★★★)!"
        ),
        ArmorSet(
            id = "barbarian",
            name = "Barbarian Armor Set",
            pieces = listOf("Barbarian Helm", "Barbarian Armor", "Barbarian Leg Wraps"),
            setBonus = "Attack Up Level 3 (+50% Atk) & Charge Attack Stamina Reduction",
            defensePerTier = listOf(9, 15, 27, 36, 60),
            materialsGuide = listOf("Lynel Horns", "Lynel Hooves", "Lynel Guts", "Dragon Horns"),
            description = "Armor from an ancient warlike tribe in Faron. Equipping the full set boosts weapon damage output to catastrophic levels."
        ),
        ArmorSet(
            id = "ancient",
            name = "Ancient Armor Set",
            pieces = listOf("Ancient Helm", "Ancient Cuirass", "Ancient Greaves"),
            setBonus = "Ancient Proficiency (+80% Damage with Ancient & Guardian weapons) & Guardian Beam Resist",
            defensePerTier = listOf(12, 21, 36, 54, 84),
            materialsGuide = listOf("Ancient Screws", "Ancient Springs", "Ancient Gears", "Giant Ancient Cores"),
            description = "Crafted with Robbie's lost technology. Stacking Ancient Proficiency with Attack Up food deals the highest DPS in the entire game!"
        ),
        ArmorSet(
            id = "stealth",
            name = "Stealth (Sheikah) Set",
            pieces = listOf("Stealth Mask", "Stealth Chest Guard", "Stealth Tights"),
            setBonus = "Stealth Up Level 3 & Night Speed Up",
            defensePerTier = listOf(6, 12, 21, 33, 48),
            materialsGuide = listOf("Blue Nightshade", "Sneaky River Snail", "Silent Shrooms", "Silent Princess"),
            description = "Crafted by the Sheikah clan. Allows Link to sprint directly up to bugs, fairies, and enemies without alerting them."
        ),
        ArmorSet(
            id = "climbers",
            name = "Climbing Gear Set",
            pieces = listOf("Climber's Bandanna", "Climbing Gear", "Climbing Boots"),
            setBonus = "Climb Speed Up Level 3 & Climbing Jump Stamina Reduction",
            defensePerTier = listOf(9, 15, 27, 36, 60),
            materialsGuide = listOf("Keese Wings", "Rushroom", "Hot-Footed Frogs", "Swift Violets"),
            description = "Essential gear for exploring Hyrule's mountains. Halves stamina spent jumping up rock faces."
        ),
        ArmorSet(
            id = "flamebreaker",
            name = "Flamebreaker Set",
            pieces = listOf("Flamebreaker Helm", "Flamebreaker Armor", "Flamebreaker Boots"),
            setBonus = "Fireproof (Immunity to Burning & Lavastep)",
            defensePerTier = listOf(9, 15, 27, 36, 60),
            materialsGuide = listOf("Fireproof Lizards", "Moblin Horns", "Smotherwing Butterflies", "Hinox Guts"),
            description = "Heavy stone-lined Goron armor crafted to resist extreme volcanic heat and igniting on contact with lava."
        ),
        ArmorSet(
            id = "snowquill",
            name = "Snowquill Rito Set",
            pieces = listOf("Snowquill Headdress", "Snowquill Tunic", "Snowquill Trousers"),
            setBonus = "Cold Resistance Level 2 & Unfreezable (Immunity to Ice Traps)",
            defensePerTier = listOf(9, 15, 27, 36, 60),
            materialsGuide = listOf("Warm Darners", "Red Chuchu Jelly", "Sunshrooms", "Fire Keese Wings"),
            description = "Lined with soft Rito down feathers. Protects against absolute zero blizzards in the Hebra Mountains."
        ),
        ArmorSet(
            id = "zora",
            name = "Zora Armor Set",
            pieces = listOf("Zora Helm", "Zora Armor", "Zora Greaves"),
            setBonus = "Swim Speed Up Level 3, Waterfall Climbing & Spin Attack in Water",
            defensePerTier = listOf(9, 15, 27, 36, 60),
            materialsGuide = listOf("Lizalfos Horns", "Hyrule Bass", "Lizalfos Tails", "Hearty Bass"),
            description = "Crafted by Princess Mipha with love for Link. Grants the supernatural ability to swim directly up vertical waterfalls."
        )
    )

    // --- Champion Abilities ---
    val championAbilities: List<ChampionAbility> = listOf(
        ChampionAbility(
            name = "Mipha's Grace",
            champion = "Mipha (Zora Champion)",
            divineBeast = "Vah Ruta (Water)",
            region = "Lanayru",
            cooldownMinutes = 24,
            charges = 1,
            effect = "Instantly resurrects Link upon falling in battle, restoring all hearts + 5 extra yellow hearts.",
            quote = "\"It is my pleasure. Mipha's Grace is ready.\""
        ),
        ChampionAbility(
            name = "Revali's Gale",
            champion = "Revali (Rito Champion)",
            divineBeast = "Vah Medoh (Wind)",
            region = "Tabantha",
            cooldownMinutes = 6,
            charges = 3,
            effect = "Creates a powerful vertical wind vortex launching Link soaring high into the sky for paragliding.",
            quote = "\"Revali's Gale is now ready!\""
        ),
        ChampionAbility(
            name = "Daruk's Protection",
            champion = "Daruk (Goron Champion)",
            divineBeast = "Vah Rudania (Fire)",
            region = "Eldin",
            cooldownMinutes = 18,
            charges = 3,
            effect = "Automatically parries and deflects all incoming attacks, explosions, and Guardian lasers without taking damage.",
            quote = "\"Daruk's Protection is ready to roll!\""
        ),
        ChampionAbility(
            name = "Urbosa's Fury",
            champion = "Urbosa (Gerudo Champion)",
            divineBeast = "Vah Naboris (Lightning)",
            region = "Gerudo",
            cooldownMinutes = 12,
            charges = 3,
            effect = "Charges spin attack to summon a devastating lightning storm dealing 500 damage and electrocuting all nearby foes.",
            quote = "\"Urbosa's Fury is ready.\""
        )
    )
}
