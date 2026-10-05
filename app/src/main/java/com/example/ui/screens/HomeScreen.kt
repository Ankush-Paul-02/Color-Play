package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DrawingEntity
import com.example.model.GameType
import com.example.model.TemplateRegistry
import com.example.viewmodel.Screen

@Composable
fun HomeScreen(
    drawings: List<DrawingEntity>,
    totalStars: Int,
    onStartColoring: (templateId: String, drawingId: Long?) -> Unit,
    onOpenGamesHub: () -> Unit,
    onOpenGame: (Screen) -> Unit,
    onOpenGallery: () -> Unit,
    onOpenTrophies: () -> Unit,
    onParentalControlsClick: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 720.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_screen_scroll"),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Hero Card
            item {
                HeroWelcomeBanner(
                    totalStars = totalStars,
                    onFreeDraw = { onStartColoring("free_draw", null) },
                    onPlayGames = onOpenGamesHub
                )
            }

            // Coloring Templates Section
            item {
                SectionHeader(
                    title = "🎨 Choose a Coloring Page",
                    subtitle = "Pick your favorite friend to color!",
                    actionLabel = "All Pages",
                    onAction = { onStartColoring("rooster", null) }
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                ) {
                    items(TemplateRegistry.templates) { template ->
                        ColoringTemplateCard(
                            emoji = template.emoji,
                            title = template.name,
                            category = template.category,
                            difficulty = template.difficulty,
                            testTag = "template_card_${template.id}",
                            onClick = { onStartColoring(template.id, null) }
                        )
                    }
                }
            }

            // Educational Games Section (Responsive Grid for tablets)
            item {
                SectionHeader(
                    title = "🎮 Educational Games",
                    subtitle = "Play, count, learn shapes & letters!",
                    actionLabel = "Games Hub",
                    onAction = onOpenGamesHub
                )

                if (isTablet) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                EducationalGameItem(
                                    emoji = "🎈",
                                    title = "Color Pop & Match",
                                    subtitle = "Pop floating colored balloons and learn color names!",
                                    badge = "Colors",
                                    backgroundColor = Color(0xFFFF5252),
                                    testTag = "game_item_color_match",
                                    onClick = { onOpenGame(Screen.ColorMatch) }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                EducationalGameItem(
                                    emoji = "🔢",
                                    title = "Number Paint & Count",
                                    subtitle = "Match numbers 1 to 5 to reveal hidden animals!",
                                    badge = "Counting",
                                    backgroundColor = Color(0xFFFF9F1A),
                                    testTag = "game_item_number_color",
                                    onClick = { onOpenGame(Screen.NumberColor) }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                EducationalGameItem(
                                    emoji = "⭐",
                                    title = "Shape Detective",
                                    subtitle = "Fit circles, stars, squares into funny shapes!",
                                    badge = "Geometry",
                                    backgroundColor = Color(0xFF2ED573),
                                    testTag = "game_item_shape_game",
                                    onClick = { onOpenGame(Screen.ShapeDetective) }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                EducationalGameItem(
                                    emoji = "🔤",
                                    title = "ABC Phonics & Animal Tracing",
                                    subtitle = "Trace alphabet letters and meet animal friends!",
                                    badge = "Alphabet",
                                    backgroundColor = Color(0xFF1E90FF),
                                    testTag = "game_item_alphabet_game",
                                    onClick = { onOpenGame(Screen.AlphabetColor) }
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EducationalGameItem(
                            emoji = "🎈",
                            title = "Color Pop & Match",
                            subtitle = "Pop floating colored balloons and learn color names!",
                            badge = "Colors",
                            backgroundColor = Color(0xFFFF5252),
                            testTag = "game_item_color_match",
                            onClick = { onOpenGame(Screen.ColorMatch) }
                        )

                        EducationalGameItem(
                            emoji = "🔢",
                            title = "Number Paint & Count",
                            subtitle = "Match numbers 1 to 5 to reveal hidden animals!",
                            badge = "Counting",
                            backgroundColor = Color(0xFFFF9F1A),
                            testTag = "game_item_number_color",
                            onClick = { onOpenGame(Screen.NumberColor) }
                        )

                        EducationalGameItem(
                            emoji = "⭐",
                            title = "Shape Detective",
                            subtitle = "Fit circles, stars, squares into funny shapes!",
                            badge = "Geometry",
                            backgroundColor = Color(0xFF2ED573),
                            testTag = "game_item_shape_game",
                            onClick = { onOpenGame(Screen.ShapeDetective) }
                        )

                        EducationalGameItem(
                            emoji = "🔤",
                            title = "ABC Phonics & Animal Tracing",
                            subtitle = "Trace alphabet letters and meet animal friends!",
                            badge = "Alphabet",
                            backgroundColor = Color(0xFF1E90FF),
                            testTag = "game_item_alphabet_game",
                            onClick = { onOpenGame(Screen.AlphabetColor) }
                        )
                    }
                }
            }

        // Recent Saved Masterpieces Section (Offline Gallery)
        item {
            SectionHeader(
                title = "🖼️ My Masterpieces",
                subtitle = "Saved offline on this device",
                actionLabel = "View All (${drawings.size})",
                onAction = onOpenGallery
            )

            if (drawings.isEmpty()) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onStartColoring("rooster", null) }
                        .testTag("empty_gallery_prompt_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("✨", fontSize = 26.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Start Your First Drawing!",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Color animals or paint freely, everything is saved safely offline.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(drawings.take(6)) { drawing ->
                        RecentDrawingCard(
                            drawing = drawing,
                            onClick = { onStartColoring(drawing.templateId, drawing.id) }
                        )
                    }
                }
            }
        }

        // Grown-ups & Parents Quick Card
        item {
            Spacer(modifier = Modifier.height(12.dp))
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onParentalControlsClick() }
                    .testTag("home_parental_controls_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF706FD3).copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF706FD3),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Parental Controls & Screen Time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Set daily limits, view kid's stats & learning report",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    }
}

@Composable
private fun HeroWelcomeBanner(
    totalStars: Int,
    onFreeDraw: () -> Unit,
    onPlayGames: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("hero_welcome_card"),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFFF5252),
                            Color(0xFFFF793F),
                            Color(0xFFFFD32A)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Welcome Little Artist! 🎨",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Let's color, play, and learn together!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }

                    // Total Stars Display
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("⭐", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$totalStars",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color(0xFF2D3436)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onFreeDraw,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("hero_free_draw_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = null,
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Magic Paint",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF5252)
                        )
                    }

                    Button(
                        onClick = onPlayGames,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3436)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("hero_play_games_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = Color(0xFFFFD32A),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Play Games",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = actionLabel,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onAction() }
                .padding(8.dp)
        )
    }
}

@Composable
private fun ColoringTemplateCard(
    emoji: String,
    title: String,
    category: String,
    difficulty: String,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(148.dp)
            .height(186.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(68.dp),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = emoji, fontSize = 38.sp)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = difficulty,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun EducationalGameItem(
    emoji: String,
    title: String,
    subtitle: String,
    badge: String,
    backgroundColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = backgroundColor.copy(alpha = 0.15f),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = emoji, fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = backgroundColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = backgroundColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun RecentDrawingCard(
    drawing: DrawingEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .height(130.dp)
            .clickable { onClick() }
            .testTag("recent_drawing_${drawing.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "🎨", fontSize = 36.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = drawing.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}
