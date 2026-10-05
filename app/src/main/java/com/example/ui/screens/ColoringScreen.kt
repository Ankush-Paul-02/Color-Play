package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrushMode
import com.example.model.DrawingCanvasData
import com.example.model.DrawingPath
import com.example.model.PlacedStamp
import com.example.model.StampType
import com.example.model.StrokePoint
import com.example.model.TemplateRegistry
import com.example.ui.components.ConfettiEffect
import com.example.ui.theme.DrawingColors
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringScreen(
    templateId: String,
    initialCanvasData: DrawingCanvasData,
    drawingId: Long?,
    onSaveDrawing: (title: String) -> Unit,
    onBack: () -> Unit,
    onPlaySoundPop: () -> Unit = {},
    onPlayBrushSound: () -> Unit = {}
) {
    BackHandler { onBack() }

    val template = remember(templateId) { TemplateRegistry.getById(templateId) }

    // Active strokes and undo/redo stacks
    val paths = remember { mutableStateListOf<DrawingPath>().apply { addAll(initialCanvasData.paths) } }
    val undonePaths = remember { mutableStateListOf<DrawingPath>() }

    val stamps = remember { mutableStateListOf<PlacedStamp>().apply { addAll(initialCanvasData.stamps) } }
    val undoneStamps = remember { mutableStateListOf<PlacedStamp>() }

    // Selected drawing tool properties
    var selectedColor by remember { mutableLongStateOf(0xFFFF3838) }
    var selectedBrushMode by remember { mutableStateOf(BrushMode.MARKER) }
    var strokeWidth by remember { mutableFloatStateOf(24f) }
    var selectedStampType by remember { mutableStateOf(StampType.STAR) }
    var showStampPicker by remember { mutableStateOf(false) }

    // Save Dialog State
    var showSaveDialog by remember { mutableStateOf(false) }
    var drawingTitle by remember { mutableStateOf(template.defaultTitle) }
    var showCelebration by remember { mutableStateOf(false) }

    // Current in-progress stroke
    var currentStrokePoints by remember { mutableStateOf<List<StrokePoint>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = template.emoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = template.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("coloring_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                // Undo
                IconButton(
                    onClick = {
                        if (paths.isNotEmpty()) {
                            val removed = paths.removeAt(paths.lastIndex)
                            undonePaths.add(removed)
                            onPlaySoundPop()
                        } else if (stamps.isNotEmpty()) {
                            val removed = stamps.removeAt(stamps.lastIndex)
                            undoneStamps.add(removed)
                            onPlaySoundPop()
                        }
                    },
                    enabled = paths.isNotEmpty() || stamps.isNotEmpty(),
                    modifier = Modifier.testTag("undo_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo"
                    )
                }

                // Redo
                IconButton(
                    onClick = {
                        if (undonePaths.isNotEmpty()) {
                            val restored = undonePaths.removeAt(undonePaths.lastIndex)
                            paths.add(restored)
                            onPlaySoundPop()
                        } else if (undoneStamps.isNotEmpty()) {
                            val restored = undoneStamps.removeAt(undoneStamps.lastIndex)
                            stamps.add(restored)
                            onPlaySoundPop()
                        }
                    },
                    enabled = undonePaths.isNotEmpty() || undoneStamps.isNotEmpty(),
                    modifier = Modifier.testTag("redo_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Redo"
                    )
                }

                // Clear Canvas
                IconButton(
                    onClick = {
                        paths.clear()
                        stamps.clear()
                        undonePaths.clear()
                        undoneStamps.clear()
                        onPlaySoundPop()
                    },
                    modifier = Modifier.testTag("clear_canvas_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                // Save Masterpiece Button
                Button(
                    onClick = { showSaveDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("save_drawing_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Save", fontWeight = FontWeight.Bold)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // The Interactive Drawing Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .testTag("coloring_drawing_canvas")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(selectedBrushMode, selectedColor, strokeWidth, showStampPicker, selectedStampType) {
                        if (showStampPicker) {
                            // Tap to place stamp
                            detectTapGestures { offset ->
                                stamps.add(
                                    PlacedStamp(
                                        x = offset.x,
                                        y = offset.y,
                                        stampType = selectedStampType,
                                        size = strokeWidth * 2.2f,
                                        color = selectedColor
                                    )
                                )
                                undoneStamps.clear()
                                onPlaySoundPop()
                            }
                        } else {
                            // Touch drawing drag
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentStrokePoints = listOf(StrokePoint(offset.x, offset.y))
                                    onPlayBrushSound()
                                },
                                onDrag = { change, _ ->
                                    val current = currentStrokePoints ?: emptyList()
                                    currentStrokePoints = current + StrokePoint(change.position.x, change.position.y)
                                },
                                onDragEnd = {
                                    val finishedPoints = currentStrokePoints
                                    if (!finishedPoints.isNullOrEmpty()) {
                                        paths.add(
                                            DrawingPath(
                                                points = finishedPoints,
                                                color = if (selectedBrushMode == BrushMode.ERASER) 0xFFFFFFFF else selectedColor,
                                                strokeWidth = strokeWidth,
                                                brushMode = selectedBrushMode
                                            )
                                        )
                                        undonePaths.clear()
                                    }
                                    currentStrokePoints = null
                                },
                                onDragCancel = {
                                    currentStrokePoints = null
                                }
                            )
                        }
                    }
            ) {
                val canvasW = size.width
                val canvasH = size.height

                // 1. Draw existing user stroke paths
                for (p in paths) {
                    if (p.points.size > 1) {
                        val path = Path().apply {
                            moveTo(p.points.first().x, p.points.first().y)
                            for (i in 1 until p.points.size) {
                                lineTo(p.points[i].x, p.points[i].y)
                            }
                        }

                        when (p.brushMode) {
                            BrushMode.RAINBOW -> {
                                val rainbowBrush = Brush.linearGradient(
                                    listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Magenta),
                                    start = Offset(0f, 0f),
                                    end = Offset(canvasW, canvasH)
                                )
                                drawPath(
                                    path = path,
                                    brush = rainbowBrush,
                                    style = Stroke(width = p.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                            BrushMode.NEON -> {
                                // Outer glow
                                drawPath(
                                    path = path,
                                    color = Color(p.color).copy(alpha = 0.35f),
                                    style = Stroke(width = p.strokeWidth * 1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                                // Inner bright line
                                drawPath(
                                    path = path,
                                    color = Color(p.color),
                                    style = Stroke(width = p.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                            BrushMode.GLITTER -> {
                                drawPath(
                                    path = path,
                                    color = Color(p.color),
                                    style = Stroke(width = p.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                                // Draw cute glitter sparkles along the points
                                for (i in p.points.indices step 6) {
                                    val pt = p.points[i]
                                    drawCircle(
                                        color = Color.White,
                                        radius = (p.strokeWidth * 0.28f).coerceAtLeast(3f),
                                        center = Offset(pt.x + (i % 5 - 2) * 4, pt.y + (i % 7 - 3) * 4)
                                    )
                                }
                            }
                            BrushMode.ERASER -> {
                                drawPath(
                                    path = path,
                                    color = Color.White,
                                    style = Stroke(width = p.strokeWidth * 1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                            else -> {
                                // MARKER & PENCIL
                                drawPath(
                                    path = path,
                                    color = Color(p.color),
                                    style = Stroke(width = p.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                        }
                    }
                }

                // 2. Draw current active stroke
                val activePts = currentStrokePoints
                if (activePts != null && activePts.size > 1) {
                    val activePath = Path().apply {
                        moveTo(activePts.first().x, activePts.first().y)
                        for (i in 1 until activePts.size) {
                            lineTo(activePts[i].x, activePts[i].y)
                        }
                    }
                    val strokeColor = if (selectedBrushMode == BrushMode.ERASER) Color.White else Color(selectedColor)
                    drawPath(
                        path = activePath,
                        color = strokeColor,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                // 3. Draw placed stamps
                for (st in stamps) {
                    drawCircle(
                        color = Color(st.color).copy(alpha = 0.25f),
                        radius = st.size * 0.8f,
                        center = Offset(st.x, st.y)
                    )
                    // Draw stamp emoji representation
                    // We render a neat circle or star shape
                    drawCircle(
                        color = Color(st.color),
                        radius = st.size * 0.45f,
                        center = Offset(st.x, st.y)
                    )
                }

                // 4. Draw template outline on top so lines stay crisp and visible!
                if (templateId != "free_draw") {
                    TemplateRegistry.drawTemplateOutline(this, templateId, Color(0xFF2D3436))
                }
            }

            // Confetti overlay on save
            if (showCelebration) {
                ConfettiEffect(
                    onFinished = { showCelebration = false }
                )
            }
        }

        // Bottom Controls Container
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                // Color Swatches Row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    items(DrawingColors) { color ->
                        val colorHex = color.toArgb().toLong() and 0xFFFFFFFF
                        val isSelected = selectedColor == colorHex && selectedBrushMode != BrushMode.ERASER

                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 46.dp else 40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.5.dp else 1.5.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.LightGray,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedColor = colorHex
                                    if (selectedBrushMode == BrushMode.ERASER) {
                                        selectedBrushMode = BrushMode.MARKER
                                    }
                                    showStampPicker = false
                                    onPlaySoundPop()
                                }
                                .testTag("color_swatch_${color.toArgb()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = if (color == Color.White) Color.Black else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Tool selection & Brush size Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Brush mode options
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ToolIconButton(
                            label = "Marker",
                            icon = "🖌️",
                            selected = selectedBrushMode == BrushMode.MARKER && !showStampPicker,
                            onClick = {
                                selectedBrushMode = BrushMode.MARKER
                                showStampPicker = false
                                onPlaySoundPop()
                            }
                        )

                        ToolIconButton(
                            label = "Neon",
                            icon = "✨",
                            selected = selectedBrushMode == BrushMode.NEON && !showStampPicker,
                            onClick = {
                                selectedBrushMode = BrushMode.NEON
                                showStampPicker = false
                                onPlaySoundPop()
                            }
                        )

                        ToolIconButton(
                            label = "Rainbow",
                            icon = "🌈",
                            selected = selectedBrushMode == BrushMode.RAINBOW && !showStampPicker,
                            onClick = {
                                selectedBrushMode = BrushMode.RAINBOW
                                showStampPicker = false
                                onPlaySoundPop()
                            }
                        )

                        ToolIconButton(
                            label = "Glitter",
                            icon = "🌟",
                            selected = selectedBrushMode == BrushMode.GLITTER && !showStampPicker,
                            onClick = {
                                selectedBrushMode = BrushMode.GLITTER
                                showStampPicker = false
                                onPlaySoundPop()
                            }
                        )

                        ToolIconButton(
                            label = "Stamps",
                            icon = selectedStampType.symbol,
                            selected = showStampPicker,
                            onClick = {
                                showStampPicker = !showStampPicker
                                onPlaySoundPop()
                            }
                        )

                        ToolIconButton(
                            label = "Eraser",
                            icon = "🧽",
                            selected = selectedBrushMode == BrushMode.ERASER && !showStampPicker,
                            onClick = {
                                selectedBrushMode = BrushMode.ERASER
                                showStampPicker = false
                                onPlaySoundPop()
                            }
                        )
                    }

                    // Stroke Width selector (Thin / Med / Thick)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(12f to "S", 24f to "M", 42f to "L").forEach { (width, label) ->
                            val isChosen = strokeWidth == width
                            Surface(
                                shape = CircleShape,
                                color = if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        strokeWidth = width
                                        onPlaySoundPop()
                                    }
                                    .testTag("stroke_width_$label"),
                                shadowElevation = if (isChosen) 2.dp else 0.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isChosen) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Stamp picker row if stamps enabled
                AnimatedVisibility(visible = showStampPicker) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(StampType.entries) { stamp ->
                            val isSel = selectedStampType == stamp
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        selectedStampType = stamp
                                        onPlaySoundPop()
                                    }
                                    .testTag("stamp_${stamp.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = stamp.symbol, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = stamp.label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Save Artwork Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(
                    text = "Save Your Masterpiece! 🎨",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Give your artwork a special name so you can find it anytime in your gallery:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = drawingTitle,
                        onValueChange = { drawingTitle = it },
                        label = { Text("Drawing Title") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_drawing_title_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveDialog = false
                        showCelebration = true
                        onSaveDrawing(drawingTitle)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_save_drawing_button")
                ) {
                    Text("Save to Gallery")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSaveDialog = false },
                    modifier = Modifier.testTag("cancel_save_drawing_button")
                ) {
                    Text("Keep Drawing")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun ToolIconButton(
    label: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("tool_button_$label"),
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
