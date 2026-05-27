package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ClothingRepository(private val clothDao: ClothDao) {

    val allClothes: Flow<List<ClothingEntity>> = clothDao.getAllClothes()
    val outfitHistory: Flow<List<OutfitHistoryEntity>> = clothDao.getOutfitHistory()

    suspend fun insert(item: ClothingEntity): Long = withContext(Dispatchers.IO) {
        clothDao.insertClothing(item)
    }

    suspend fun update(item: ClothingEntity) = withContext(Dispatchers.IO) {
        clothDao.updateClothing(item)
    }

    suspend fun delete(item: ClothingEntity) = withContext(Dispatchers.IO) {
        clothDao.deleteClothing(item)
    }

    suspend fun getClothingById(id: Int): ClothingEntity? = withContext(Dispatchers.IO) {
        clothDao.getClothingById(id)
    }

    suspend fun logOutfit(history: OutfitHistoryEntity) = withContext(Dispatchers.IO) {
        clothDao.insertOutfitHistory(history)
        // Increment worn count for parts
        incrementWornCount(history.topId)
        incrementWornCount(history.bottomId)
        incrementWornCount(history.shoesId)
        history.accessoryId?.let { incrementWornCount(it) }
        history.jacketId?.let { incrementWornCount(it) }
    }

    private suspend fun incrementWornCount(id: Int) {
        clothDao.getClothingById(id)?.let {
            clothDao.updateClothing(it.copy(wornCount = it.wornCount + 1))
        }
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        clothDao.clearOutfitHistory()
    }

    suspend fun clearAllClothes() = withContext(Dispatchers.IO) {
        clothDao.clearAllClothes()
    }

    // Auto-populates a curated modern capsule wardrobe if empty
    suspend fun prepopulateIfEmpty(userId: String) = withContext(Dispatchers.IO) {
        // Disabled: user requested all default/hardcoded shirts & pants to be removed.
    }

    /**
     * Compute Compatibility Score for an Outfit Selection using:
     * Final Score = 0.4 * Color Compatibility + 0.3 * Style Compatibility + 0.2 * Occasion Match + 0.1 * User Preference
     */
    fun calculateCompatibility(
        top: ClothingEntity,
        bottom: ClothingEntity,
        shoes: ClothingEntity,
        accessory: ClothingEntity? = null,
        jacket: ClothingEntity? = null,
        targetOccasion: String
    ): Int {
        val colorComp = getColorCompatibility(top.colorHex, bottom.colorHex, shoes.colorHex, jacket?.colorHex)
        val styleComp = getStyleCompatibility(top.style, bottom.style, shoes.style, jacket?.style)
        val occasionMatch = getOccasionMatch(listOfNotNull(top, bottom, shoes, accessory, jacket), targetOccasion)
        val userPref = getUserPreferenceScore(listOfNotNull(top, bottom, shoes, accessory, jacket))

        val score = (0.4 * colorComp) + (0.3 * styleComp) + (0.2 * occasionMatch) + (0.1 * userPref)
        return score.coerceIn(10.0, 100.0).toInt()
    }

    private fun getColorCompatibility(topHex: String, bottomHex: String, shoesHex: String, jacketHex: String?): Double {
        // High-fidelity programmatic pairing engine based on classic fashion color wheel and tone theory
        // We match common hex styles to extract complementary color systems
        val normTop = topHex.uppercase().removePrefix("#")
        val normBottom = bottomHex.uppercase().removePrefix("#")

        // Neutral tones (white, grey, black, beige, cream) match almost everything beautifully
        val neutrals = setOf("FAFAFA", "FFFFFF", "2C2C2E", "0F0F0F", "C0C0C0", "F5F2EB", "D7C49E")
        val darks = setOf("1D2731", "3D2B1F", "4A2E1B", "0F0F0F", "2C2C2E")

        if (neutrals.contains(normTop) || neutrals.contains(normBottom)) {
            return 95.0 // Neutrals offer premium foundation compatibility
        }

        // Navy Blue and Camel/Browns
        if ((normTop == "1D2731" && normBottom == "C19A6B") || (normTop == "C19A6B" && normBottom == "1D2731")) {
            return 98.0 // High aesthetic navy-camel pair
        }

        // Sage green matches white, charcoal, navy beautifully
        if (normTop == "7F8E7F") {
            if (normBottom == "2C2C2E" || normBottom == "FFFFFF" || normBottom == "FAFAFA" || normBottom == "F5F2EB") {
                return 94.0
            }
        }

        // Monochromatic looks
        if (normTop == normBottom) {
            return 85.0
        }

        // Complementary high contrast
        if (darks.contains(normTop) && neutrals.contains(normBottom)) {
            return 92.0
        }
        if (neutrals.contains(normTop) && darks.contains(normBottom)) {
            return 92.0
        }

        // Fallback standard score
        return 70.0
    }

    private fun getStyleCompatibility(top: String, bottom: String, shoes: String, jacket: String?): Double {
        // Evaluate coordination among clothing styles
        val styles = listOfNotNull(top, bottom, shoes, jacket)
        val uniqueStyles = styles.toSet()

        return when {
            uniqueStyles.size == 1 -> 100.0 // Single aesthetic cohesion
            uniqueStyles.size == 2 -> {
                // Compatible style pairings
                val p1 = uniqueStyles.first()
                val p2 = uniqueStyles.elementAt(1)
                if ((p1 == "Formal" && p2 == "Elegant") || (p1 == "Elegant" && p2 == "Formal")) 92.0
                else if ((p1 == "Casual" && p2 == "Streetwear") || (p1 == "Streetwear" && p2 == "Casual")) 90.0
                else if ((p1 == "Casual" && p2 == "Elegant") || (p1 == "Elegant" && p2 == "Casual")) 85.0
                else if ((p1 == "Casual" && p2 == "Sporty") || (p1 == "Sporty" && p2 == "Casual")) 85.0
                else if ((p1 == "Streetwear" && p2 == "Sporty") || (p1 == "Sporty" && p2 == "Streetwear")) 88.0
                else 60.0 // Partial clash (e.g. Formal + Sporty)
            }
            else -> 50.0 // Highly fragmented aesthetic styles
        }
    }

    private fun getOccasionMatch(items: List<ClothingEntity>, occasion: String): Double {
        var matches = 0
        for (item in items) {
            val list = item.occasions.split(",").map { it.trim().lowercase() }
            if (list.contains(occasion.lowercase())) {
                matches++
            }
        }
        return (matches.toDouble() / items.size.toDouble()) * 100.0
    }

    private fun getUserPreferenceScore(items: List<ClothingEntity>): Double {
        var score = 50.0
        for (item in items) {
            if (item.isFavorite) score += 10.0
            score += (item.wornCount * 2.0).coerceAtMost(20.0)
        }
        return score.coerceAtMost(100.0)
    }

    // Automatically synthesizes the single best outfits out of the current wardrobe for the selected criteria, and penalizes recently worn clothes
    fun generateRecommendations(
        clothes: List<ClothingEntity>,
        occasion: String,
        season: String,
        history: List<OutfitHistoryEntity> = emptyList()
    ): List<OutfitCombination> {
        // Find recently worn item IDs (last 3 logging sessions)
        val recentSessions = history.take(3)
        val recentlyWornIds = mutableSetOf<Int>()
        for (session in recentSessions) {
            recentlyWornIds.add(session.topId)
            recentlyWornIds.add(session.bottomId)
            recentlyWornIds.add(session.shoesId)
            session.jacketId?.let { recentlyWornIds.add(it) }
            session.accessoryId?.let { recentlyWornIds.add(it) }
        }

        val tops = clothes.filter { it.category == "shirt" || it.category == "t-shirt" || it.category == "hoodie" }.take(10)
        val bottoms = clothes.filter { it.category == "pants" || it.category == "jeans" }.take(10)
        val shoes = clothes.filter { it.category == "shoes" }.take(10)
        val accessories = clothes.filter { it.category == "accessories" }
        val jackets = clothes.filter { it.category == "jacket" }

        val combinations = mutableListOf<OutfitCombination>()

        for (top in tops) {
            val isTopRecent = recentlyWornIds.contains(top.id)
            for (bottom in bottoms) {
                val isBottomRecent = recentlyWornIds.contains(bottom.id)
                for (shoe in shoes) {
                    val isShoeRecent = recentlyWornIds.contains(shoe.id)

                    // Find suitable jacket & accessory
                    val suitableJacket = jackets.find { 
                        it.season.lowercase() == season.lowercase() || it.season.lowercase() == "all"
                    }
                    val suitableAccessory = accessories.find { 
                        it.occasions.lowercase().contains(occasion.lowercase()) 
                    } ?: accessories.firstOrNull()

                    val isJacketRecent = suitableJacket != null && recentlyWornIds.contains(suitableJacket.id)
                    val isAccessoryRecent = suitableAccessory != null && recentlyWornIds.contains(suitableAccessory.id)

                    // Option 1: Classic Minimalist Combo (Top + Bottom + Shoes - No Accessory, No Jacket)
                    val isAnyRecentBase = isTopRecent || isBottomRecent || isShoeRecent
                    var scoreBase = calculateCompatibility(
                        top = top,
                        bottom = bottom,
                        shoes = shoe,
                        jacket = null,
                        accessory = null,
                        targetOccasion = occasion
                    )
                    if (isAnyRecentBase) scoreBase = (scoreBase - 35).coerceAtLeast(10)
                    combinations.add(
                        OutfitCombination(
                            top = top,
                            bottom = bottom,
                            shoes = shoe,
                            accessory = null,
                            jacket = null,
                            averageScore = scoreBase,
                            occasion = occasion,
                            containRecentWorn = isAnyRecentBase
                        )
                    )

                    // Option 2: Accessory Enhanced Combo (Top + Bottom + Shoes + Accessory)
                    if (suitableAccessory != null) {
                        val isAnyRecentAcc = isAnyRecentBase || isAccessoryRecent
                        var scoreAcc = calculateCompatibility(
                            top = top,
                            bottom = bottom,
                            shoes = shoe,
                            jacket = null,
                            accessory = suitableAccessory,
                            targetOccasion = occasion
                        )
                        if (isAnyRecentAcc) scoreAcc = (scoreAcc - 35).coerceAtLeast(10)
                        combinations.add(
                            OutfitCombination(
                                top = top,
                                bottom = bottom,
                                shoes = shoe,
                                accessory = suitableAccessory,
                                jacket = null,
                                averageScore = scoreAcc + 2, // Slight bonus for accessorizing!
                                occasion = occasion,
                                containRecentWorn = isAnyRecentAcc
                            )
                        )
                    }

                    // Option 3: Full Layered Cover Combo (Top + Bottom + Shoes + Jacket + Accessory)
                    if (suitableJacket != null) {
                        val isAnyRecentFull = isAnyRecentBase || isJacketRecent || (suitableAccessory != null && isAccessoryRecent)
                        var scoreFull = calculateCompatibility(
                            top = top,
                            bottom = bottom,
                            shoes = shoe,
                            jacket = suitableJacket,
                            accessory = suitableAccessory,
                            targetOccasion = occasion
                        )
                        if (isAnyRecentFull) scoreFull = (scoreFull - 35).coerceAtLeast(10)
                        combinations.add(
                            OutfitCombination(
                                top = top,
                                bottom = bottom,
                                shoes = shoe,
                                accessory = suitableAccessory,
                                jacket = suitableJacket,
                                averageScore = scoreFull + 4, // layered style bonus!
                                occasion = occasion,
                                containRecentWorn = isAnyRecentFull
                            )
                        )
                    }
                }
            }
        }

        // Rank by average score and return top results (limit to avoid combinatorial lag)
        return combinations.sortedByDescending { it.averageScore }.take(40)
    }
}
