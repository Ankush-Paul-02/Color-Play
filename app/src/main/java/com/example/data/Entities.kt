package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drawings")
data class DrawingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val templateId: String,
    val strokesJson: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "game_progress")
data class GameProgressEntity(
    @PrimaryKey val gameId: String,
    val level: Int = 1,
    val stars: Int = 0,
    val highScore: Int = 0,
    val completedCount: Int = 0,
    val lastPlayed: Long = System.currentTimeMillis()
)

@Entity(tableName = "parental_settings")
data class ParentalSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val pinCode: String = "1234",
    val dailyLimitMinutes: Int = 30, // 0 = unlimited
    val usedTodayMinutes: Int = 0,
    val lastActiveDate: String = "",
    val soundFxEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val themeMode: String = "system", // "light", "dark", "system"
    val eyeCareWarmFilter: Boolean = false
)
