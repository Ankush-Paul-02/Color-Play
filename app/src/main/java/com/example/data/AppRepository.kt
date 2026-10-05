package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: AppDao) {
    val allDrawings: Flow<List<DrawingEntity>> = dao.getAllDrawings()
    val favoriteDrawings: Flow<List<DrawingEntity>> = dao.getFavoriteDrawings()
    val drawingsCount: Flow<Int> = dao.getDrawingsCount()

    val allGameProgress: Flow<List<GameProgressEntity>> = dao.getAllGameProgress()
    val totalStars: Flow<Int?> = dao.getTotalStars()

    val parentalSettings: Flow<ParentalSettingsEntity?> = dao.getParentalSettings()

    suspend fun getDrawing(id: Long): DrawingEntity? = dao.getDrawingById(id)

    suspend fun saveDrawing(drawing: DrawingEntity): Long = dao.insertDrawing(drawing)

    suspend fun updateDrawing(drawing: DrawingEntity) = dao.updateDrawing(drawing)

    suspend fun deleteDrawing(id: Long) = dao.deleteDrawingById(id)

    suspend fun getGameProgress(gameId: String): GameProgressEntity? = dao.getGameProgress(gameId)

    suspend fun updateGameProgress(progress: GameProgressEntity) = dao.insertOrUpdateGameProgress(progress)

    suspend fun getParentalSettingsDirect(): ParentalSettingsEntity? = dao.getParentalSettingsDirect()

    suspend fun saveParentalSettings(settings: ParentalSettingsEntity) = dao.saveParentalSettings(settings)
}
