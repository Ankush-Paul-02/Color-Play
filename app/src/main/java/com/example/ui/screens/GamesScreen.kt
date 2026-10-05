package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlphabetCatalog
import com.example.model.ColorOption
import com.example.model.GameType
import com.example.model.KidGameColors
import com.example.model.KidShape
import com.example.model.StrokePoint
import com.example.ui.components.ConfettiEffect
import com.example.viewmodel.Screen
import kotlinx.coroutines.delay
import kotlin.random.Random

// -------------------------------------------------------------
// 1. GAMES HUB SCREEN
// -------------------------------------------------------------
@Composable
fun GamesHubScreen(
    totalStars: Int,
    onSelectGame: (Screen) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("games_hub_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Learning Games Playground! 🎮",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Earn stars while learning colors, counting, shapes & ABCs!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("⭐", fontSize = 28.sp)
                        }
                    }
                }
            }
        }

        item {
            GameCardBig(
                title = "Color Pop & Match",
                subtitle = "Identify target colors and pop flying balloons to score!",
                badge = "Visual Perception",
                emoji = "🎈",
                themeColor = Color(0xFFFF5252),
                testTag = "hub_card_color_match",
                onClick = { onSelectGame(Screen.ColorMatch) }
            )
        }

        item {
            GameCardBig(
                title = "Number Paint & Count",
                subtitle = "Match numbers 1-5 to colored zones to reveal surprise animals!",
                badge = "Math & Logic",
                emoji = "🔢",
                themeColor = Color(0xFFFF9F1A),
                testTag = "hub_card_number_color",
                onClick = { onSelectGame(Screen.NumberColor) }
            )
        }

        item {
            GameCardBig(
                title = "Shape Detective",
                subtitle = "Find and place geometric shapes into fun puzzles!",
                badge = "Shapes & Spatial",
                emoji = "⭐",
                themeColor = Color(0xFF2ED573),
                testTag = "hub_card_shape_detective",
                onClick = { onSelectGame(Screen.ShapeDetective) }
            )
        }

        item {
            GameCardBig(
                title = "ABC Phonics & Animal Tracing",
                subtitle = "Learn letter sounds, trace outlines, and meet cute animals!",
                badge = "Early Literacy",
                emoji = "🔤",
                themeColor = Color(0xFF1E90FF),
                testTag = "hub_card_alphabet_game",
                onClick = { onSelectGame(Screen.AlphabetColor) }
            )
        }
    }
}

