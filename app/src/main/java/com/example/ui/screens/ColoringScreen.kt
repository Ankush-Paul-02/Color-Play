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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
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
import com.example.util.PhotoLineArtConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringScreen(
    templateId: String,
    initialCanvasData: DrawingCanvasData,
    drawingId: Long?,
    onSaveDrawing: (title: String) -> Unit,
    onBack: () -> Unit,
    onCanvasUpdated: (DrawingCanvasData) -> Unit = {},
    onPlaySoundPop: () -> Unit = {},
    onPlayBrushSound: () -> Unit = {}
) {
    BackHandler { onBack() }

    val template = remember(templateId) { TemplateRegistry.getById(templateId) }

    // Custom Photo Line Art Overlay if this drawing was generated from a photo
    val photoLineArtBitmap = remember(initialCanvasData.photoLineArtBase64) {
        initialCanvasData.photoLineArtBase64?.let { base64 ->
            PhotoLineArtConverter.base64ToBitmap(base64)?.let { bmp ->
                PhotoLineArtConverter.createTransparentLineOverlay(bmp).asImageBitmap()
            }
        }
    }

    // Active strokes and undo/redo stacks keyed on templateId and drawingId so different drawings don't bleed into each other
    val paths = remember(templateId, drawingId) {
        mutableStateListOf<DrawingPath>().apply { addAll(initialCanvasData.paths) }
    }
    val undonePaths = remember(templateId, drawingId) { mutableStateListOf<DrawingPath>() }

    val stamps = remember(templateId, drawingId) {
        mutableStateListOf<PlacedStamp>().apply { addAll(initialCanvasData.stamps) }
    }
    val undoneStamps = remember(templateId, drawingId) { mutableStateListOf<PlacedStamp>() }

    // Tool state
    var selectedColor by remember { mutableLongStateOf(0xFFFF3838) }
    var selectedBrushMode by remember { mutableStateOf(BrushMode.MARKER) }
    var strokeWidth by remember { mutableFloatStateOf(24f) }
    var isEraserActive by remember { mutableStateOf(false) }
    var eraserWidth by remember { mutableFloatStateOf(44f) }

    var selectedStampType by remember { mutableStateOf(StampType.STAR) }
    var showStampPicker by remember { mutableStateOf(false) }

    // Save Dialog State
    var showSaveDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var drawingTitle by remember(templateId, drawingId) { mutableStateOf(template.defaultTitle) }
    var showCelebration by remember { mutableStateOf(false) }

    // Current in-progress stroke with accurate coordinate tracking
    var currentStrokePoints by remember { mutableStateOf<List<StrokePoint>?>(null) }

    fun notifyUpdate() {
        onCanvasUpdated(DrawingCanvasData(paths.toList(), stamps.toList(), templateId, initialCanvasData.photoLineArtBase64))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = template.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = template.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (isEraserActive) {
                            Text(
                                text = "🧽 Eraser Mode Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFF5252),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
                            notifyUpdate()
                            onPlaySoundPop()
                        } else if (stamps.isNotEmpty()) {
                            val removed = stamps.removeAt(stamps.lastIndex)
                            undoneStamps.add(removed)
                            notifyUpdate()
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
                            notifyUpdate()
                            onPlaySoundPop()
                        } else if (undoneStamps.isNotEmpty()) {
                            val restored = undoneStamps.removeAt(undoneStamps.lastIndex)
                            stamps.add(restored)
                            notifyUpdate()
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

                // Clear All Button
                IconButton(
                    onClick = { showClearConfirmDialog = true },
                    modifier = Modifier.testTag("clear_canvas_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear All",
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

        // The Interactive Drawing Canvas with Accurately Tracked Touch Gestures
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(2.5.dp, if (isEraserActive) Color(0xFFFF5252).copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .testTag("coloring_drawing_canvas")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isEraserActive, selectedBrushMode, selectedColor, strokeWidth, eraserWidth, showStampPicker, selectedStampType) {
                        if (showStampPicker && !isEraserActive) {
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
                                notifyUpdate()
                                onPlaySoundPop()
                            }
                        } else {
                            // Touch drawing and eraser drag with precision tracking
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val startPoint = StrokePoint(offset.x, offset.y)
                                    currentStrokePoints = listOf(startPoint)

                                    if (isEraserActive) {
                                        // If tap on a stamp, erase the stamp immediately!
                                        val radiusSq = (eraserWidth * 1.5f) * (eraserWidth * 1.5f)
                                        val idx = stamps.indexOfLast { st ->
                                            val dx = st.x - offset.x
                                            val dy = st.y - offset.y
                                            (dx * dx + dy * dy) <= radiusSq
                                        }
                                        if (idx >= 0) {
                                            stamps.removeAt(idx)
                                            notifyUpdate()
                                        }
                                        onPlaySoundPop()
                                    } else {
                                        onPlayBrushSound()
                                    }
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    val current = currentStrokePoints ?: emptyList()
                                    val newPt = StrokePoint(change.position.x, change.position.y)
                                    currentStrokePoints = current + newPt

                                    if (isEraserActive) {
                                        // Erase any stamp that touches eraser path
                                        val radiusSq = (eraserWidth * 1.4f) * (eraserWidth * 1.4f)
                                        val idx = stamps.indexOfLast { st ->
                                            val dx = st.x - change.position.x
                                            val dy = st.y - change.position.y
                                            (dx * dx + dy * dy) <= radiusSq
                                        }
                                        if (idx >= 0) {
                                            stamps.removeAt(idx)
                                            notifyUpdate()
                                            onPlaySoundPop()
                                        }
                                    }
                                },
                                onDragEnd = {
                                    val finishedPoints = currentStrokePoints
                                    if (!finishedPoints.isNullOrEmpty()) {
                                        val effectiveWidth = if (isEraserActive) eraserWidth else strokeWidth
                                        val effectiveColor = if (isEraserActive) 0xFFFFFFFF else selectedColor
                                        val effectiveMode = if (isEraserActive) BrushMode.ERASER else selectedBrushMode

                                        paths.add(
                                            DrawingPath(
                                                points = finishedPoints,
                                                color = effectiveColor,
                                                strokeWidth = effectiveWidth,
                                                brushMode = effectiveMode
                                            )
                                        )
                                        undonePaths.clear()
                                        notifyUpdate()
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

                // 1. Draw existing user stroke paths with smooth Bezier curves
                for (p in paths) {
                    if (p.points.size == 1) {
                        // Accurately render single-tap dots!
                        val pt = p.points.first()
                        val col = if (p.brushMode == BrushMode.ERASER) Color.White else Color(p.color)
                        drawCircle(
                            color = col,
                            radius = (p.strokeWidth / 2f).coerceAtLeast(3f),
                            center = Offset(pt.x, pt.y)
                        )
                    } else if (p.points.size > 1) {
                        val path = Path().apply {
                            moveTo(p.points.first().x, p.points.first().y)
                            for (i in 1 until p.points.size) {
                                val prev = p.points[i - 1]
                                val curr = p.points[i]
                                quadraticTo(prev.x, prev.y, (prev.x + curr.x) / 2f, (prev.y + curr.y) / 2f)
                            }
                            lineTo(p.points.last().x, p.points.last().y)
                        }

                        when (p.brushMode) {
                            BrushMode.RAINBOW -> {
                                val rainbowBrush = Brush.linearGradient(
                                    listOf(Color(0xFFFF3838), Color(0xFFFFD32A), Color(0xFF2ED573), Color(0xFF1E90FF), Color(0xFF9B59B6)),
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
                                drawPath(
                                    path = path,
                                    color = Color(p.color).copy(alpha = 0.35f),
                                    style = Stroke(width = p.strokeWidth * 1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
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
                                for (i in p.points.indices step 5) {
                                    val pt = p.points[i]
                                    drawCircle(
                                        color = Color.White,
                                        radius = (p.strokeWidth * 0.28f).coerceAtLeast(3.5f),
                                        center = Offset(pt.x + (i % 5 - 2) * 4, pt.y + (i % 7 - 3) * 4)
                                    )
                                }
                            }
                            BrushMode.ERASER -> {
                                // Smooth white eraser wipe
                                drawPath(
                                    path = path,
                                    color = Color.White,
                                    style = Stroke(width = p.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                            else -> {
                                drawPath(
                                    path = path,
                                    color = Color(p.color),
                                    style = Stroke(width = p.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                        }
                    }
                }

                // 2. Draw current in-progress live stroke & live eraser cursor
                val activePts = currentStrokePoints
                if (!activePts.isNullOrEmpty()) {
                    val activeWidth = if (isEraserActive) eraserWidth else strokeWidth
                    val activeColor = if (isEraserActive) Color.White else Color(selectedColor)

                    if (activePts.size == 1) {
                        drawCircle(
                            color = activeColor,
                            radius = (activeWidth / 2f).coerceAtLeast(3f),
                            center = Offset(activePts.first().x, activePts.first().y)
                        )
                    } else {
                        val activePath = Path().apply {
                            moveTo(activePts.first().x, activePts.first().y)
                            for (i in 1 until activePts.size) {
                                val prev = activePts[i - 1]
                                val curr = activePts[i]
                                quadraticTo(prev.x, prev.y, (prev.x + curr.x) / 2f, (prev.y + curr.y) / 2f)
                            }
                            lineTo(activePts.last().x, activePts.last().y)
                        }

                        drawPath(
                            path = activePath,
                            color = activeColor,
                            style = Stroke(width = activeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }

                    // Live tactile cursor under finger:
                    val lastPt = activePts.last()
                    if (isEraserActive) {
                        // Prominent pink/coral ring showing exact eraser wipe zone
                        drawCircle(
                            color = Color(0xFFFF5252).copy(alpha = 0.5f),
                            radius = activeWidth / 2f,
                            center = Offset(lastPt.x, lastPt.y),
                            style = Stroke(width = 3f)
                        )
                        drawCircle(
                            color = Color(0xFFFF5252).copy(alpha = 0.15f),
                            radius = activeWidth / 2f,
                            center = Offset(lastPt.x, lastPt.y)
                        )
                    }
                }

                // 3. Draw placed stickers & stamps
                for (st in stamps) {
                    drawCircle(
                        color = Color(st.color).copy(alpha = 0.25f),
                        radius = st.size * 0.8f,
                        center = Offset(st.x, st.y)
                    )
                    drawCircle(
                        color = Color(st.color),
                        radius = st.size * 0.45f,
                        center = Offset(st.x, st.y)
                    )
                }

                // 4. Draw template outline on top so boundaries remain crisp and clear!
                if (photoLineArtBitmap != null) {
                    drawImage(
                        image = photoLineArtBitmap,
                        dstSize = IntSize(size.width.toInt(), size.height.toInt())
                    )
                } else if (templateId != "free_draw" && templateId != "photo_art") {
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

        // Bottom Controls Container with Prominent Eraser & Color Picker
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                // Row 1: Active Mode Header & Contextual Controls
                if (isEraserActive) {
                    // Eraser Active Banner with Eraser Sizes
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFF5252).copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🧽", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Eraser Active",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFFFF5252)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Wipe to erase paint & stickers",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Eraser Sizes: S / M / L
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(24f to "S", 44f to "M", 74f to "L").forEach { (size, label) ->
                                val isSel = eraserWidth == size
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSel) Color(0xFFFF5252) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            eraserWidth = size
                                            onPlaySoundPop()
                                        }
                                        .testTag("eraser_size_$label"),
                                    shadowElevation = if (isSel) 3.dp else 0.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Regular Color Swatches Row
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        items(DrawingColors) { color ->
                            val colorHex = color.toArgb().toLong() and 0xFFFFFFFF
                            val isSelected = selectedColor == colorHex && !isEraserActive

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
                                        isEraserActive = false
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
                }

                // Row 2: Dedicated Eraser Button, Brush Tools & Sizes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Group: Tools + Dedicated Prominent ERASER
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. DEDICATED PROMINENT ERASER BUTTON
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isEraserActive) Color(0xFFFF5252) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    isEraserActive = !isEraserActive
                                    showStampPicker = false
                                    onPlaySoundPop()
                                }
                                .testTag("tool_button_Eraser"),
                            shadowElevation = if (isEraserActive) 4.dp else 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🧽", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Eraser",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isEraserActive) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // 2. Brush mode chips
                        ToolIconButton(
                            label = "Marker",
                            icon = "🖌️",
                            selected = !isEraserActive && selectedBrushMode == BrushMode.MARKER && !showStampPicker,
                            onClick = {
                                isEraserActive = false
                                selectedBrushMode = BrushMode.MARKER
                                showStampPicker = false
                                onPlaySoundPop()
                            }
                        )

                        ToolIconButton(
                            label = "Neon",
                            icon = "✨",
                            selected = !isEraserActive && selectedBrushMode == BrushMode.NEON && !showStampPicker,
                            onClick = {
                                isEraserActive = false
                                selectedBrushMode = BrushMode.NEON
                                showStampPicker = false
                                onPlaySoundPop()
                            }
                        )

                        ToolIconButton(
                            label = "Rainbow",
                            icon = "🌈",
                            selected = !isEraserActive && selectedBrushMode == BrushMode.RAINBOW && !showStampPicker,
                            onClick = {
                                isEraserActive = false
                                selectedBrushMode = BrushMode.RAINBOW
                                showStampPicker = false
                                onPlaySoundPop()
                            }
                        )

                        ToolIconButton(
                            label = "Stamps",
                            icon = selectedStampType.symbol,
                            selected = !isEraserActive && showStampPicker,
                            onClick = {
                                isEraserActive = false
                                showStampPicker = !showStampPicker
                                onPlaySoundPop()
                            }
                        )
                    }

                    // Right Group: Brush size selector (when not eraser)
                    if (!isEraserActive) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(12f to "S", 24f to "M", 44f to "L").forEach { (width, label) ->
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
                }

                // Row 3: Stamp Picker if opened
                AnimatedVisibility(visible = showStampPicker && !isEraserActive) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
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
                        text = "Give your artwork a special name to find it in your offline gallery:",
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

    // Clear Canvas Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear Canvas? 🧹") },
            text = { Text("Would you like to erase all paint and stickers to start a fresh drawing?") },
            confirmButton = {
                Button(
                    onClick = {
                        paths.clear()
                        stamps.clear()
                        undonePaths.clear()
                        undoneStamps.clear()
                        notifyUpdate()
                        onPlaySoundPop()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_clear_canvas_button")
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmDialog = false },
                    modifier = Modifier.testTag("cancel_clear_canvas_button")
                ) {
                    Text("Keep Canvas")
                }
            },
            shape = RoundedCornerShape(20.dp)
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
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
