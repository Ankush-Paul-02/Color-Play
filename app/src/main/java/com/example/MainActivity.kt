package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.DrawingEntity
import com.example.data.GameProgressEntity
import com.example.data.ParentalSettingsEntity
import com.example.model.DrawingCanvasData
import com.example.model.GameType
import com.example.ui.components.AppDrawerContent
import com.example.ui.components.AppNavigationRail
import com.example.ui.components.KidTopBar
import com.example.ui.components.ParentGateDialog
import com.example.ui.components.ScreenTimeLockScreen
import com.example.ui.screens.AlphabetColorGameScreen
import com.example.ui.screens.ColorMatchGameScreen
import com.example.ui.screens.ColoringScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.GamesHubScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NumberColorGameScreen
import com.example.ui.screens.ParentalControlsScreen
import com.example.ui.screens.PhotoToArtScreen
import com.example.ui.screens.ShapeDetectiveGameScreen
import com.example.ui.screens.TrophyScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Pure Constructor Injection via ViewModelProvider.Factory
            val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
            val themeMode by viewModel.themeOverride.collectAsState()
            val eyeCareMode by viewModel.eyeCareMode.collectAsState()

            val isDark = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDark) {
                Box(modifier = Modifier.fillMaxSize()) {
                    ColorPlayApp(viewModel = viewModel)

                    // Eye-care warm amber filter overlay
                    if (eyeCareMode) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFFF9F1A).copy(alpha = 0.08f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorPlayApp(viewModel: MainViewModel) {
    val navStack by viewModel.navStack.collectAsState()
    val currentScreen = navStack.lastOrNull() ?: Screen.Home

    val allDrawings by viewModel.allDrawings.collectAsState()
    val totalStars by viewModel.totalStars.collectAsState()
    val gameProgressList by viewModel.gameProgressList.collectAsState()
    val parentalSettings by viewModel.parentalSettings.collectAsState()
    val activeCanvasData by viewModel.activeCanvasData.collectAsState()
    val isTimeLocked by viewModel.isTimeLocked.collectAsState()
    val todaySeconds by viewModel.todaySecondsPlayed.collectAsState()
    val showParentGate by viewModel.showParentGate.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Proper Android Back Navigation:
    // If the drawer is open, back closes the drawer.
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    // If on a subscreen and drawer is closed, back navigates back through ViewModel navigation stack
    BackHandler(enabled = !drawerState.isOpen && currentScreen !is Screen.Home) {
        viewModel.goBack()
    }

    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 720

    if (isWideScreen) {
        // Adaptive Tablet / Landscape Canonical Layout with NavigationRail
        Row(modifier = Modifier.fillMaxSize()) {
            AppNavigationRail(
                currentScreen = currentScreen,
                totalStars = totalStars,
                onNavigate = { screen ->
                    viewModel.navigateAndClearTo(screen)
                },
                onParentalControlsClick = {
                    viewModel.requestParentAccess {
                        viewModel.navigateTo(Screen.ParentalControls)
                    }
                }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                ScreenScaffoldContent(
                    currentScreen = currentScreen,
                    totalStars = totalStars,
                    allDrawings = allDrawings,
                    gameProgressList = gameProgressList,
                    parentalSettings = parentalSettings,
                    activeCanvasData = activeCanvasData,
                    todaySeconds = todaySeconds,
                    isWideScreen = true,
                    viewModel = viewModel,
                    onOpenDrawer = {}
                )
            }
        }
    } else {
        // Mobile Compact Layout with Modal Navigation Drawer
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen || (currentScreen is Screen.Home),
            drawerContent = {
                AppDrawerContent(
                    currentScreen = currentScreen,
                    totalStars = totalStars,
                    onNavigate = { screen ->
                        viewModel.navigateAndClearTo(screen)
                    },
                    onParentalControlsClick = {
                        viewModel.requestParentAccess {
                            viewModel.navigateTo(Screen.ParentalControls)
                        }
                    },
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )
            },
            modifier = Modifier.testTag("app_navigation_drawer")
        ) {
            ScreenScaffoldContent(
                currentScreen = currentScreen,
                totalStars = totalStars,
                allDrawings = allDrawings,
                gameProgressList = gameProgressList,
                parentalSettings = parentalSettings,
                activeCanvasData = activeCanvasData,
                todaySeconds = todaySeconds,
                isWideScreen = false,
                viewModel = viewModel,
                onOpenDrawer = {
                    scope.launch { drawerState.open() }
                }
            )
        }
    }

    // Parent Gate Modal
    if (showParentGate) {
        ParentGateDialog(
            parentPin = parentalSettings?.pinCode ?: "1234",
            onDismiss = { viewModel.onParentGateResult(false) },
            onSuccess = { viewModel.onParentGateResult(true) }
        )
    }

    // Screen Time Lock Overlay
    if (isTimeLocked) {
        val usedMins = todaySeconds / 60
        val limitMins = parentalSettings?.dailyLimitMinutes ?: 30
        ScreenTimeLockScreen(
            usedMinutes = usedMins,
            dailyLimitMinutes = limitMins,
            onParentUnlockRequest = {
                viewModel.requestParentAccess {
                    viewModel.unlockScreenTimeTemporarily(15)
                }
            }
        )
    }
}

@Composable
private fun ScreenScaffoldContent(
    currentScreen: Screen,
    totalStars: Int,
    allDrawings: List<DrawingEntity>,
    gameProgressList: List<GameProgressEntity>,
    parentalSettings: ParentalSettingsEntity?,
    activeCanvasData: DrawingCanvasData,
    todaySeconds: Int,
    isWideScreen: Boolean,
    viewModel: MainViewModel,
    onOpenDrawer: () -> Unit
) {
    // Show TopBar only on Home and Games Hub. Subscreens (Gallery, Trophies, Parental Controls, Coloring, Games) have their own dedicated top bars.
    val showTopBar = currentScreen is Screen.Home || currentScreen is Screen.GamesHub

    Scaffold(
        topBar = {
            if (showTopBar) {
                val title = when (currentScreen) {
                    is Screen.Home -> "Color & Play 🎨"
                    is Screen.GamesHub -> "Games Hub 🎮"
                    else -> "Color & Play"
                }

                val isSubScreen = currentScreen !is Screen.Home

                KidTopBar(
                    title = title,
                    showBackButton = isSubScreen,
                    showMenuButton = !isWideScreen,
                    totalStars = totalStars,
                    onBackClick = { viewModel.goBack() },
                    onOpenDrawer = onOpenDrawer,
                    onStarsClick = {
                        viewModel.navigateTo(Screen.TrophyRoom)
                    }
                )
            }
        },
        contentWindowInsets = if (showTopBar) ScaffoldDefaults.contentWindowInsets else WindowInsets(0, 0, 0, 0),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 1200.dp)
            ) {
                when (val screen = currentScreen) {
                    is Screen.Home -> {
                        HomeScreen(
                            drawings = allDrawings,
                            totalStars = totalStars,
                            onStartColoring = { templateId, drawingId ->
                                viewModel.startColoring(templateId, drawingId)
                            },
                            onOpenPhotoToArt = {
                                viewModel.navigateTo(Screen.PhotoToArt)
                            },
                            onOpenGamesHub = {
                                viewModel.navigateTo(Screen.GamesHub)
                            },
                            onOpenGame = { gameScreen ->
                                viewModel.navigateTo(gameScreen)
                            },
                            onOpenGallery = {
                                viewModel.navigateTo(Screen.Gallery)
                            },
                            onOpenTrophies = {
                                viewModel.navigateTo(Screen.TrophyRoom)
                            },
                            onParentalControlsClick = {
                                viewModel.requestParentAccess {
                                    viewModel.navigateTo(Screen.ParentalControls)
                                }
                            }
                        )
                    }

                    is Screen.Coloring -> {
                        ColoringScreen(
                            templateId = screen.templateId,
                            initialCanvasData = activeCanvasData,
                            drawingId = screen.drawingId,
                            onSaveDrawing = { title ->
                                viewModel.saveActiveDrawing(title) {}
                            },
                            onBack = {
                                viewModel.goBack()
                            },
                            onCanvasUpdated = { canvasData ->
                                viewModel.updateActiveCanvas(canvasData)
                            },
                            onPlaySoundPop = {
                                viewModel.soundPlayer.playPop()
                            },
                            onPlayBrushSound = {
                                viewModel.soundPlayer.playBrushStroke()
                            }
                        )
                    }

                    is Screen.GamesHub -> {
                        GamesHubScreen(
                            totalStars = totalStars,
                            onSelectGame = { gameScreen ->
                                viewModel.navigateTo(gameScreen)
                            },
                            onBack = {
                                viewModel.goBack()
                            }
                        )
                    }

                    is Screen.ColorMatch -> {
                        ColorMatchGameScreen(
                            onAwardStars = { stars, score ->
                                viewModel.awardGameStars(GameType.COLOR_MATCH.id, stars, score)
                            },
                            onBack = { viewModel.goBack() },
                            onPlayPopSound = { viewModel.soundPlayer.playPop() },
                            onPlaySuccessSound = { viewModel.soundPlayer.playChimeSuccess() },
                            onPlayErrorSound = { viewModel.soundPlayer.playErrorBuzz() }
                        )
                    }

                    is Screen.NumberColor -> {
                        NumberColorGameScreen(
                            onAwardStars = { stars, score ->
                                viewModel.awardGameStars(GameType.NUMBER_COLOR.id, stars, score)
                            },
                            onBack = { viewModel.goBack() },
                            onPlayPopSound = { viewModel.soundPlayer.playPop() },
                            onPlaySuccessSound = { viewModel.soundPlayer.playChimeSuccess() }
                        )
                    }

                    is Screen.ShapeDetective -> {
                        ShapeDetectiveGameScreen(
                            onAwardStars = { stars, score ->
                                viewModel.awardGameStars(GameType.SHAPE_DETECTIVE.id, stars, score)
                            },
                            onBack = { viewModel.goBack() },
                            onPlayPopSound = { viewModel.soundPlayer.playPop() },
                            onPlaySuccessSound = { viewModel.soundPlayer.playChimeSuccess() },
                            onPlayErrorSound = { viewModel.soundPlayer.playErrorBuzz() }
                        )
                    }

                    is Screen.AlphabetColor -> {
                        AlphabetColorGameScreen(
                            onAwardStars = { stars, score ->
                                viewModel.awardGameStars(GameType.ALPHABET_COLOR.id, stars, score)
                            },
                            onBack = { viewModel.goBack() },
                            onPlayPopSound = { viewModel.soundPlayer.playPop() }
                        )
                    }

                    is Screen.PhotoToArt -> {
                        PhotoToArtScreen(
                            onStartColoring = { lineArtBitmap ->
                                viewModel.startColoringFromPhoto(lineArtBitmap)
                            },
                            onBack = { viewModel.goBack() },
                            onPlayPopSound = { viewModel.soundPlayer.playPop() },
                            onPlaySuccessSound = { viewModel.soundPlayer.playChimeSuccess() }
                        )
                    }

                    is Screen.Gallery -> {
                        GalleryScreen(
                            drawings = allDrawings,
                            onOpenDrawing = { templateId, drawingId ->
                                viewModel.startColoring(templateId, drawingId)
                            },
                            onDeleteDrawing = { id ->
                                viewModel.deleteDrawing(id)
                            },
                            onToggleFavorite = { drawing ->
                                viewModel.toggleFavorite(drawing)
                            },
                            onStartNewDrawing = {
                                viewModel.startColoring("free_draw", null)
                            },
                            onOpenPhotoToArt = {
                                viewModel.navigateTo(Screen.PhotoToArt)
                            },
                            onBack = {
                                viewModel.goBack()
                            }
                        )
                    }

                    is Screen.TrophyRoom -> {
                        TrophyScreen(
                            totalStars = totalStars,
                            gameProgressList = gameProgressList,
                            onBack = { viewModel.goBack() }
                        )
                    }

                    is Screen.ParentalControls -> {
                        ParentalControlsScreen(
                            settings = parentalSettings,
                            todaySeconds = todaySeconds,
                            totalDrawingsCount = allDrawings.size,
                            totalStars = totalStars,
                            onSaveSettings = { pin, limit, sound, theme, eyeCare ->
                                viewModel.updateParentalSettings(pin, limit, sound, theme, eyeCare)
                            },
                            onBack = { viewModel.goBack() }
                        )
                    }
                }
            }
        }
    }
}
