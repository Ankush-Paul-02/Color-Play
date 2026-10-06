package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DrawingEntity
import com.example.model.DrawingCanvasData
import com.example.model.TemplateRegistry
import com.example.util.PhotoLineArtConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    drawings: List<DrawingEntity>,
    onOpenDrawing: (templateId: String, drawingId: Long) -> Unit,
    onDeleteDrawing: (Long) -> Unit,
    onToggleFavorite: (DrawingEntity) -> Unit,
    onStartNewDrawing: () -> Unit,
    onOpenPhotoToArt: () -> Unit = {},
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var showOnlyFavorites by remember { mutableStateOf(false) }
    var drawingToDelete by remember { mutableStateOf<DrawingEntity?>(null) }

    val displayedDrawings = if (showOnlyFavorites) {
        drawings.filter { it.isFavorite }
    } else {
        drawings
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "My Masterpieces 🖼️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("gallery_back_button")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                FilledTonalButton(
                    onClick = onOpenPhotoToArt,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .testTag("gallery_photo_to_art_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Photo to Art",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Photo 📸", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = onStartNewDrawing,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("gallery_new_drawing_button")
                ) {
                    Text("+ New Art", fontWeight = FontWeight.Bold)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Filters row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = !showOnlyFavorites,
                onClick = { showOnlyFavorites = false },
                label = { Text("All Art (${drawings.size})") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("filter_all_art")
            )

            FilterChip(
                selected = showOnlyFavorites,
                onClick = { showOnlyFavorites = true },
                label = { Text("Favorites (${drawings.count { it.isFavorite }})") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("filter_favorites")
            )
        }

        if (displayedDrawings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🎨", fontSize = 42.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (showOnlyFavorites) "No favorite art yet!" else "Your gallery is waiting!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Draw, color, and tap Save to keep your art safe offline forever.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onStartNewDrawing,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Create a Drawing Now!")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayedDrawings, key = { it.id }) { drawing ->
                    DrawingCardItem(
                        drawing = drawing,
                        onOpen = { onOpenDrawing(drawing.templateId, drawing.id) },
                        onToggleFavorite = { onToggleFavorite(drawing) },
                        onDelete = { drawingToDelete = drawing }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    drawingToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { drawingToDelete = null },
            title = { Text("Delete Artwork?") },
            text = { Text("Are you sure you want to remove '${item.title}' from your gallery?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteDrawing(item.id)
                        drawingToDelete = null
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { drawingToDelete = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun DrawingCardItem(
    drawing: DrawingEntity,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    val template = remember(drawing.templateId) { TemplateRegistry.getById(drawing.templateId) }
    val dateStr = remember(drawing.createdAt) {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(drawing.createdAt))
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("gallery_drawing_card_${drawing.id}"),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                val canvasData = remember(drawing.strokesJson) {
                    DrawingCanvasData.fromJson(drawing.strokesJson)
                }
                val photoBmp = remember(canvasData.photoLineArtBase64) {
                    canvasData.photoLineArtBase64?.let { PhotoLineArtConverter.base64ToBitmap(it) }
                }

                if (photoBmp != null) {
                    Image(
                        bitmap = photoBmp.asImageBitmap(),
                        contentDescription = drawing.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(text = template.emoji, fontSize = 54.sp)
                }

                // Favorite button top right
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(36.dp)
                        .testTag("fav_button_${drawing.id}")
                ) {
                    Icon(
                        imageVector = if (drawing.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (drawing.isFavorite) Color(0xFFFF5252) else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = drawing.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1
            )

            Text(
                text = dateStr,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = template.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row {
                    IconButton(
                        onClick = onOpen,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
