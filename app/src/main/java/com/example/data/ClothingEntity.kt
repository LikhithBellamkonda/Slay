package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothes")
data class ClothingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val category: String, // "shirt", "t-shirt", "pants", "jeans", "hoodie", "jacket", "shoes", "accessories"
    val name: String,
    val color: String, // Hex string or descriptive name like "White", "Navy Blue"
    val colorHex: String, // To render dynamically
    val style: String, // "Formal", "Casual", "Streetwear", "Sporty", "Elegant"
    val season: String, // "Summer", "Autumn", "Winter", "Spring", "All"
    val occasions: String, // Comma separated: "Casual,Office,Travel"
    val wornCount: Int = 0,
    val isFavorite: Boolean = false,
    val embeddingVector: String, // Serialized array of floats: "0.1,0.3,-0.2,..." to calculate style match
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "outfit_history")
data class OutfitHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val topId: Int,
    val bottomId: Int,
    val shoesId: Int,
    val accessoryId: Int? = null,
    val jacketId: Int? = null,
    val occasion: String,
    val compatibilityScore: Int,
    val wornDate: Long = System.currentTimeMillis()
)

data class OutfitCombination(
    val top: ClothingEntity,
    val bottom: ClothingEntity,
    val shoes: ClothingEntity,
    val accessory: ClothingEntity? = null,
    val jacket: ClothingEntity? = null,
    val averageScore: Int,
    val occasion: String,
    val containRecentWorn: Boolean = false
)
