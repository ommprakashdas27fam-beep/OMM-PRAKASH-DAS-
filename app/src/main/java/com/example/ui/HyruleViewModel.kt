package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SheikahAudio
import com.example.cooking.CookingEngine
import com.example.data.HyruleRepository
import com.example.data.models.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val icon: String) {
    HOME("Slate", "👁️"),
    COMPENDIUM("Compendium", "📖"),
    COOKING("Cooking Pot", "🍲"),
    SHRINES("Shrines", "⛩️"),
    RUNES("Runes", "⚡"),
    ARMOR("Armor Vault", "🛡️")
}

enum class BombType { ROUND, SQUARE }

class HyruleViewModel(application: Application) : AndroidViewModel(application) {
    val repository = HyruleRepository(application.applicationContext)

    // Navigation
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppTab) {
        SheikahAudio.playBeep(980f, 40)
        _currentTab.value = tab
    }

    // --- Compendium State ---
    private val _compendiumCategory = MutableStateFlow(CompendiumCategory.ALL)
    val compendiumCategory: StateFlow<CompendiumCategory> = _compendiumCategory.asStateFlow()

    private val _compendiumSearch = MutableStateFlow("")
    val compendiumSearch: StateFlow<String> = _compendiumSearch.asStateFlow()

    private val _compendiumFilterFavorites = MutableStateFlow(false)
    val compendiumFilterFavorites: StateFlow<Boolean> = _compendiumFilterFavorites.asStateFlow()

    fun setCompendiumCategory(cat: CompendiumCategory) {
        SheikahAudio.playBeep(750f, 30)
        _compendiumCategory.value = cat
    }

    fun setCompendiumSearch(query: String) {
        _compendiumSearch.value = query
    }

    fun toggleCompendiumFavoritesFilter() {
        SheikahAudio.playBeep(850f, 30)
        _compendiumFilterFavorites.value = !_compendiumFilterFavorites.value
    }

    fun toggleItemPhotographed(itemId: Int) {
        SheikahAudio.playCollectChime()
        repository.togglePhotographed(itemId)
    }

    fun toggleItemFavorite(itemId: Int) {
        SheikahAudio.playBeep(1100f, 50)
        repository.toggleFavorite(itemId)
    }

    // --- Cooking Pot State ---
    private val _potIngredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val potIngredients: StateFlow<List<Ingredient>> = _potIngredients.asStateFlow()

    private val _lastCookedDish = MutableStateFlow<CookedDish?>(null)
    val lastCookedDish: StateFlow<CookedDish?> = _lastCookedDish.asStateFlow()

    private val _isCooking = MutableStateFlow(false)
    val isCooking: StateFlow<Boolean> = _isCooking.asStateFlow()

    private val _cookingHistory = MutableStateFlow<List<CookedDish>>(emptyList())
    val cookingHistory: StateFlow<List<CookedDish>> = _cookingHistory.asStateFlow()

    fun addIngredientToPot(ingredient: Ingredient) {
        if (_potIngredients.value.size < 5) {
            SheikahAudio.playBeep(900f + (_potIngredients.value.size * 100f), 45)
            _potIngredients.value = _potIngredients.value + ingredient
        }
    }

    fun removeIngredientFromPot(index: Int) {
        val current = _potIngredients.value.toMutableList()
        if (index in current.indices) {
            SheikahAudio.playBeep(650f, 40)
            current.removeAt(index)
            _potIngredients.value = current
        }
    }

    fun clearPot() {
        SheikahAudio.playBeep(550f, 40)
        _potIngredients.value = emptyList()
    }

    fun loadRecipe(recipe: CookbookRecipe) {
        SheikahAudio.playRuneSelect()
        val loaded = mutableListOf<Ingredient>()
        for (ingName in recipe.ingredientNames.take(5)) {
            val found = repository.ingredients.find { it.name.equals(ingName, ignoreCase = true) }
            if (found != null) {
                loaded.add(found)
            }
        }
        _potIngredients.value = loaded
    }

    fun cookCurrentPot() {
        if (_potIngredients.value.isEmpty() || _isCooking.value) return
        viewModelScope.launch {
            _isCooking.value = true
            SheikahAudio.playBeep(440f, 150)
            delay(1200) // Cooking animation delay
            val result = CookingEngine.cook(_potIngredients.value)
            _isCooking.value = false
            _lastCookedDish.value = result
            _cookingHistory.value = listOf(result) + _cookingHistory.value
            _potIngredients.value = emptyList()
            SheikahAudio.playCookingFanfare()
        }
    }

    fun dismissCookResult() {
        _lastCookedDish.value = null
    }

    // --- Shrines State ---
    private val _shrineRegionFilter = MutableStateFlow("All")
    val shrineRegionFilter: StateFlow<String> = _shrineRegionFilter.asStateFlow()

    private val _shrineSearch = MutableStateFlow("")
    val shrineSearch: StateFlow<String> = _shrineSearch.asStateFlow()

    fun setShrineRegionFilter(region: String) {
        SheikahAudio.playBeep(700f, 35)
        _shrineRegionFilter.value = region
    }

    fun setShrineSearch(query: String) {
        _shrineSearch.value = query
    }

    fun toggleShrineCompletion(shrineId: Int) {
        val wasCompleted = repository.completedShrines.value.contains(shrineId)
        if (!wasCompleted) {
            SheikahAudio.playShrineChime()
        } else {
            SheikahAudio.playBeep(600f, 40)
        }
        repository.toggleShrineCompleted(shrineId)
    }

    // --- Armor State ---
    fun updateArmorStarLevel(armorId: String, level: Int) {
        SheikahAudio.playCollectChime()
        repository.setArmorStarLevel(armorId, level)
    }

    // --- Runes Interactive Simulation State ---
    private val _selectedRune = MutableStateFlow("bomb") // bomb, magnesis, stasis, cryonis, sensor
    val selectedRune: StateFlow<String> = _selectedRune.asStateFlow()

    fun selectRune(rune: String) {
        SheikahAudio.playRuneSelect()
        _selectedRune.value = rune
    }

    // Remote Bomb
    private val _bombType = MutableStateFlow(BombType.ROUND)
    val bombType: StateFlow<BombType> = _bombType.asStateFlow()

    private val _isBombPlaced = MutableStateFlow(false)
    val isBombPlaced: StateFlow<Boolean> = _isBombPlaced.asStateFlow()

    private val _bombExploding = MutableStateFlow(false)
    val bombExploding: StateFlow<Boolean> = _bombExploding.asStateFlow()

    fun switchBombType(type: BombType) {
        SheikahAudio.playBeep(1000f, 50)
        _bombType.value = type
    }

    fun deployBomb() {
        SheikahAudio.playBeep(1200f, 80)
        _isBombPlaced.value = true
    }

    fun detonateBomb() {
        if (!_isBombPlaced.value) return
        viewModelScope.launch {
            _isBombPlaced.value = false
            _bombExploding.value = true
            SheikahAudio.playDetonation()
            delay(500)
            _bombExploding.value = false
        }
    }

    // Stasis
    private val _isStasisActive = MutableStateFlow(false)
    val isStasisActive: StateFlow<Boolean> = _isStasisActive.asStateFlow()

    private val _stasisHits = MutableStateFlow(0)
    val stasisHits: StateFlow<Int> = _stasisHits.asStateFlow()

    private val _stasisTimerSeconds = MutableStateFlow(5)
    val stasisTimerSeconds: StateFlow<Int> = _stasisTimerSeconds.asStateFlow()

    private val _stasisLaunched = MutableStateFlow(false)
    val stasisLaunched: StateFlow<Boolean> = _stasisLaunched.asStateFlow()

    private var stasisJob: Job? = null

    fun activateStasis() {
        stasisJob?.cancel()
        _isStasisActive.value = true
        _stasisHits.value = 0
        _stasisTimerSeconds.value = 5
        _stasisLaunched.value = false
        SheikahAudio.playBeep(1100f, 100)

        stasisJob = viewModelScope.launch {
            while (_stasisTimerSeconds.value > 0) {
                delay(1000)
                _stasisTimerSeconds.value -= 1
                SheikahAudio.playBeep(800f + (_stasisTimerSeconds.value * 50f), 40)
            }
            _isStasisActive.value = false
            _stasisLaunched.value = true
            SheikahAudio.playDetonation()
            delay(800)
            _stasisLaunched.value = false
        }
    }

    fun hitStasisObject() {
        if (_isStasisActive.value && _stasisHits.value < 15) {
            _stasisHits.value += 1
            // Ascending pitch per hit as force accumulates!
            SheikahAudio.playBeep(500f + (_stasisHits.value * 60f), 50)
        }
    }

    // Cryonis
    private val _cryonisPillars = MutableStateFlow(0) // max 3
    val cryonisPillars: StateFlow<Int> = _cryonisPillars.asStateFlow()

    fun spawnPillar() {
        if (_cryonisPillars.value < 3) {
            _cryonisPillars.value += 1
            SheikahAudio.playBeep(850f + (_cryonisPillars.value * 150f), 80)
        } else {
            // Replaces 1st
            _cryonisPillars.value = 3
            SheikahAudio.playBeep(1200f, 80)
        }
    }

    fun shatterPillars() {
        SheikahAudio.playDetonation()
        _cryonisPillars.value = 0
    }

    // Sheikah Sensor Radar
    private val _sensorTarget = MutableStateFlow("Silent Princess")
    val sensorTarget: StateFlow<String> = _sensorTarget.asStateFlow()

    private val _sensorProximity = MutableStateFlow(0.65f) // 0.0 to 1.0
    val sensorProximity: StateFlow<Float> = _sensorProximity.asStateFlow()

    fun setSensorTarget(target: String) {
        SheikahAudio.playRuneSelect()
        _sensorTarget.value = target
    }

    fun setSensorProximity(prox: Float) {
        _sensorProximity.value = prox.coerceIn(0.0f, 1.0f)
        SheikahAudio.playRadarPing(prox)
    }

    fun pingRadar() {
        SheikahAudio.playRadarPing(_sensorProximity.value)
    }
}
