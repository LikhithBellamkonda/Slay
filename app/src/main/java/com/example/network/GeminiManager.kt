package com.example.network

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.ClothingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiManager {
    private const val TAG = "GeminiManager"
    private const val MODEL_NAME = "gemini-3.5-flash"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            "MY_GEMINI_API_KEY"
        }
    }

    private fun isKeyConfigured(key: String): Boolean {
        return key.isNotEmpty() && key != "MY_GEMINI_API_KEY" && !key.contains("PLACEHOLDER")
    }

    // Encodes a Bitmap to Base64 String for Gemini Multimodal input
    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Scans an added item (simulated or real image) using Gemini Multimodal input.
     * Returns matching metadata to index the wardrobe.
     */
    suspend fun analyzeGarment(
        tagSuggestion: String,
        bitmap: Bitmap? = null
    ): GarmentAnalysis = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (!isKeyConfigured(apiKey)) {
            Log.w(TAG, "Gemini API Key is not configured. Falling back to local offline fast classification.")
            // Use smart offline rules to simulate classification instantly based on user prompt
            return@withContext parseDraftOffline(tagSuggestion)
        }

        val prompt = """
            You are a highly precise fashion expert and automated garment vision system.
            Analyze this clothing item described as: "$tagSuggestion". 
            Determine its category (must be either: "shirt", "t-shirt", "pants", "jeans", "hoodie", "jacket", "shoes", or "accessories"), 
            its dominant descriptive color (e.g. "Slate Grey"), an appropriate matching HEX color string for rendering (e.g. "#4E5D6C"),
            its fashion style (must be either: "Formal", "Casual", "Streetwear", "Sporty", "Elegant"),
            its ideal season suitability ("Summer", "Autumn", "Winter", "Spring", or "All"),
            and select 2-3 matching occasions (from: "Casual", "Office", "Party", "Wedding", "College", "Gym", "Travel").
            Also generate a concise stylish commercial product label name (e.g. "Vintage Ribbed Cardigan").
            
            Return strictly a JSON object with this exact format (no markdown, no quotes outside JSON):
            {
              "category": "shirt",
              "name": "Cozy Flannel Over-shirt",
              "color": "Crimson Red",
              "colorHex": "#990000",
              "style": "Casual",
              "season": "Autumn",
              "occasions": "Casual,Travel,College"
            }
        """.trimIndent()

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
            
            // Build contents payload
            val partsArray = JSONArray()
            partsArray.put(JSONObject().put("text", prompt))
            
            if (bitmap != null) {
                val inlineData = JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", bitmap.toBase64())
                }
                partsArray.put(JSONObject().put("inlineData", inlineData))
            }

            val contents = JSONArray().put(JSONObject().put("parts", partsArray))
            
            // Configure JSON response scheme
            val responseFormat = JSONObject().apply {
                put("type", "OBJECT")
                val properties = JSONObject().apply {
                    put("category", JSONObject().put("type", "STRING"))
                    put("name", JSONObject().put("type", "STRING"))
                    put("color", JSONObject().put("type", "STRING"))
                    put("colorHex", JSONObject().put("type", "STRING"))
                    put("style", JSONObject().put("type", "STRING"))
                    put("season", JSONObject().put("type", "STRING"))
                    put("occasions", JSONObject().put("type", "STRING"))
                }
                put("properties", properties)
                put("required", JSONArray().apply {
                    put("category"); put("name"); put("color"); put("colorHex"); put("style"); put("season"); put("occasions")
                })
            }

            val generationConfig = JSONObject().apply {
                put("responseMimeType", "application/json")
                put("responseSchema", responseFormat)
                put("temperature", 0.1)
            }

            val requestBodyJson = JSONObject().apply {
                put("contents", contents)
                put("generationConfig", generationConfig)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBodyJson.toString().toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("API Error code ${response.code}: ${response.body?.string()}")
                }
                
                val responseString = response.body?.string() ?: ""
                val responseJson = JSONObject(responseString)
                val candidates = responseJson.getJSONArray("candidates")
                val textOutput = candidates.getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                val res = JSONObject(textOutput.trim())
                return@withContext GarmentAnalysis(
                    category = res.optString("category", "shirt"),
                    name = res.optString("name", "Custom Item"),
                    color = res.optString("color", "Midnight Black"),
                    colorHex = res.optString("colorHex", "#111111"),
                    style = res.optString("style", "Casual"),
                    season = res.optString("season", "All"),
                    occasions = res.optString("occasions", "Casual")
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to call Gemini API, falling back to offline", e)
            return@withContext parseDraftOffline(tagSuggestion)
        }
    }

    /**
     * Conversation Stylist Chat. Translates user preference and queries into highly
     * custom outfit summaries matching their specific wardrobe items.
     */
    suspend fun chatWithStylist(
        userQuery: String,
        wardrobe: List<ClothingEntity>,
        chatHistory: List<Pair<String, Boolean>> // List of Pair(Message, isUser)
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (!isKeyConfigured(apiKey)) {
            return@withContext generateOfflineStylistResponse(userQuery, wardrobe)
        }

        // Serializing their current wardrobe so Gemini understands exactly what items they have
        val wardrobeSummary = wardrobe.joinToString("\n") { item ->
            "- [ID #${item.id}] Category: ${item.category}, Name: ${item.name}, Color_Hex: ${item.colorHex}, Description: ${item.color} ${item.style} fits. Suited seasons: ${item.season}. Matches occasions: ${item.occasions}."
        }

        val systemPrompt = """
            You are "Slay" - a world-class elite AI Personal Fashion Stylist assisting the user.
            You have live, direct visibility into the user's digital wardrobe:
            
            USER WARDROBE ITEMS:
            $wardrobeSummary
            
            Rules for Stylist:
            1. Suggest real styling coordinates *strictly* matching the IDs and names of items existing in the user's wardrobe summary in order to make recommendations actionable.
            2. Be inspiring, highly conversational, visually descriptive (explain why colors and style silhouettes coordinate well).
            3. Address occasion contexts (office, wedding, streetwear, winter warmth) with precision.
            4. Keep responses elegant, structured with concise bullet points, and brief (under 120 words).
            5. Reference specific item IDs: for example: "Suggest checking out your [White Oxford Buttondown #2]" or similar, so they can easily click or tap.
        """.trimIndent()

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
            
            val contents = JSONArray()
            
            // Append historic messages (max 5)
            val historySubset = chatHistory.takeLast(5)
            for ((msg, isUser) in historySubset) {
                val role = if (isUser) "user" else "model"
                contents.put(JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().put(JSONObject().put("text", msg)))
                })
            }

            // Append current message
            contents.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", userQuery)))
            })

            val requestBodyJson = JSONObject().apply {
                put("contents", contents)
                put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemPrompt))))
                put("generationConfig", JSONObject().put("temperature", 0.7))
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBodyJson.toString().toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("API response failed: ${response.code}")
                }
                val responseString = response.body?.string() ?: ""
                val responseJson = JSONObject(responseString)
                val candidates = responseJson.getJSONArray("candidates")
                return@withContext candidates.getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to call Gemini chat, using offline response", e)
            return@withContext generateOfflineStylistResponse(userQuery, wardrobe)
        }
    }

    suspend fun findSuitableInternetCombos(
        height: Int,
        build: String,
        shirtSize: String,
        pantSize: String,
        shoeSize: String,
        wardrobe: List<ClothingEntity>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        
        val wardrobeSummary = wardrobe.joinToString("\n") { item ->
            "- ${item.category}: ${item.name} (${item.color}, ${item.style})"
        }
        
        val query = "Find suitable combos on the internet that match my size profile: Height $height cm, Build $build, Shirt size $shirtSize, Pant size $pantSize, Shoe size $shoeSize and coordinate with my wardrobe:\n$wardrobeSummary. Tell me where to find suitable combos that complement this."
        
        if (!isKeyConfigured(apiKey)) {
            // Offline fallback
            return@withContext """
                🛒 *Slay AI Internet Finder (Offline mode)*:
                Matching trending combos for your Size Profile (**$shirtSize** Tops, **$pantSize** Bottoms, **$shoeSize** Shoes):
                
                1. **Nordstrom Refined Minimalist Pack**: Coordinates perfectly with your current closet. Matches your $height cm $build build.
                2. **ASOS Urban Tailored Suit**: Suggests pairing with your wardrobe tops to complete the capsule.
                3. **Nike Air styling coordinates**: Excellent fit for US $shoeSize footwear listings online.
                
                *Tip: Enter a Gemini API Key in your project's Secrets configuration to access alive marketplace recommendations!*
            """.trimIndent()
        }
        
        val systemPrompt = """
            You are "Slay AI Internet Finder" - an ultra-premium global fashion scout.
            The user is seeking internet clothing combinations that match their physical profile and complement their wardrobe.
            
            USER PROFILE:
            - Height: $height cm
            - Build: $build
            - Tops/Shirt Size: $shirtSize
            - Bottoms/Pant Size: $pantSize
            - Shoe Size: $shoeSize
            
            USER WARDROBE:
            $wardrobeSummary
            
            Provide styling coordinates from major premium retailers (such as Zara, ASOS, Nordstrom, Nike, Uniqlo) with specific items that are highly recommended in their specific sizes. Show exact items, sizes to buy, and explain why they coordinate perfectly. Be very stylish, concise (under 130 words), and use clean markdown bullet points.
        """.trimIndent()

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
            val contents = JSONArray().put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", query)))
            })
            val requestBodyJson = JSONObject().apply {
                put("contents", contents)
                put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemPrompt))))
                put("generationConfig", JSONObject().put("temperature", 0.7))
            }
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBodyJson.toString().toRequestBody(mediaType))
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext "Offline search result: Recommended Zara Casual Pants (Size $pantSize) and ASOS Cotton Oxfords (Size $shirtSize) matching your current wardrobe."
                val responseString = response.body?.string() ?: ""
                val responseJson = JSONObject(responseString)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    if (contentObj != null) {
                        val parts = contentObj.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text", "")
                        }
                    }
                }
                "No recommendations found. Suggest checking ASOS and Zara online for Size $shirtSize tops."
            }
        } catch (e: Exception) {
            "Offline search result: Coordinates found on Zara and Nordstrom matching your $shirtSize top, $pantSize bottom, and $shoeSize shoe sizes."
        }
    }

    // High quality offline fallback parsing system using advanced local string matching heuristics
    private fun parseDraftOffline(tag: String): GarmentAnalysis {
        val query = tag.lowercase()
        
        // Category detection
        val category = when {
            query.contains("tee") || query.contains("tshirt") || query.contains("t-shirt") -> "t-shirt"
            query.contains("shirt") || query.contains("buttondown") || query.contains("polo") -> "shirt"
            query.contains("jeans") || query.contains("denim") -> "jeans"
            query.contains("pants") || query.contains("trousers") || query.contains("slacks") || query.contains("chinos") || query.contains("cargo") -> "pants"
            query.contains("hoodie") || query.contains("sweatshirt") -> "hoodie"
            query.contains("jacket") || query.contains("blazer") || query.contains("coat") || query.contains("outerwear") -> "jacket"
            query.contains("shoes") || query.contains("sneakers") || query.contains("boots") || query.contains("loafers") || query.contains("oxford") -> "shoes"
            else -> "accessories"
        }

        // Color & Hex detection
        val (colorName, colorHex) = when {
            query.contains("white") || query.contains("cream") -> Pair("Ivory White", "#FAFAFA")
            query.contains("black") || query.contains("charcoal") || query.contains("dark") -> Pair("Carbon Black", "#1F1F1F")
            query.contains("blue") || query.contains("navy") || query.contains("indigo") -> Pair("Royal Navy Blue", "#1A2530")
            query.contains("green") || query.contains("sage") || query.contains("olive") -> Pair("Earthy Sage Green", "#5F6F5E")
            query.contains("beige") || query.contains("tan") || query.contains("camel") || query.contains("khaki") -> Pair("Camel Tan", "#C09F80")
            query.contains("red") || query.contains("maroon") || query.contains("crimson") -> Pair("Scarlet Crimson", "#800000")
            query.contains("grey") || query.contains("gray") || query.contains("silver") -> Pair("Heather Grey", "#8E8E93")
            else -> Pair("Earth Tone Accent", "#7D6B58")
        }

        // Style detection
        val style = when {
            query.contains("formal") || query.contains("oxford") || query.contains("blazer") || query.contains("dress") || query.contains("slacks") -> "Formal"
            query.contains("street") || query.contains("hoodie") || query.contains("graphic") || query.contains("cargo") -> "Streetwear"
            query.contains("smart") || query.contains("elegant") || query.contains("chinos") || query.contains("knit") -> "Elegant"
            query.contains("sport") || query.contains("gym") || query.contains("active") -> "Sporty"
            else -> "Casual"
        }

        // Season detection
        val season = when {
            query.contains("summer") || query.contains("light") || query.contains("beach") -> "Summer"
            query.contains("winter") || query.contains("heavy") || query.contains("coat") || query.contains("fleece") || query.contains("hoodie") -> "Winter"
            query.contains("autumn") || query.contains("fall") || query.contains("jacket") || query.contains("blazer") -> "Autumn"
            query.contains("spring") -> "Spring"
            else -> "All"
        }

        // Occasions detection
        val occasions = when (style) {
            "Formal" -> "Office,Wedding,Party"
            "Streetwear" -> "Casual,College,Travel"
            "Elegant" -> "Office,Party,Casual"
            "Sporty" -> "Gym,Casual,Travel"
            else -> "Casual,Travel,College"
        }

        // Format name casing
        val formattedName = tag.split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }.ifEmpty { "Tailored Modern $category" }

        return GarmentAnalysis(
            category = category,
            name = formattedName,
            color = colorName,
            colorHex = colorHex,
            style = style,
            season = season,
            occasions = occasions
        )
    }

    // Simulated personal fashion styling engine
    private fun generateOfflineStylistResponse(userQuery: String, wardrobe: List<ClothingEntity>): String {
        val query = userQuery.lowercase()
        return when {
            query.contains("office") || query.contains("formal") || query.contains("interview") -> {
                val tops = wardrobe.filter { it.category == "shirt" && it.style == "Formal" }
                val bottoms = wardrobe.filter { it.category == "pants" && it.style == "Formal" }
                val shoes = wardrobe.filter { it.category == "shoes" && it.style == "Formal" }
                
                if (tops.isNotEmpty() && bottoms.isNotEmpty()) {
                    "**Corporate Elite Coordinate:**\n" +
                    "Try pairing your **${tops.first().name}** with **${bottoms.first().name}**. " +
                    "Finish off with the **${shoes.firstOrNull()?.name ?: "Oxford Dress Shoes"}**.\n\n" +
                    "*Stylist Tip:* This combination focuses on sharp formal lines. Ensure accessories match your leather tone exactly!"
                } else {
                    "For a crisp office coordination, I recommend styling a collared Oxford shirt tucked into tailored dark trousers or slacks, paired with clean leather wingtip dress shoes or sleek walnut loafers."
                }
            }
            query.contains("street") || query.contains("hoodie") || query.contains("skate") -> {
                val hood = wardrobe.filter { it.category == "hoodie" || it.style == "Streetwear" }
                val bottoms = wardrobe.filter { it.category == "jeans" || it.category == "pants" }
                val sneaker = wardrobe.filter { it.category == "shoes" && it.style == "Casual" }

                if (hood.isNotEmpty() && bottoms.isNotEmpty()) {
                    "**Urban Streetwear Fit:**\n" +
                    "Grab your **${hood.first().name}** and pair it with **${bottoms.first().name}**. " +
                    "Add some pop with your **${sneaker.firstOrNull()?.name ?: "Minimal Sneakers"}**.\n\n" +
                    "*Stylist Tip:* The oversized silhouette of the top matches perfectly with slim or relaxed bottoms. Layering a denim jacket would elevate this aesthetic."
                } else {
                    "A street vibe looks best using relaxed oversized hoodies, heavy-knit crews, drop-shoulder graphic t-shirts, worn-in distressed jeans, and high-contrast retro white sneakers."
                }
            }
            query.contains("casual") || query.contains("friday") || query.contains("college") || query.contains("hangout") -> {
                val tee = wardrobe.filter { it.category == "t-shirt" }
                val jean = wardrobe.filter { it.category == "jeans" }
                val shoe = wardrobe.filter { it.category == "shoes" && it.style == "Casual" }

                if (tee.isNotEmpty() && jean.isNotEmpty()) {
                    "**Smart Casual Comfort:**\n" +
                    "Pairing your **${tee.first().name}** with **${jean.first().name}** and clean **${shoe.firstOrNull()?.name ?: "White Sneakers"}** offers an effortless, high-contrast relaxed daily look.\n\n" +
                    "*Stylist Tip:* It's casual but intentional. Roll up the sleeve cuffs slightly and throw on a silver sports watch for subtle dynamic detailing!"
                } else {
                    "A classic smart-casual balance is easily achieved pairing a crewneck pocket t-shirt, tailored chinos or mid-wash slim denim, and minimalist leather cupsole sneakers."
                }
            }
            else -> {
                val top = wardrobe.shuffled().find { it.category in listOf("shirt", "t-shirt", "hoodie") }
                val bottom = wardrobe.shuffled().find { it.category in listOf("pants", "jeans") }
                if (top != null && bottom != null) {
                    "Based on your smart capsule catalog, I recommend pairing **${top.name}** with **${bottom.name}**.\n\n" +
                    "This balances the **${top.style}** details of the top with the **${bottom.style}** structure of the bottoms for a highly versatile aesthetic suited for multiple occasions."
                } else {
                    "Welcome! I am your AI stylist. Add items to your wardobe or upload garment photos, and I will instantly analyze compatibility, draft color palettes, and curate high-aesthetic capsule outfits representing your absolute best."
                }
            }
        }
    }
}

data class GarmentAnalysis(
    val category: String,
    val name: String,
    val color: String,
    val colorHex: String,
    val style: String,
    val season: String,
    val occasions: String
)
