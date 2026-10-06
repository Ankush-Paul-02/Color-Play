package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.Screen

@Composable
fun AppDrawerContent(
    currentScreen: Screen,
    totalStars: Int,
    onNavigate: (Screen) -> Unit,
    onParentalControlsClick: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFF5252),
                                Color(0xFFFF793F),
                                Color(0xFFFFD32A)
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🎨", fontSize = 28.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Color & Play",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Kids Creative Studio",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Star counter pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Stars",
                                tint = Color(0xFFFFB142),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$totalStars Stars Earned",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF2D3436)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Items
            DrawerHeaderLabel(title = "CREATE & COLOR")

            DrawerNavItem(
                label = "Home Playground",
                icon = Icons.Default.Home,
                selected = currentScreen is Screen.Home,
                testTag = "drawer_item_home",
                onClick = {
                    onNavigate(Screen.Home)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                label = "Coloring Pages",
                icon = Icons.Default.ColorLens,
                selected = currentScreen is Screen.Coloring && currentScreen.templateId != "free_draw" && currentScreen.templateId != "photo_art",
                testTag = "drawer_item_coloring",
                onClick = {
                    onNavigate(Screen.Coloring(templateId = "star"))
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                label = "Photo to Line Art 📸",
                icon = Icons.Default.CameraAlt,
                selected = currentScreen is Screen.PhotoToArt,
                testTag = "drawer_item_photo_to_art",
                onClick = {
                    onNavigate(Screen.PhotoToArt)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                label = "Magic Free Paint",
                icon = Icons.Default.Brush,
                selected = currentScreen is Screen.Coloring && currentScreen.templateId == "free_draw",
                testTag = "drawer_item_free_paint",
                onClick = {
                    onNavigate(Screen.Coloring(templateId = "free_draw"))
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                label = "My Masterpieces (Offline)",
                icon = Icons.Default.Collections,
                selected = currentScreen is Screen.Gallery,
                testTag = "drawer_item_gallery",
                onClick = {
                    onNavigate(Screen.Gallery)
                    onCloseDrawer()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            DrawerHeaderLabel(title = "EDUCATIONAL GAMES")

            DrawerNavItem(
                label = "Games Hub",
                icon = Icons.Default.Casino,
                selected = currentScreen is Screen.GamesHub,
                testTag = "drawer_item_games_hub",
                onClick = {
                    onNavigate(Screen.GamesHub)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                label = "Color Match & Pop 🎈",
                icon = Icons.Default.AutoAwesome,
                selected = currentScreen is Screen.ColorMatch,
                testTag = "drawer_item_color_match",
                onClick = {
                    onNavigate(Screen.ColorMatch)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                label = "Number Paint & Count 🔢",
                icon = Icons.Default.School,
                selected = currentScreen is Screen.NumberColor,
                testTag = "drawer_item_number_color",
                onClick = {
                    onNavigate(Screen.NumberColor)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                label = "Shape Detective ⭐",
                icon = Icons.Default.Star,
                selected = currentScreen is Screen.ShapeDetective,
                testTag = "drawer_item_shape_game",
                onClick = {
                    onNavigate(Screen.ShapeDetective)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                label = "ABC Phonics & Animals 🔤",
                icon = Icons.Default.School,
                selected = currentScreen is Screen.AlphabetColor,
                testTag = "drawer_item_alphabet_game",
                onClick = {
                    onNavigate(Screen.AlphabetColor)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                label = "Trophies & Badges 🏆",
                icon = Icons.Default.EmojiEvents,
                selected = currentScreen is Screen.TrophyRoom,
                testTag = "drawer_item_trophies",
                onClick = {
                    onNavigate(Screen.TrophyRoom)
                    onCloseDrawer()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            DrawerHeaderLabel(title = "GROWN-UPS")

            DrawerNavItem(
                label = "Parental Controls 🛡️",
                icon = Icons.Default.Lock,
                selected = currentScreen is Screen.ParentalControls,
                testTag = "drawer_item_parental_controls",
                onClick = {
                    onParentalControlsClick()
                    onCloseDrawer()
                }
            )
        }
    }
}

@Composable
private fun DrawerHeaderLabel(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
private fun DrawerNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 15.sp
            )
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        },
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedContainerColor = Color.Transparent,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
