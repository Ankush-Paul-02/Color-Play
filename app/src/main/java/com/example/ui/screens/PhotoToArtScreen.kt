package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.LineArtStyle
import com.example.util.PhotoLineArtConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoToArtScreen(
    onStartColoring: (lineArtBitmap: Bitmap) -> Unit,
    onBack: () -> Unit,
    onPlayPopSound: () -> Unit = {},
    onPlaySuccessSound: () -> Unit = {}
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var inputPhoto by remember { mutableStateOf<Bitmap?>(null) }
    var lineArtResult by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    // Tuning controls
    var detailLevel by remember { mutableFloatStateOf(0.55f) }
    var selectedStyle by remember { mutableStateOf(LineArtStyle.BALANCED) }
    var lineThickness by remember { mutableIntStateOf(2) }

    // Preview tab: 0 = Line Art, 1 = Original Photo
    var selectedPreviewTab by remember { mutableIntStateOf(0) }

    // Recompute line art when photo or tuning settings change
    LaunchedEffect(inputPhoto, detailLevel, selectedStyle, lineThickness) {
        val src = inputPhoto ?: return@LaunchedEffect
        isProcessing = true
        withContext(Dispatchers.Default) {
            val converted = PhotoLineArtConverter.convertToLineArt(
                source = src,
                detailLevel = detailLevel,
                style = selectedStyle,
                thickness = lineThickness
            )
            lineArtResult = converted
            isProcessing = false
        }
    }

    // Camera Launcher using TakePicturePreview
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { capturedBitmap ->
        if (capturedBitmap != null) {
            inputPhoto = capturedBitmap
            onPlayPopSound()
        }
    }

    // Camera Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        }
    }

    // Zero-permission Android Photo Picker for picking from gallery
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val loadedBitmap = loadBitmapFromUri(context, uri)
                if (loadedBitmap != null) {
                    inputPhoto = loadedBitmap
                    onPlayPopSound()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("photo_to_art_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Magic Photo to Line Art 📸",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Turn your pictures into coloring pages!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("photo_to_art_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Source Selection Cards: Camera & Gallery & Samples
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Take Photo Button
                    ElevatedCard(
                        onClick = {
                            val hasCamera = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasCamera) {
                                cameraLauncher.launch()
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("photo_take_camera_button")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Camera",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Take Photo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Use Camera 📸",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Pick From Gallery Button
                    ElevatedCard(
                        onClick = {
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("photo_pick_gallery_button")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Collections,
                                        contentDescription = "Gallery",
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Choose Picture",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "From Photos 🖼️",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Quick Samples Row (Immediate try for kids!)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Or tap a sample to try right now:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SampleQuickButton(
                                label = "Playful Pup",
                                emoji = "🐶",
                                modifier = Modifier.weight(1f),
                                testTag = "sample_pup_button"
                            ) {
                                inputPhoto = PhotoLineArtConverter.createSamplePhoto("puppy")
                                onPlayPopSound()
                            }
                            SampleQuickButton(
                                label = "Teddy Bear",
                                emoji = "🧸",
                                modifier = Modifier.weight(1f),
                                testTag = "sample_teddy_button"
                            ) {
                                inputPhoto = PhotoLineArtConverter.createSamplePhoto("teddy")
                                onPlayPopSound()
                            }
                            SampleQuickButton(
                                label = "Flower Garden",
                                emoji = "🌸",
                                modifier = Modifier.weight(1f),
                                testTag = "sample_flower_button"
                            ) {
                                inputPhoto = PhotoLineArtConverter.createSamplePhoto("flower")
                                onPlayPopSound()
                            }
                        }
                    }
                }
            }

            // Main Preview Area
            item {
                if (inputPhoto == null) {
                    // Empty State Card with Cute Illustration
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🎨✨", fontSize = 54.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Take or choose a picture!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Snap your pet, your favorite toy, a car, or even a drawing you made on paper. Our magic converter will trace the outlines into a clean coloring book page!",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // Active Photo Preview & Line Art Display
                    ElevatedCard(
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Tab switcher between Line Art and Original
                            TabRow(selectedTabIndex = selectedPreviewTab) {
                                Tab(
                                    selected = selectedPreviewTab == 0,
                                    onClick = { selectedPreviewTab = 0 },
                                    text = { Text("Coloring Lines 🖍️", fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = selectedPreviewTab == 1,
                                    onClick = { selectedPreviewTab = 1 },
                                    text = { Text("Original Photo 📷", fontWeight = FontWeight.Bold) }
                                )
                            }

                            // Image display
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.1f)
                                    .background(Color.White)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedPreviewTab == 0) {
                                    val lineBmp = lineArtResult
                                    if (lineBmp != null) {
                                        Image(
                                            bitmap = lineBmp.asImageBitmap(),
                                            contentDescription = "Converted Line Drawing",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .border(
                                                    width = 2.dp,
                                                    color = Color(0xFFDFE4EA),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                } else {
                                    val orig = inputPhoto
                                    if (orig != null) {
                                        Image(
                                            bitmap = orig.asImageBitmap(),
                                            contentDescription = "Original Photo",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }

                                if (isProcessing) {
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color.Black.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "🪄 Tracing lines...", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tuning Controls (Only when photo is loaded)
            if (inputPhoto != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Line Style & Sensitivity",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Style Preset Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (style in LineArtStyle.entries) {
                                    val isSelected = selectedStyle == style
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedStyle = style
                                            onPlayPopSound()
                                        },
                                        label = {
                                            Text("${style.emoji} ${style.displayName}", fontSize = 12.sp)
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        modifier = Modifier.testTag("style_chip_${style.name.lowercase()}")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Detail Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Line Detail:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = if (detailLevel > 0.65f) "High Detail" else if (detailLevel < 0.4f) "Simple Shapes" else "Standard",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = detailLevel,
                                onValueChange = { detailLevel = it },
                                valueRange = 0.15f..0.85f,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("detail_slider")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Thickness Selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Outline Thickness:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ThicknessChip(label = "Fine", selected = lineThickness == 1) {
                                        lineThickness = 1
                                        onPlayPopSound()
                                    }
                                    ThicknessChip(label = "Medium", selected = lineThickness == 2) {
                                        lineThickness = 2
                                        onPlayPopSound()
                                    }
                                    ThicknessChip(label = "Bold", selected = lineThickness == 3) {
                                        lineThickness = 3
                                        onPlayPopSound()
                                    }
                                }
                            }
                        }
                    }
                }

                // Big Action Button: Start Coloring!
                item {
                    val lineBmp = lineArtResult
                    Button(
                        onClick = {
                            if (lineBmp != null) {
                                onPlaySuccessSound()
                                onStartColoring(lineBmp)
                            }
                        },
                        enabled = lineBmp != null && !isProcessing,
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2ED573),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .testTag("start_coloring_photo_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🎨", fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Start Coloring This Picture!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SampleQuickButton(
    label: String,
    emoji: String,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ThicknessChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

private suspend fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        } catch (_: Exception) {
            null
        }
    }
}
