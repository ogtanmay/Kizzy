/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2026
 *  *  * LiquidGlass.kt is part of Kizzy
 *  *  * iOS 27 Liquid Glassmorphism & Instagram Slider Nav Bar
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Liquid Glass styling modifier providing iOS 27 glassmorphism:
 * Frosted translucent surfaces, prismatic specular borders, ambient illumination.
 */
@Composable
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(26.dp),
    glowColor: Color = MaterialTheme.colorScheme.primary,
    borderAlpha: Float = 0.35f,
    surfaceAlpha: Float = 0.12f,
): Modifier {
    val isDark = MaterialTheme.colorScheme.surface.let {
        // Simple luminance heuristic: if surface is dark
        it.red * 0.299f + it.green * 0.587f + it.blue * 0.114f < 0.5f
    }

    val glassBackground = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF2A2E3D).copy(alpha = 0.65f),
                Color(0xFF1E212E).copy(alpha = 0.82f),
                glowColor.copy(alpha = 0.15f)
            ),
            start = Offset(0f, 0f),
            end = Offset(400f, 800f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.85f),
                Color.White.copy(alpha = 0.55f),
                glowColor.copy(alpha = 0.12f)
            ),
            start = Offset(0f, 0f),
            end = Offset(400f, 800f)
        )
    }

    val specularBorder = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = if (isDark) borderAlpha else borderAlpha + 0.25f),
            glowColor.copy(alpha = borderAlpha * 0.8f),
            Color.White.copy(alpha = 0.05f),
            glowColor.copy(alpha = borderAlpha * 0.5f)
        ),
        start = Offset(0f, 0f),
        end = Offset(300f, 600f)
    )

    return this
        .shadow(
            elevation = 12.dp,
            shape = shape,
            ambientColor = glowColor.copy(alpha = 0.35f),
            spotColor = glowColor.copy(alpha = 0.45f)
        )
        .clip(shape)
        .background(glassBackground)
        .border(1.dp, specularBorder, shape)
}

/**
 * Liquid Glass Card with specular highlight reflection.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    glowColor: Color = MaterialTheme.colorScheme.primary,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .liquidGlass(shape = shape, glowColor = glowColor)
            .then(clickModifier)
    ) {
        content()
    }
}

/**
 * Instagram-style navigation item definition.
 */
data class NavSliderItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

/**
 * iOS 27 Liquid Glass Slider Nav Bar with an animated fluid glass sliding pill indicator,
 * chromatic specular highlights, and floating translucent dock.
 */
@Composable
fun IosLiquidGlassSliderNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    items: List<NavSliderItem> = defaultNavItems()
) {
    val isDark = MaterialTheme.colorScheme.surface.let {
        it.red * 0.299f + it.green * 0.587f + it.blue * 0.114f < 0.5f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating iOS 27 Liquid Glass Dock Container
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .liquidGlass(
                    shape = RoundedCornerShape(35.dp),
                    glowColor = MaterialTheme.colorScheme.primary,
                    borderAlpha = if (isDark) 0.45f else 0.65f,
                    surfaceAlpha = if (isDark) 0.22f else 0.35f
                )
                .drawBehind {
                    // Top specular rim light reflection (iOS 27 glass refraction)
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDark) 0.40f else 0.70f),
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.45f
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(35.dp.toPx(), 35.dp.toPx())
                    )
                }
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            val totalWidth = maxWidth
            val itemCount = items.size.coerceAtLeast(1)
            val tabWidth = totalWidth / itemCount

            // Ultra-smooth spring-based liquid sliding pill indicator
            val indicatorOffset by animateDpAsState(
                targetValue = tabWidth * selectedIndex,
                animationSpec = spring(
                    dampingRatio = 0.72f,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "ios27_slider_offset"
            )

            // The Liquid Glass Sliding Capsule Pill
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(tabWidth)
                    .height(58.dp)
                    .padding(horizontal = 3.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(29.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.90f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.78f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(200f, 200f)
                        )
                    )
                    .border(
                        1.2.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.80f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        ),
                        RoundedCornerShape(29.dp)
                    )
            )

            // Navigation Items Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.18f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = 0.65f,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "ios27_icon_scale"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .testTag(item.testTag)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onItemSelected(index)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(iconScale)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Backward compatibility alias for InstagramSliderNavBar -> IosLiquidGlassSliderNavBar
 */
@Composable
fun InstagramSliderNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    items: List<NavSliderItem> = defaultNavItems()
) {
    IosLiquidGlassSliderNavBar(
        selectedIndex = selectedIndex,
        onItemSelected = onItemSelected,
        modifier = modifier,
        items = items
    )
}

/**
 * Default Instagram-style navigation items:
 * Home, PC Games, Console, Apps & Media, Profile.
 */
fun defaultNavItems(): List<NavSliderItem> = listOf(
    NavSliderItem("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    NavSliderItem("PC Games", Icons.Filled.Computer, Icons.Outlined.Computer, "nav_pc_games"),
    NavSliderItem("Console", Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports, "nav_console"),
    NavSliderItem("Apps/Media", Icons.Filled.Apps, Icons.Outlined.Apps, "nav_apps_media"),
    NavSliderItem("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile"),
)

/**
 * Liquid Glass Category Slider for platform filtering (like Instagram Stories/Categories).
 */
@Composable
fun LiquidGlassCategorySlider(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(categories) { _, category ->
            val isSelected = category.equals(selectedCategory, ignoreCase = true)
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1.0f,
                animationSpec = spring(dampingRatio = 0.65f),
                label = "cat_scale"
            )

            Box(
                modifier = Modifier
                    .scale(scale)
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (isSelected) {
                            Modifier
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                    )
                                )
                                .border(
                                    1.dp,
                                    Brush.linearGradient(
                                        listOf(Color.White.copy(0.7f), Color.White.copy(0.2f))
                                    ),
                                    RoundedCornerShape(20.dp)
                                )
                        } else {
                            Modifier.liquidGlass(
                                shape = RoundedCornerShape(20.dp),
                                glowColor = MaterialTheme.colorScheme.surfaceVariant,
                                borderAlpha = 0.25f,
                                surfaceAlpha = 0.1f
                            )
                        }
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
