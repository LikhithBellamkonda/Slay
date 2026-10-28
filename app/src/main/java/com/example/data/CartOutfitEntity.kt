package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_outfits")
data class CartOutfitEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val topId: Int,
    val bottomId: Int,
    val shoesId: Int,
    val accessoryId: Int? = null,
    val jacketId: Int? = null,
    val occasion: String,
    val compatibilityScore: Int,
    val savedDate: Long = System.currentTimeMillis()
)
