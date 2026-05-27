package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothDao {
    @Query("SELECT * FROM clothes ORDER BY timestamp DESC")
    fun getAllClothes(): Flow<List<ClothingEntity>>

    @Query("SELECT * FROM clothes WHERE category = :category ORDER BY timestamp DESC")
    fun getClothesByCategory(category: String): Flow<List<ClothingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothing(item: ClothingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothes(items: List<ClothingEntity>)

    @Update
    suspend fun updateClothing(item: ClothingEntity)

    @Delete
    suspend fun deleteClothing(item: ClothingEntity)

    @Query("SELECT * FROM clothes WHERE id = :id LIMIT 1")
    suspend fun getClothingById(id: Int): ClothingEntity?

    @Query("SELECT * FROM outfit_history ORDER BY wornDate DESC")
    fun getOutfitHistory(): Flow<List<OutfitHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfitHistory(history: OutfitHistoryEntity): Long

    @Query("DELETE FROM outfit_history")
    suspend fun clearOutfitHistory()

    @Query("DELETE FROM clothes")
    suspend fun clearAllClothes()
}