@Composable
private fun GameCardBig(
    title: String,
    subtitle: String,
    badge: String,
    emoji: String,
    themeColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = themeColor.copy(alpha = 0.15f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = emoji, fontSize = 34.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = themeColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = badge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = themeColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// 2. COLOR POP & MATCH GAME
// -------------------------------------------------------------
@Composable
fun ColorMatchGameScreen(
    onAwardStars: (stars: Int, score: Int) -> Unit,
    onBack: () -> Unit,
    onPlayPopSound: () -> Unit = {},
    onPlaySuccessSound: () -> Unit = {},
    onPlayErrorSound: () -> Unit = {}
) {
    BackHandler { onBack() }

    var score by remember { mutableIntStateOf(0) }
    var targetColor by remember { mutableStateOf(KidGameColors.random()) }
    var balloons by remember { mutableStateOf(generateBalloons(targetColor)) }
    var showConfetti by remember { mutableStateOf(false) }

    fun nextRound() {
        val nextTarget = KidGameColors.random()
        targetColor = nextTarget
        balloons = generateBalloons(nextTarget)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Game Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("color_match_back")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐ Score: $score", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Target Color Banner
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pop the balloon that is:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(targetColor.color)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = targetColor.name.uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = targetColor.color
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Balloons Grid
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in balloons.chunked(3)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        for (balloon in row) {
                            BalloonItem(
                                colorOption = balloon,
                                onClick = {
                                    if (balloon.name == targetColor.name) {
                                        onPlayPopSound()
                                        onPlaySuccessSound()
                                        score += 10
                                        showConfetti = true
                                        onAwardStars(1, score)
                                        nextRound()
                                    } else {
                                        onPlayErrorSound()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            if (showConfetti) {
                ConfettiEffect(onFinished = { showConfetti = false })
            }
        }
    }
}

private fun generateBalloons(target: ColorOption): List<ColorOption> {
    val list = mutableListOf(target)
    val others = KidGameColors.filter { it.name != target.name }.shuffled()
    list.addAll(others.take(5))
    return list.shuffled()
}

@Composable
private fun BalloonItem(
    colorOption: ColorOption,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = colorOption.color,
        modifier = Modifier
            .size(86.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .testTag("balloon_${colorOption.name.lowercase()}"),
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🎈", fontSize = 28.sp)
                Text(
                    text = colorOption.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 3. NUMBER PAINT & COUNT GAME
// -------------------------------------------------------------
@Composable
fun NumberColorGameScreen(
    onAwardStars: (stars: Int, score: Int) -> Unit,
    onBack: () -> Unit,
    onPlayPopSound: () -> Unit = {},
    onPlaySuccessSound: () -> Unit = {}
) {
    BackHandler { onBack() }

    val numberColors = remember {
        listOf(
            1 to Color(0xFFFF3838), // Red
            2 to Color(0xFFFF9F1A), // Orange
            3 to Color(0xFFFFD32A), // Yellow
            4 to Color(0xFF2ED573), // Green
            5 to Color(0xFF1E90FF)  // Blue
        )
    }

    var selectedNumber by remember { mutableIntStateOf(1) }
    val coloredSections = remember { mutableStateListOf<Int>() }
    var showCelebration by remember { mutableStateOf(false) }

    val totalSections = 8

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("number_color_back")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Color By Numbers 🔢",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "${coloredSections.size} / $totalSections",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Puzzle Interactive Canvas
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("number_puzzle_canvas"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Interactive zones representing a cute cartoon puppy or rooster
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tap a number below, then color its matching zones!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        PuzzleZone(id = 1, requiredNum = 1, colored = coloredSections.contains(1), selectedNum = selectedNumber, onColored = {
                            if (!coloredSections.contains(1)) coloredSections.add(1)
                            onPlayPopSound()
                        })
                        PuzzleZone(id = 2, requiredNum = 2, colored = coloredSections.contains(2), selectedNum = selectedNumber, onColored = {
                            if (!coloredSections.contains(2)) coloredSections.add(2)
                            onPlayPopSound()
                        })
                        PuzzleZone(id = 3, requiredNum = 3, colored = coloredSections.contains(3), selectedNum = selectedNumber, onColored = {
                            if (!coloredSections.contains(3)) coloredSections.add(3)
                            onPlayPopSound()
                        })
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        PuzzleZone(id = 4, requiredNum = 4, colored = coloredSections.contains(4), selectedNum = selectedNumber, onColored = {
                            if (!coloredSections.contains(4)) coloredSections.add(4)
                            onPlayPopSound()
                        })
                        PuzzleZone(id = 5, requiredNum = 5, colored = coloredSections.contains(5), selectedNum = selectedNumber, onColored = {
                            if (!coloredSections.contains(5)) coloredSections.add(5)
                            onPlayPopSound()
                        })
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        PuzzleZone(id = 6, requiredNum = 1, colored = coloredSections.contains(6), selectedNum = selectedNumber, onColored = {
                            if (!coloredSections.contains(6)) coloredSections.add(6)
                            onPlayPopSound()
                        })
                        PuzzleZone(id = 7, requiredNum = 3, colored = coloredSections.contains(7), selectedNum = selectedNumber, onColored = {
                            if (!coloredSections.contains(7)) coloredSections.add(7)
                            onPlayPopSound()
                        })
                        PuzzleZone(id = 8, requiredNum = 2, colored = coloredSections.contains(8), selectedNum = selectedNumber, onColored = {
                            if (!coloredSections.contains(8)) coloredSections.add(8)
                            onPlayPopSound()
                        })
                    }

                    if (coloredSections.size >= totalSections) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "🎉 Awesome! Puzzle Complete! 🐶",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2ED573)
                        )
                    }
                }

                if (showCelebration) {
                    ConfettiEffect(onFinished = { showCelebration = false })
                }
            }
        }

        // Trigger finish star
        LaunchedEffect(coloredSections.size) {
            if (coloredSections.size >= totalSections && !showCelebration) {
                showCelebration = true
                onPlaySuccessSound()
                onAwardStars(3, 100)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Number Selector Bar
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for ((num, color) in numberColors) {
                    val isChosen = selectedNumber == num
                    Surface(
                        shape = CircleShape,
                        color = color,
                        modifier = Modifier
                            .size(if (isChosen) 54.dp else 44.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (isChosen) 3.dp else 1.dp,
                                color = if (isChosen) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedNumber = num
                                onPlayPopSound()
                            }
                            .testTag("number_picker_$num"),
                        shadowElevation = if (isChosen) 4.dp else 1.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$num",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = if (isChosen) 22.sp else 18.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PuzzleZone(
    id: Int,
    requiredNum: Int,
    colored: Boolean,
    selectedNum: Int,
    onColored: () -> Unit
) {
    val numberColors = mapOf(
        1 to Color(0xFFFF3838),
        2 to Color(0xFFFF9F1A),
        3 to Color(0xFFFFD32A),
        4 to Color(0xFF2ED573),
        5 to Color(0xFF1E90FF)
    )

    val fillColor = if (colored) (numberColors[requiredNum] ?: Color.LightGray) else Color(0xFFF5F6FA)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = fillColor,
        modifier = Modifier
            .size(76.dp)
            .border(2.dp, Color(0xFFDFE4EA), RoundedCornerShape(16.dp))
            .clickable {
                if (selectedNum == requiredNum) {
                    onColored()
                }
            }
            .testTag("puzzle_zone_$id"),
        shadowElevation = if (colored) 3.dp else 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (colored) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Done",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Text(
                    text = "$requiredNum",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF718093)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 4. SHAPE DETECTIVE GAME
// -------------------------------------------------------------
@Composable
fun ShapeDetectiveGameScreen(
    onAwardStars: (stars: Int, score: Int) -> Unit,
    onBack: () -> Unit,
    onPlayPopSound: () -> Unit = {},
    onPlaySuccessSound: () -> Unit = {},
    onPlayErrorSound: () -> Unit = {}
) {
    BackHandler { onBack() }

    val shapes = KidShape.entries
    var targetShape by remember { mutableStateOf(shapes.random()) }
    var score by remember { mutableIntStateOf(0) }
    var showConfetti by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("shape_game_back")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Shape Detective ⭐",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "Score: $score",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Target Silhouette Slot Card
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Find the matching shape!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(20.dp))

                // The shape outline slot
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    modifier = Modifier.size(130.dp),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = targetShape.emoji, fontSize = 68.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = targetShape.label,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (showConfetti) {
            ConfettiEffect(onFinished = { showConfetti = false })
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Candidate shape options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (shape in shapes) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .size(62.dp)
                        .clickable {
                            if (shape == targetShape) {
                                onPlayPopSound()
                                onPlaySuccessSound()
                                score += 10
                                showConfetti = true
                                onAwardStars(1, score)
                                targetShape = shapes.random()
                            } else {
                                onPlayErrorSound()
                            }
                        }
                        .testTag("shape_option_${shape.name.lowercase()}"),
                    shadowElevation = 3.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = shape.emoji, fontSize = 32.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. ABC PHONICS & ANIMAL TRACING
// -------------------------------------------------------------
@Composable
fun AlphabetColorGameScreen(
    onAwardStars: (stars: Int, score: Int) -> Unit,
    onBack: () -> Unit,
    onPlayPopSound: () -> Unit = {}
) {
    BackHandler { onBack() }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val item = AlphabetCatalog[selectedIndex]
    val drawnPoints = remember { mutableStateListOf<StrokePoint>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("alphabet_game_back")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "ABC Phonics & Animals 🔤",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = {
                drawnPoints.clear()
                onPlayPopSound()
            }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Clear")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Animal & Phonics Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(item.strokeColor).copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.emoji, fontSize = 48.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = item.prompt,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(item.strokeColor)
                    )
                    Text(
                        text = "Trace the letter '${item.letter}' with your finger!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tracing Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(2.dp, Color(item.strokeColor).copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .testTag("letter_tracing_canvas"),
            contentAlignment = Alignment.Center
        ) {
            // Big outlined letter in background
            Text(
                text = item.letter,
                fontSize = 180.sp,
                fontWeight = FontWeight.Black,
                color = Color.LightGray.copy(alpha = 0.35f),
                textAlign = TextAlign.Center
            )

            // Canvas for user finger trace
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(item.letter) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                drawnPoints.add(StrokePoint(change.position.x, change.position.y))
                            }
                        )
                    }
            ) {
                if (drawnPoints.size > 1) {
                    val path = Path().apply {
                        moveTo(drawnPoints.first().x, drawnPoints.first().y)
                        for (i in 1 until drawnPoints.size) {
                            lineTo(drawnPoints[i].x, drawnPoints[i].y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color(item.strokeColor),
                        style = Stroke(width = 24f, cap = StrokeCap.Round)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Alphabet Letters Carousel
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(AlphabetCatalog.indices.toList()) { idx ->
                val itm = AlphabetCatalog[idx]
                val isSel = selectedIndex == idx
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSel) Color(itm.strokeColor) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {
                            selectedIndex = idx
                            drawnPoints.clear()
                            onPlayPopSound()
                            onAwardStars(1, 10)
                        }
                        .testTag("letter_tab_${itm.letter}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = itm.letter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
