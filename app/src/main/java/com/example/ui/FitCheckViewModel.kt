package com.example.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class UserProfile(
    val email: String,
    val name: String,
    val profilePictureUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150"
)

data class StylistMessage(
    val message: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class TryOnState(
    val activeModel: ModelPreset = ModelPreset.presets.first(),
    val topItem: ClothingEntity? = null,
    val bottomItem: ClothingEntity? = null,
    val shoesItem: ClothingEntity? = null,
    val jacketItem: ClothingEntity? = null,
    val accessoryItem: ClothingEntity? = null,
    val currentStep: String = "", // "idle", "segmenting", "warping", "rendering", "complete"
    val progress: Float = 0f,
    val userHeightCm: Int = 175,
    val userBuild: String = "Athletic", // "Slim", "Athletic", "Average", "Plus"
    val userSkinColor: String = "#E8D3C5",
    val userSelfieUrl: String? = null, // Custom selfie URL background
    val shirtSize: String = "M",
    val pantSize: String = "32",
    val shoeSize: String = "10",
    val cloudSyncProgress: Float = 0f,
    val cloudSyncStatus: String = "Connected"
)

data class ModelPreset(
    val id: String,
    val name: String,
    val subtitle: String,
    val gender: String,
    val baseColor: String // Hex color for user body highlight
) {
    companion object {
        val presets = listOf(
            ModelPreset("model_user", "Myself", "Personalized Canvas (Me)", "Custom", "#E8D3C5"),
            ModelPreset("model_1", "Aria", "Classic Minimalist (F)", "Female", "#E8D3C5"),
            ModelPreset("model_2", "Marcus", "Athletic Modern (M)", "Male", "#C2B2A2"),
            ModelPreset("model_3", "Sophia", "Tailored Elegant (F)", "Female", "#DFC7B3"),
            ModelPreset("model_4", "Jordan", "Versatile Relaxed (M)", "Male", "#D0C0B0")
        )
    }
}

class FitCheckViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = ClothingRepository(db.clothDao())

    // --- User Session ---
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    // --- Wardrobe ---
    val allClothes: StateFlow<List<ClothingEntity>> = repository.allClothes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Active Filters ---
    private val _selectedOccasion = MutableStateFlow("Casual")
    val selectedOccasion: StateFlow<String> = _selectedOccasion.asStateFlow()

    private val _selectedSeason = MutableStateFlow("All")
    val selectedSeason: StateFlow<String> = _selectedSeason.asStateFlow()

    // --- Dismissed Outfit Recommendations ---
    private val _dismissedComboKeys = MutableStateFlow<Set<String>>(emptySet())
    val dismissedComboKeys: StateFlow<Set<String>> = _dismissedComboKeys.asStateFlow()

    // --- Curated Outfit Recommendations ---
    val recommendations: StateFlow<List<OutfitCombination>> = combine(
        allClothes,
        _selectedOccasion,
        _selectedSeason,
        repository.outfitHistory,
        dismissedComboKeys
    ) { clothes, occasion, season, history, dismissedKeys ->
        repository.generateRecommendations(clothes, occasion, season, history).filter { combo ->
            val key = "${combo.top.id}_${combo.bottom.id}_${combo.shoes.id}"
            !dismissedKeys.contains(key)
        }
    }.flowOn(kotlinx.coroutines.Dispatchers.Default)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Selected Outfit Recommendation for details ---
    private val _selectedCombo = MutableStateFlow<OutfitCombination?>(null)
    val selectedCombo: StateFlow<OutfitCombination?> = _selectedCombo.asStateFlow()

    // --- Outfit History Log ---
    val outfitHistory: StateFlow<List<OutfitHistoryEntity>> = repository.outfitHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Gemini Interactive Stylist Chat ---
    private val _chatMessages = MutableStateFlow<List<StylistMessage>>(listOf(
        StylistMessage("Hello! I am Slay, your Personal AI Stylist.\n\n💡 HOW TO USE SLAY:\n1. Ask natural questions about color coordination or capsule tips (e.g. \"What colors match charcoal grey?\").\n2. Request outfit drafts for upcoming events (e.g. \"Plan an office meeting outfit for cold weather\").\n3. Select quick suggestion blocks below to explore style rules instantly!", false)
    ))
    val chatMessages: StateFlow<List<StylistMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // --- Virtual Try On Simulator State ---
    private val _tryOnState = MutableStateFlow(TryOnState())
    val tryOnState: StateFlow<TryOnState> = _tryOnState.asStateFlow()

    // --- Internet Scouting Solver State ---
    private val _scoutingResult = MutableStateFlow<String?>(null)
    val scoutingResult: StateFlow<String?> = _scoutingResult.asStateFlow()

    private val _isScoutingLoading = MutableStateFlow(false)
    val isScoutingLoading: StateFlow<Boolean> = _isScoutingLoading.asStateFlow()

    // --- Scanning / Analyzing item loading ---
    private val _isGarmentAnalyzing = MutableStateFlow(false)
    val isGarmentAnalyzing: StateFlow<Boolean> = _isGarmentAnalyzing.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    // --- Photo Sharing Stream ---
    private val _incomingSharedImageUri = MutableStateFlow<String?>(null)
    val incomingSharedImageUri: StateFlow<String?> = _incomingSharedImageUri.asStateFlow()

    fun setIncomingSharedImage(uriString: String?) {
        _incomingSharedImageUri.value = uriString
    }

    fun clearIncomingSharedImage() {
        _incomingSharedImageUri.value = null
    }

    // --- Weather Advisory States ---
    private val _weatherAlert = MutableStateFlow<String?>("Connecting to weather service...")
    val weatherAlert: StateFlow<String?> = _weatherAlert.asStateFlow()

    private val _weatherNotification = MutableStateFlow<String?>("No active notification")
    val weatherNotification: StateFlow<String?> = _weatherNotification.asStateFlow()

    init {
        // Load persistent profile config from SharedPreferences if exists
        loadPersistentState()
    }

    private fun loadPersistentState() {
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences("slay_user_prefs", Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val name = prefs.getString("name", null)
        
        if (email != null && name != null) {
            _userProfile.value = UserProfile(email, name)
            _onboardingCompleted.value = prefs.getBoolean("onboarding_completed", false)
            
            val height = prefs.getInt("height_cm", 175)
            val build = prefs.getString("build", "Athletic") ?: "Athletic"
            val skinColor = prefs.getString("skin_color", "#E8D3C5") ?: "#E8D3C5"
            val shirt = prefs.getString("shirt_size", "M") ?: "M"
            val pant = prefs.getString("pant_size", "32") ?: "32"
            val shoe = prefs.getString("shoe_size", "10") ?: "10"
            val selfieUrl = prefs.getString("selfie_url", "")
            
            _tryOnState.value = _tryOnState.value.copy(
                userHeightCm = height,
                userBuild = build,
                userSkinColor = skinColor,
                shirtSize = shirt,
                pantSize = pant,
                shoeSize = shoe,
                userSelfieUrl = if (selfieUrl.isNullOrBlank()) null else selfieUrl,
                activeModel = ModelPreset("model_user", "Myself", "Personalized Canvas (Me)", "Custom", skinColor)
            )
            val savedDismissed = prefs.getStringSet("dismissed_combos_set", emptySet()) ?: emptySet()
            _dismissedComboKeys.value = savedDismissed
        }
        
        // Trigger daily start-of-day reminder irrespective of weather
        triggerStartOfDayNotification()

        // Schedule periodic weather checker updates repeating every 4 hours
        viewModelScope.launch {
            while (true) {
                checkWeatherAndSendNotification()
                // Wait 4 hours
                kotlinx.coroutines.delay(4 * 60 * 60 * 1000L)
            }
        }
    }

    fun savePersistentState() {
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences("slay_user_prefs", Context.MODE_PRIVATE)
        val state = _tryOnState.value
        val profile = _userProfile.value
        
        prefs.edit().apply {
            if (profile != null) {
                putString("email", profile.email)
                putString("name", profile.name)
                putBoolean("onboarding_completed", _onboardingCompleted.value)
            }
            putInt("height_cm", state.userHeightCm)
            putString("build", state.userBuild)
            putString("skin_color", state.userSkinColor)
            putString("shirt_size", state.shirtSize)
            putString("pant_size", state.pantSize)
            putString("shoe_size", state.shoeSize)
            putString("selfie_url", state.userSelfieUrl ?: "")
            apply()
        }
    }

    fun triggerStartOfDayNotification() {
        Log.d("FitCheckViewModel", "Triggering daily start reminder...")
        val message = "Good morning! Let's Slay a brand new day! Irrespective of weather, your smart wardrobe concierge is ready."
        triggerSystemNotification(message)
    }

    fun checkWeatherAndSendNotification() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Request current and hourly forecast parameters
                val url = URL("https://api.open-meteo.com/v1/forecast?latitude=12.9716&longitude=77.5946&current=temperature_2m,weather_code&hourly=temperature_2m,weather_code")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                val code = conn.responseCode
                if (code == 200) {
                    val response = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)
                    val current = json.getJSONObject("current")
                    val temp = current.getDouble("temperature_2m")
                    val weatherCode = current.getInt("weather_code")
                    
                    // Parse hourly forecasts (+4 hrs, +8 hrs)
                    val hourly = json.optJSONObject("hourly")
                    val hourlyTimes = hourly?.optJSONArray("time")
                    val hourlyTemps = hourly?.optJSONArray("temperature_2m")
                    val hourlyCodes = hourly?.optJSONArray("weather_code")
                    
                    var laterForecastText = "No forecast data available for later."
                    if (hourlyTimes != null && hourlyTemps != null && hourlyCodes != null) {
                        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                        val indexPlus4 = (currentHour + 4) % 24
                        val indexPlus8 = (currentHour + 8) % 24
                        
                        val tempPlus4 = hourlyTemps.optDouble(indexPlus4, temp)
                        val codePlus4 = hourlyCodes.optInt(indexPlus4, weatherCode)
                        val descPlus4 = when (codePlus4) {
                            0 -> "Clear Skies"
                            1, 2, 3 -> "Partly Cloudy"
                            45, 48 -> "Foggy Atmospheric Mist"
                            51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Rain Showers"
                            71, 73, 75, 77, 85, 86 -> "Snow Fall"
                            95, 96, 99 -> "Severe Storms"
                            else -> "Overcast Sky"
                        }
                        
                        val tempPlus8 = hourlyTemps.optDouble(indexPlus8, temp)
                        val codePlus8 = hourlyCodes.optInt(indexPlus8, weatherCode)
                        val descPlus8 = when (codePlus8) {
                            0 -> "Clear Skies"
                            1, 2, 3 -> "Partly Cloudy"
                            45, 48 -> "Foggy Atmospheric Mist"
                            51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Rain Showers"
                            71, 73, 75, 77, 85, 86 -> "Snow Fall"
                            95, 96, 99 -> "Severe Storms"
                            else -> "Overcast Sky"
                        }
                        
                        laterForecastText = "⚠️ LATER FORECAST INTERNALS:\n• In 4 Hours: ${tempPlus4.toInt()}°C (${descPlus4})\n• In 8 Hours: ${tempPlus8.toInt()}°C (${descPlus8})"
                    }
                    
                    val (notificationMsg, stylistAdvice) = generateWeatherAdvice(temp, weatherCode)
                    val enrichedStylistAdvice = stylistAdvice + "\n\n$laterForecastText"
                    
                    withContext(Dispatchers.Main) {
                        _weatherAlert.value = enrichedStylistAdvice
                        _weatherNotification.value = notificationMsg
                        
                        triggerSystemNotification("Weather: $notificationMsg. Slay forecasts: " + laterForecastText.replace("\n", " ").replace("⚠️", ""))
                    }
                } else {
                    throw Exception("Failed with code: $code")
                }
            } catch (e: Exception) {
                Log.e("FitCheckViewModel", "Failed to query weather, falling back offline", e)
                val targetSeason = _selectedSeason.value
                val (notificationMsg, stylistAdvice) = generateWeatherAdviceOffline(targetSeason)
                
                withContext(Dispatchers.Main) {
                    _weatherAlert.value = stylistAdvice + "\n\n⚠️ LATER FORECAST INTERNALS (Offline standard season trend):\nTemp will remain steady typical of $targetSeason."
                    _weatherNotification.value = notificationMsg
                    
                    triggerSystemNotification(notificationMsg)
                }
            }
        }
    }

    private fun generateWeatherAdvice(temp: Double, weatherCode: Int): Pair<String, String> {
        val forecast = when (weatherCode) {
            0 -> "Sunny"
            1, 2, 3 -> "Partly Cloudy"
            45, 48 -> "Foggy"
            51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Rainy"
            71, 73, 75, 77, 85, 86 -> "Snowy"
            95, 96, 99 -> "Stormy"
            else -> "Overcast"
        }
        
        val notification = when {
            weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82, 95, 96, 99) -> "carry an umbrella, rain expected!"
            weatherCode == 0 || temp > 28 -> "wear a hat, its sunny!"
            temp < 15 -> "wear a jacket, its chilly out!"
            else -> "wear a light sweater, mild weather today"
        }
        
        val fullAdvice = "☀️ Slay Today's Weather Assistant:\nIt is currently ${temp.toInt()}°C and $forecast.\n\n" +
                "💡 STYLIST RECOMMENDATION:\n" +
                "Because $notification, we have updated your stylist suggestions. " +
                "Check out recommended layers or hats in your capsule archive!"
                
        return Pair(notification, fullAdvice)
    }

    private fun generateWeatherAdviceOffline(season: String): Pair<String, String> {
        val notification = when (season.lowercase()) {
            "winter" -> "wear a warm coat, its chilly out!"
            "summer" -> "wear a hat, its sunny!"
            "spring", "autumn", "fall" -> "wear a light sweater, mild weather today"
            else -> "carry an umbrella, rain expected today!"
        }
        val fullAdvice = "☀️ Slay Today's Weather Assistant:\n(Offline Forecast) - Active Season: $season.\n\n" +
                "💡 STYLIST RECOMMENDATION:\n" +
                "Because $notification, we have customized your active digital double stylist recommendations. Stay styled!"
        return Pair(notification, fullAdvice)
    }

    private fun triggerSystemNotification(message: String) {
        val context = getApplication<Application>()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val channelId = "weather_notifications"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Slay Weather Alerts",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily weather clothing alerts"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_today)
            .setContentTitle("Slay Wardrobe weather alert")
            .setContentText(message)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            
        try {
            notificationManager.notify(1001, builder.build())
        } catch (e: SecurityException) {
            Log.e("FitCheckViewModel", "Lacked POST_NOTIFICATIONS permission", e)
        }
    }

    fun login(email: String, name: String) {
        viewModelScope.launch {
            val profile = UserProfile(email, name)
            _userProfile.value = profile
            repository.prepopulateIfEmpty(email)
            savePersistentState()
        }
    }

    fun completeOnboarding(selfieUrl: String?, height: Int, build: String, skinColor: String) {
        _tryOnState.value = _tryOnState.value.copy(
            userSelfieUrl = selfieUrl,
            userHeightCm = height,
            userBuild = build,
            userSkinColor = skinColor,
            activeModel = ModelPreset("model_user", "Myself", "Personalized Canvas (Me)", "Custom", skinColor)
        )
        _onboardingCompleted.value = true
        savePersistentState()
        triggerCloudSync()
    }

    fun updateUserSizes(height: Int, build: String, skinColor: String, shirt: String, pant: String, shoe: String) {
        _tryOnState.value = _tryOnState.value.copy(
            userHeightCm = height,
            userBuild = build,
            userSkinColor = skinColor,
            shirtSize = shirt,
            pantSize = pant,
            shoeSize = shoe
        )
        savePersistentState()
    }

    fun saveSelfieBitmap(bitmap: Bitmap): String? {
        return try {
            val context = getApplication<Application>()
            val file = java.io.File(context.cacheDir, "user_selfie_${System.currentTimeMillis()}.jpg")
            java.io.FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    private val _selectedScoutClothes = MutableStateFlow<Set<ClothingEntity>>(emptySet())
    val selectedScoutClothes: StateFlow<Set<ClothingEntity>> = _selectedScoutClothes.asStateFlow()

    fun toggleScoutSelection(item: ClothingEntity) {
        val current = _selectedScoutClothes.value
        _selectedScoutClothes.value = if (current.contains(item)) {
            current - item
        } else {
            current + item
        }
    }

    fun clearScoutSelection() {
        _selectedScoutClothes.value = emptySet()
    }

    fun queryInternetCombos() {
        val curState = _tryOnState.value
        val selection = _selectedScoutClothes.value
        val wardrobeToFeed = if (selection.isNotEmpty()) selection.toList() else allClothes.value
        
        viewModelScope.launch {
            _isScoutingLoading.value = true
            _scoutingResult.value = null
            try {
                val result = com.example.network.GeminiManager.findSuitableInternetCombos(
                    height = curState.userHeightCm,
                    build = curState.userBuild,
                    shirtSize = curState.shirtSize,
                    pantSize = curState.pantSize,
                    shoeSize = curState.shoeSize,
                    wardrobe = wardrobeToFeed
                )
                _scoutingResult.value = result
            } catch (e: Exception) {
                Log.e("FitCheckViewModel", "Failed internet scouting search", e)
                _scoutingResult.value = "Scouting search failed. Check your internet connection."
            } finally {
                _isScoutingLoading.value = false
            }
        }
    }

    fun logout() {
        _userProfile.value = null
        _onboardingCompleted.value = false
    }

    fun setFilters(occasion: String, season: String) {
        _selectedOccasion.value = occasion
        _selectedSeason.value = season
    }

    fun setSelectedCombo(combo: OutfitCombination?) {
        _selectedCombo.value = combo
    }

    fun selectOccasion(occasion: String) {
        _selectedOccasion.value = occasion
    }

    fun selectSeason(season: String) {
        _selectedSeason.value = season
    }

    // --- Repository operations ---
    fun toggleFavorite(item: ClothingEntity) {
        viewModelScope.launch {
            repository.update(item.copy(isFavorite = !item.isFavorite))
        }
    }

    fun deleteClothing(item: ClothingEntity) {
        viewModelScope.launch {
            repository.delete(item)
            // If try-on displays this garment, reset it
            val cur = _tryOnState.value
            _tryOnState.value = cur.copy(
                topItem = if (cur.topItem?.id == item.id) null else cur.topItem,
                bottomItem = if (cur.bottomItem?.id == item.id) null else cur.bottomItem,
                shoesItem = if (cur.shoesItem?.id == item.id) null else cur.shoesItem,
                jacketItem = if (cur.jacketItem?.id == item.id) null else cur.jacketItem,
                accessoryItem = if (cur.accessoryItem?.id == item.id) null else cur.accessoryItem
            )
        }
    }

    /**
     * Upload / Add a Clothing Item with real-time AI computer vision scanning & tagging from Gemini!
     */
    fun addNewGarment(tagInput: String, bitmap: Bitmap? = null) {
        viewModelScope.launch {
            _isGarmentAnalyzing.value = true
            try {
                val analysis = GeminiManager.analyzeGarment(tagInput, bitmap)
                val newCloth = ClothingEntity(
                    userId = _userProfile.value?.email ?: "anonymous",
                    category = analysis.category,
                    name = analysis.name,
                    color = analysis.color,
                    colorHex = analysis.colorHex,
                    style = analysis.style,
                    season = analysis.season,
                    occasions = analysis.occasions,
                    embeddingVector = "0.5,0.5,0.5,0.5" // standard seed vector
                )
                repository.insert(newCloth)
            } catch (e: Exception) {
                Log.e("FitCheckViewModel", "Failed to add garment with AI Analysis", e)
            } finally {
                _isGarmentAnalyzing.value = false
            }
        }
    }

    /**
     * Upload / Add 2 clothing items (e.g. professional office tops/bottoms) with dual real-time AI scanning
     */
    fun addNewGarmentDual(tagInput: String, bitmap1: Bitmap? = null, bitmap2: Bitmap? = null) {
        viewModelScope.launch {
            _isGarmentAnalyzing.value = true
            try {
                val items = GeminiManager.analyzeGarmentDual(tagInput, bitmap1, bitmap2)
                for (item in items) {
                    val newCloth = ClothingEntity(
                        userId = _userProfile.value?.email ?: "anonymous",
                        category = item.category,
                        name = item.name,
                        color = item.color,
                        colorHex = item.colorHex,
                        style = item.style,
                        season = item.season,
                        occasions = item.occasions,
                        embeddingVector = "0.5,0.5,0.5,0.5"
                    )
                    repository.insert(newCloth)
                }
            } catch (e: Exception) {
                Log.e("FitCheckViewModel", "Failed dual office analysis", e)
            } finally {
                _isGarmentAnalyzing.value = false
            }
        }
    }

    /**
     * Log Outfit wear event to History
     */
    fun logWearingOutfit(combo: OutfitCombination) {
        viewModelScope.launch {
            val history = OutfitHistoryEntity(
                userId = _userProfile.value?.email ?: "anonymous",
                topId = combo.top.id,
                bottomId = combo.bottom.id,
                shoesId = combo.shoes.id,
                accessoryId = combo.accessory?.id,
                jacketId = combo.jacket?.id,
                occasion = combo.occasion,
                compatibilityScore = combo.averageScore
            )
            repository.logOutfit(history)
        }
    }

    fun clearLogHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun clearAllClothes() {
        viewModelScope.launch {
            repository.clearAllClothes()
        }
    }

    fun updateClothingCategory(item: ClothingEntity, newCategory: String) {
        viewModelScope.launch {
            val updated = item.copy(category = newCategory.trim().lowercase())
            repository.update(updated)
        }
    }

    fun updateClothing(item: ClothingEntity) {
        viewModelScope.launch {
            repository.update(item)
        }
    }

    fun insertCustomGarment(
        category: String,
        name: String,
        color: String,
        colorHex: String,
        style: String,
        season: String,
        occasions: String = "Casual,Travel"
    ) {
        viewModelScope.launch {
            val newCloth = ClothingEntity(
                userId = _userProfile.value?.email ?: "anonymous",
                category = category.trim().lowercase(),
                name = name.trim(),
                color = color.trim(),
                colorHex = if (colorHex.startsWith("#")) colorHex else "#7D6B58",
                style = style,
                season = season,
                occasions = occasions,
                embeddingVector = "0.5,0.5,0.5,0.5"
            )
            repository.insert(newCloth)
        }
    }

    /**
     * Send message to AI Stylist Chat
     */
    fun sendMessageToStylist(query: String) {
        if (query.isBlank()) return
        val userMsg = StylistMessage(query, true)
        _chatMessages.update { it + userMsg }
        
        _isChatLoading.value = true
        viewModelScope.launch {
            try {
                // Compile standard simple chat pairs
                val historyList = _chatMessages.value.dropLast(1).map { Pair(it.message, it.isUser) }
                val reply = GeminiManager.chatWithStylist(query, allClothes.value, historyList, outfitHistory.value)
                _chatMessages.update { it + StylistMessage(reply, false) }
            } catch (e: Exception) {
                _chatMessages.update { it + StylistMessage("Pardon me, I encountered a connection hiccup. Let's try styling that again!", false) }
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    // --- Try On Simulator Controls ---
    fun selectModelForTryOn(model: ModelPreset) {
        val finalModel = if (model.id == "model_user") {
            model.copy(baseColor = _tryOnState.value.userSkinColor)
        } else {
            model
        }
        _tryOnState.value = _tryOnState.value.copy(activeModel = finalModel)
    }

    fun selectToTryOn(item: ClothingEntity) {
        val cur = _tryOnState.value
        val newState = when (item.category) {
            "shirt", "t-shirt" -> cur.copy(topItem = item)
            "hoodie" -> cur.copy(topItem = item) // Hoodie replaces Top
            "pants", "jeans", "shorts" -> cur.copy(bottomItem = item)
            "shoes" -> cur.copy(shoesItem = item)
            "jacket" -> cur.copy(jacketItem = item)
            "accessories" -> cur.copy(accessoryItem = item)
            else -> cur
        }
        _tryOnState.value = newState
    }

    fun clearTryOnSlot(category: String) {
        val cur = _tryOnState.value
        val newState = when (category) {
            "top" -> cur.copy(topItem = null)
            "bottom" -> cur.copy(bottomItem = null)
            "shoes" -> cur.copy(shoesItem = null)
            "jacket" -> cur.copy(jacketItem = null)
            "accessories" -> cur.copy(accessoryItem = null)
            else -> cur
        }
        _tryOnState.value = newState
    }

    fun resetTryOn() {
        _tryOnState.value = TryOnState(activeModel = _tryOnState.value.activeModel)
    }

    /**
     * Simulates standard professional IDM-VTON / CatVTON diffusion workflow pipeline step by step:
     * Human segmentation -> Pose estimation -> Garment deformation warping -> AI rendering.
     */
    fun runVirtualTryOnPipeline() {
        viewModelScope.launch {
            _tryOnState.value = _tryOnState.value.copy(currentStep = "segmenting", progress = 0.1f)
            kotlinx.coroutines.delay(1000)
            
            _tryOnState.value = _tryOnState.value.copy(currentStep = "warping", progress = 0.5f)
            kotlinx.coroutines.delay(1200)
            
            _tryOnState.value = _tryOnState.value.copy(currentStep = "rendering", progress = 0.8f)
            kotlinx.coroutines.delay(1000)
            
            _tryOnState.value = _tryOnState.value.copy(currentStep = "complete", progress = 1.0f)
        }
    }

    fun dismissTryOnPipeline() {
        _tryOnState.value = _tryOnState.value.copy(currentStep = "", progress = 0f)
    }

    fun updateUserHeight(height: Int) {
        _tryOnState.value = _tryOnState.value.copy(userHeightCm = height)
        savePersistentState()
    }

    fun updateUserBuild(build: String) {
        _tryOnState.value = _tryOnState.value.copy(userBuild = build)
        savePersistentState()
    }

    fun updateUserSkinColor(hex: String) {
        _tryOnState.value = _tryOnState.value.copy(userSkinColor = hex, activeModel = _tryOnState.value.activeModel.copy(baseColor = hex))
        savePersistentState()
    }

    fun updateUserSelfie(url: String?) {
        _tryOnState.value = _tryOnState.value.copy(userSelfieUrl = url)
        savePersistentState()
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            _tryOnState.value = _tryOnState.value.copy(cloudSyncStatus = "Syncing...", cloudSyncProgress = 0.1f)
            kotlinx.coroutines.delay(600)
            _tryOnState.value = _tryOnState.value.copy(cloudSyncProgress = 0.5f)
            kotlinx.coroutines.delay(600)
            _tryOnState.value = _tryOnState.value.copy(cloudSyncProgress = 0.9f)
            kotlinx.coroutines.delay(500)
            _tryOnState.value = _tryOnState.value.copy(cloudSyncStatus = "Synced!", cloudSyncProgress = 1.0f)
            savePersistentState()
            kotlinx.coroutines.delay(1200)
            _tryOnState.value = _tryOnState.value.copy(cloudSyncStatus = "Connected", cloudSyncProgress = 0.0f)
        }
    }

    fun dismissCombo(combo: OutfitCombination, reasons: List<String>, customText: String?) {
        val key = "${combo.top.id}_${combo.bottom.id}_${combo.shoes.id}"
        val newSet = _dismissedComboKeys.value + key
        _dismissedComboKeys.value = newSet
        
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences("slay_user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putStringSet("dismissed_combos_set", newSet).apply()
    }
}
