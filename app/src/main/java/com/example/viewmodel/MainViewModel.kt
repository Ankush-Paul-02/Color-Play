package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ColorPlayApplication
import com.example.audio.SoundPlayer
import com.example.data.AppRepository
import com.example.data.DrawingEntity
import com.example.data.GameProgressEntity
import com.example.data.ParentalSettingsEntity
import com.example.model.DrawingCanvasData
import com.example.model.GameType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface Screen {
    data object Home : Screen
    data class Coloring(val templateId: String = "rooster", val drawingId: Long? = null) : Screen
    data object GamesHub : Screen
    data object ColorMatch : Screen
    data object NumberColor : Screen
    data object ShapeDetective : Screen
    data object AlphabetColor : Screen
    data object Gallery : Screen
    data object TrophyRoom : Screen
    data object ParentalControls : Screen
}

class MainViewModel(
    val repository: AppRepository,
    val soundPlayer: SoundPlayer
) : ViewModel() {

    // Navigation Stack
    private val _navStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val navStack: StateFlow<List<Screen>> = _navStack.asStateFlow()

    val currentScreen: Screen
        get() = _navStack.value.lastOrNull() ?: Screen.Home

    // Drawings Flow
    val allDrawings: StateFlow<List<DrawingEntity>> = repository.allDrawings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalStars: StateFlow<Int> = repository.totalStars
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val gameProgressList: StateFlow<List<GameProgressEntity>> = repository.allGameProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val parentalSettings: StateFlow<ParentalSettingsEntity?> = repository.parentalSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Active Drawing In Progress (for canvas persistence)
    private val _activeCanvasData = MutableStateFlow(DrawingCanvasData())
    val activeCanvasData: StateFlow<DrawingCanvasData> = _activeCanvasData.asStateFlow()

    private val _currentDrawingId = MutableStateFlow<Long?>(null)
    val currentDrawingId: StateFlow<Long?> = _currentDrawingId.asStateFlow()

    // Screen Time & Parental Lock
    private val _isTimeLocked = MutableStateFlow(false)
    val isTimeLocked: StateFlow<Boolean> = _isTimeLocked.asStateFlow()

    private val _todaySecondsPlayed = MutableStateFlow(0)
    val todaySecondsPlayed: StateFlow<Int> = _todaySecondsPlayed.asStateFlow()

    // Temporary parent gate challenge
    private val _showParentGate = MutableStateFlow(false)
    val showParentGate: StateFlow<Boolean> = _showParentGate.asStateFlow()
    private var onGateSuccessAction: (() -> Unit)? = null

    // Theme override
    private val _themeOverride = MutableStateFlow<String?>("system") // "light", "dark", "system"
    val themeOverride: StateFlow<String?> = _themeOverride.asStateFlow()

    private val _eyeCareMode = MutableStateFlow(false)
    val eyeCareMode: StateFlow<Boolean> = _eyeCareMode.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Initialize parental settings if not created
        viewModelScope.launch {
            val existing = repository.getParentalSettingsDirect()
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            if (existing == null) {
                val initSettings = ParentalSettingsEntity(
                    pinCode = "1234",
                    dailyLimitMinutes = 30,
                    usedTodayMinutes = 0,
                    lastActiveDate = todayStr,
                    soundFxEnabled = true,
                    musicEnabled = true,
                    themeMode = "system",
                    eyeCareWarmFilter = false
                )
                repository.saveParentalSettings(initSettings)
                _themeOverride.value = "system"
                _eyeCareMode.value = false
            } else {
                _themeOverride.value = existing.themeMode
                _eyeCareMode.value = existing.eyeCareWarmFilter
                soundPlayer.soundEnabled = existing.soundFxEnabled
                if (existing.lastActiveDate == todayStr) {
                    _todaySecondsPlayed.value = existing.usedTodayMinutes * 60
                } else {
                    _todaySecondsPlayed.value = 0
                    repository.saveParentalSettings(existing.copy(usedTodayMinutes = 0, lastActiveDate = todayStr))
                }
            }
        }
        startScreenTimeTracker()
    }

    private fun startScreenTimeTracker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _todaySecondsPlayed.value += 1
                val seconds = _todaySecondsPlayed.value
                val minutes = seconds / 60

                // Check limit every minute
                val settings = parentalSettings.value
                if (settings != null && settings.dailyLimitMinutes > 0) {
                    if (minutes >= settings.dailyLimitMinutes) {
                        _isTimeLocked.value = true
                    }
                }

                if (seconds % 60 == 0) {
                    val current = repository.getParentalSettingsDirect() ?: continue
                    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    repository.saveParentalSettings(
                        current.copy(usedTodayMinutes = minutes, lastActiveDate = todayStr)
                    )
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        soundPlayer.playClick()
        val currentList = _navStack.value.toMutableList()
        currentList.add(screen)
        _navStack.value = currentList
    }

    fun navigateAndClearTo(screen: Screen) {
        soundPlayer.playClick()
        if (screen is Screen.Coloring) {
            _currentDrawingId.value = screen.drawingId
            viewModelScope.launch {
                if (screen.drawingId != null) {
                    val drawing = repository.getDrawing(screen.drawingId)
                    if (drawing != null) {
                        _activeCanvasData.value = DrawingCanvasData.fromJson(drawing.strokesJson)
                    } else {
                        _activeCanvasData.value = DrawingCanvasData(templateId = screen.templateId)
                    }
                } else {
                    _activeCanvasData.value = DrawingCanvasData(templateId = screen.templateId)
                }
                _navStack.value = listOf(Screen.Home, screen)
            }
            return
        }
        if (screen == Screen.Home) {
            _navStack.value = listOf(Screen.Home)
        } else {
            _navStack.value = listOf(Screen.Home, screen)
        }
    }

    fun navigateToScreen(screen: Screen) {
        soundPlayer.playClick()
        if (screen is Screen.Coloring) {
            startColoring(screen.templateId, screen.drawingId)
        } else {
            navigateAndClearTo(screen)
        }
    }

    fun goBack(): Boolean {
        soundPlayer.playClick()
        val currentList = _navStack.value.toMutableList()
        return if (currentList.size > 1) {
            currentList.removeAt(currentList.lastIndex)
            _navStack.value = currentList
            true
        } else {
            false
        }
    }

    fun startColoring(templateId: String, drawingId: Long? = null) {
        _currentDrawingId.value = drawingId
        viewModelScope.launch {
            if (drawingId != null) {
                val drawing = repository.getDrawing(drawingId)
                if (drawing != null) {
                    _activeCanvasData.value = DrawingCanvasData.fromJson(drawing.strokesJson)
                } else {
                    _activeCanvasData.value = DrawingCanvasData(templateId = templateId)
                }
            } else {
                _activeCanvasData.value = DrawingCanvasData(templateId = templateId)
            }
            navigateTo(Screen.Coloring(templateId, drawingId))
        }
    }

    fun updateActiveCanvas(data: DrawingCanvasData) {
        _activeCanvasData.value = data
    }

    fun saveActiveDrawing(title: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val canvasData = _activeCanvasData.value
            val json = canvasData.toJson()
            val existingId = _currentDrawingId.value

            val entity = DrawingEntity(
                id = existingId ?: 0L,
                title = title.ifBlank { "My Artwork" },
                templateId = canvasData.templateId,
                strokesJson = json,
                createdAt = System.currentTimeMillis()
            )
            val newId = repository.saveDrawing(entity)
            _currentDrawingId.value = if (existingId != null && existingId > 0) existingId else newId
            soundPlayer.playChimeSuccess()
            // Award star for saving drawing!
            awardGameStars(GameType.COLOR_MATCH.id, starsToAdd = 1)
            onSuccess()
        }
    }

    fun deleteDrawing(id: Long) {
        viewModelScope.launch {
            repository.deleteDrawing(id)
            soundPlayer.playPop()
        }
    }

    fun toggleFavorite(drawing: DrawingEntity) {
        viewModelScope.launch {
            repository.updateDrawing(drawing.copy(isFavorite = !drawing.isFavorite))
            soundPlayer.playSparkle()
        }
    }

    fun awardGameStars(gameId: String, starsToAdd: Int, score: Int = 0) {
        viewModelScope.launch {
            val current = repository.getGameProgress(gameId) ?: GameProgressEntity(gameId = gameId)
            val updated = current.copy(
                stars = current.stars + starsToAdd,
                highScore = maxOf(current.highScore, score),
                completedCount = current.completedCount + 1,
                lastPlayed = System.currentTimeMillis()
            )
            repository.updateGameProgress(updated)
            soundPlayer.playChimeSuccess()
        }
    }

    // Parent Gate verification
    fun requestParentAccess(onSuccess: () -> Unit) {
        soundPlayer.playPop()
        onGateSuccessAction = onSuccess
        _showParentGate.value = true
    }

    fun onParentGateResult(success: Boolean) {
        _showParentGate.value = false
        if (success) {
            soundPlayer.playChimeSuccess()
            onGateSuccessAction?.invoke()
            onGateSuccessAction = null
        } else {
            soundPlayer.playErrorBuzz()
            onGateSuccessAction = null
        }
    }

    fun unlockScreenTimeTemporarily(additionalMinutes: Int) {
        viewModelScope.launch {
            _isTimeLocked.value = false
            val current = repository.getParentalSettingsDirect() ?: return@launch
            val currentUsedMins = _todaySecondsPlayed.value / 60
            val newLimit = maxOf(current.dailyLimitMinutes, currentUsedMins) + additionalMinutes
            repository.saveParentalSettings(current.copy(dailyLimitMinutes = newLimit))
        }
    }

    fun updateParentalSettings(
        pin: String,
        dailyLimit: Int,
        soundEnabled: Boolean,
        themeMode: String,
        eyeCare: Boolean
    ) {
        viewModelScope.launch {
            val current = repository.getParentalSettingsDirect() ?: ParentalSettingsEntity()
            val currentUsedMins = _todaySecondsPlayed.value / 60
            if (dailyLimit == 0 || dailyLimit > currentUsedMins) {
                _isTimeLocked.value = false
            }
            val updated = current.copy(
                pinCode = pin,
                dailyLimitMinutes = dailyLimit,
                soundFxEnabled = soundEnabled,
                themeMode = themeMode,
                eyeCareWarmFilter = eyeCare
            )
            repository.saveParentalSettings(updated)
            soundPlayer.soundEnabled = soundEnabled
            _themeOverride.value = themeMode
            _eyeCareMode.value = eyeCare
            soundPlayer.playChimeSuccess()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ColorPlayApplication)
                MainViewModel(
                    repository = application.container.repository,
                    soundPlayer = application.container.soundPlayer
                )
            }
        }
    }
}
