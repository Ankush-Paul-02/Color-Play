package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Drawings ---
    @Query("SELECT * FROM drawings ORDER BY createdAt DESC")
    fun getAllDrawings(): Flow<List<DrawingEntity>>

    @Query("SELECT * FROM drawings WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteDrawings(): Flow<List<DrawingEntity>>

    @Query("SELECT * FROM drawings WHERE id = :id LIMIT 1")
    suspend fun getDrawingById(id: Long): DrawingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrawing(drawing: DrawingEntity): Long

    @Update
    suspend fun updateDrawing(drawing: DrawingEntity)

    @Query("DELETE FROM drawings WHERE id = :id")
    suspend fun deleteDrawingById(id: Long)

    @Query("SELECT COUNT(*) FROM drawings")
    fun getDrawingsCount(): Flow<Int>

    // --- Game Progress ---
    @Query("SELECT * FROM game_progress")
    fun getAllGameProgress(): Flow<List<GameProgressEntity>>

    @Query("SELECT * FROM game_progress WHERE gameId = :gameId LIMIT 1")
    suspend fun getGameProgress(gameId: String): GameProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGameProgress(progress: GameProgressEntity)

    @Query("SELECT SUM(stars) FROM game_progress")
    fun getTotalStars(): Flow<Int?>

    // --- Parental Settings ---
    @Query("SELECT * FROM parental_settings WHERE id = 1 LIMIT 1")
    fun getParentalSettings(): Flow<ParentalSettingsEntity?>

    @Query("SELECT * FROM parental_settings WHERE id = 1 LIMIT 1")
    suspend fun getParentalSettingsDirect(): ParentalSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveParentalSettings(settings: ParentalSettingsEntity)
}
