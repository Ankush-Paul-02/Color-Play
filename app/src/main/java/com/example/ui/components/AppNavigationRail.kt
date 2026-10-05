package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.Screen

@Composable
fun AppNavigationRail(
    currentScreen: Screen,
    totalStars: Int,
    onNavigate: (Screen) -> Unit,
    onParentalControlsClick: () -> Unit
) {
    NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .testTag("app_navigation_rail"),
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🎨", fontSize = 24.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Star Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFD32A).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "⭐ $totalStars",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3436),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RailItem(
                label = "Home",
                icon = Icons.Default.Home,
                selected = currentScreen is Screen.Home,
                testTag = "rail_home",
                onClick = { onNavigate(Screen.Home) }
            )

            RailItem(
                label = "Coloring",
                icon = Icons.Default.ColorLens,
                selected = currentScreen is Screen.Coloring && currentScreen.templateId != "free_draw",
                testTag = "rail_coloring",
                onClick = { onNavigate(Screen.Coloring("rooster")) }
            )

            RailItem(
                label = "Paint",
                icon = Icons.Default.Brush,
                selected = currentScreen is Screen.Coloring && currentScreen.templateId == "free_draw",
                testTag = "rail_free_paint",
                onClick = { onNavigate(Screen.Coloring("free_draw")) }
            )

            RailItem(
                label = "Games",
                icon = Icons.Default.Casino,
                selected = currentScreen is Screen.GamesHub ||
                        currentScreen is Screen.ColorMatch ||
                        currentScreen is Screen.NumberColor ||
                        currentScreen is Screen.ShapeDetective ||
                        currentScreen is Screen.AlphabetColor,
                testTag = "rail_games",
                onClick = { onNavigate(Screen.GamesHub) }
            )

            RailItem(
                label = "Gallery",
                icon = Icons.Default.Collections,
                selected = currentScreen is Screen.Gallery,
                testTag = "rail_gallery",
                onClick = { onNavigate(Screen.Gallery) }
            )

            RailItem(
                label = "Badges",
                icon = Icons.Default.EmojiEvents,
                selected = currentScreen is Screen.TrophyRoom,
                testTag = "rail_trophies",
                onClick = { onNavigate(Screen.TrophyRoom) }
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))

            RailItem(
                label = "Parents",
                icon = Icons.Default.Lock,
                selected = currentScreen is Screen.ParentalControls,
                testTag = "rail_parental",
                onClick = onParentalControlsClick
            )
        }
    }
}

@Composable
private fun RailItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    NavigationRailItem(
        label = { Text(label, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) },
        icon = { Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(22.dp)) },
        selected = selected,
        onClick = onClick,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.testTag(testTag)
    )
}
