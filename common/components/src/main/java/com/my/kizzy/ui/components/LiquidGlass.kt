/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2026
 *  *  * LiquidGlass.kt is part of Kizzy
 *  *  * Authentic iOS 27 Liquid Glassmorphism & Interactive Draggable Slider Nav Bar
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Clean iOS 27 Liquid Glass modifier:
 * Elegant frosted physical glass look without harsh dark gradients or neon cyberpunk cliches.
 * Features subtle rim lighting, soft diffuse shadow, and pristine translucent optical depth.
 */
@Composable
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(28.dp),
    glowColor: Color = MaterialTheme.colorScheme.primary,
    borderAlpha: Float = 0.30f,
    surfaceAlpha: Float = 0.70f,
): Modifier {
    val isDark = MaterialTheme.colorScheme.surface.let {
        it.red * 0.299f + it.green * 0.587f + it.blue * 0.114f < 0.5f
    }

    val glassBackground = if (isDark) {
        Color(0xFF1E2129).copy(alpha = 0.82f)
    } else {
        Color(0xFFFFFFFF).copy(alpha = 0.88f)
    }

    val specularBorder = Brush.linearGradient(
        colors = listOf(
            if (isDark) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.90f),
            if (isDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.40f),
            if (isDark) Color(0xFF333842).copy(alpha = 0.40f) else Color(0xFFE2E8F0).copy(alpha = 0.70f)
        ),
        start = Offset(0f, 0f),
        end = Offset(400f, 800f)
    )

    return this
        .shadow(
            elevation = 10.dp,
            shape = shape,
            ambientColor = if (isDark) Color.Black.copy(alpha = 0.40f) else Color(0xFF64748B).copy(alpha = 0.15f),
            spotColor = if (isDark) Color.Black.copy(alpha = 0.50f) else Color(0xFF475569).copy(alpha = 0.20f)
        )
        .clip(shape)
        .background(glassBackground)
        .border(1.dp, specularBorder, shape)
}

/**
 * Liquid Glass Card with clean Apple-style glass aesthetic.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
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
 * Navigation item definition.
 */
data class NavSliderItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

/**
 * iOS 27 Liquid Glass Draggable Slider Nav Bar:
 * Users can either tap ANY tab or directly DRAG / SLIDE the liquid glass capsule pill across the dock.
 * Wherever the user releases their finger, the indicator snaps cleanly with fluid physics spring
 * animation to that target tab, and automatically opens that destination tab ("jispe jake ruke wo tab open").
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
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating iOS 27 Liquid Glass Dock Container
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .liquidGlass(
                    shape = RoundedCornerShape(34.dp),
                    glowColor = MaterialTheme.colorScheme.primary
                )
                .drawBehind {
                    // Top rim specular reflection (refined, soft glass finish)
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                (if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.70f)),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.5f
                        ),
                        cornerRadius = CornerRadius(34.dp.toPx(), 34.dp.toPx())
                    )
                }
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            val totalWidthPx = constraints.maxWidth.toFloat()
            val itemCount = items.size.coerceAtLeast(1)
            val tabWidthPx = totalWidthPx / itemCount
            val tabWidthDp = maxWidth / itemCount

            // Animatable offset in pixels for zero-latency dragging and organic spring snap
            val pillOffsetAnim = remember { Animatable(selectedIndex * tabWidthPx) }
            var isDragging by remember { mutableStateOf(false) }

            // Sync with external selectedIndex updates when not user-dragging
            LaunchedEffect(selectedIndex, tabWidthPx) {
                if (!isDragging && tabWidthPx > 0f) {
                    val targetPx = selectedIndex * tabWidthPx
                    if (pillOffsetAnim.value != targetPx) {
                        pillOffsetAnim.animateTo(
                            targetValue = targetPx,
                            animationSpec = spring(
                                dampingRatio = 0.75f,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    }
                }
            }

            // Real-time hover index calculated while dragging or snapping
            val activeHoverIndex = remember(pillOffsetAnim.value, tabWidthPx) {
                if (tabWidthPx <= 0f) selectedIndex
                else {
                    val center = pillOffsetAnim.value + (tabWidthPx / 2f)
                    (center / tabWidthPx).toInt().coerceIn(0, itemCount - 1)
                }
            }

            // Gesture Detector: Unified Tap + Drag across the entire Dock
            val gestureModifier = Modifier.pointerInput(tabWidthPx, itemCount) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                        // "jispe jake ruke wo wala tab open": snap to the tab nearest to where released
                        val releaseIndex = (pillOffsetAnim.value / tabWidthPx)
                            .roundToInt()
                            .coerceIn(0, itemCount - 1)
                        scope.launch {
                            pillOffsetAnim.animateTo(
                                targetValue = releaseIndex * tabWidthPx,
                                animationSpec = spring(
                                    dampingRatio = 0.72f,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        }
                        onItemSelected(releaseIndex)
                    },
                    onDragCancel = {
                        isDragging = false
                        val snapBackIndex = selectedIndex.coerceIn(0, itemCount - 1)
                        scope.launch {
                            pillOffsetAnim.animateTo(
                                targetValue = snapBackIndex * tabWidthPx,
                                animationSpec = spring(
                                    dampingRatio = 0.75f,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            )
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = (pillOffsetAnim.value + dragAmount.x)
                            .coerceIn(0f, (itemCount - 1) * tabWidthPx)
                        scope.launch {
                            pillOffsetAnim.snapTo(newOffset)
                        }
                    }
                )
            }

            // Outer Gestures Box covering entire Dock
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .then(gestureModifier)
            ) {
                // The Liquid Glass Sliding Capsule Pill
                val pillDragScale by animateFloatAsState(
                    targetValue = if (isDragging) 1.04f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.65f),
                    label = "pill_drag_scale"
                )

                // Clean iOS Pill: Clean solid primary tint with translucent frosted glow (non-techy)
                val pillBackground = if (isDark) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.92f)
                } else {
                    MaterialTheme.colorScheme.primary
                }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(pillOffsetAnim.value.roundToInt(), 0) }
                        .width(tabWidthDp)
                        .height(56.dp)
                        .scale(pillDragScale)
                        .padding(horizontal = 3.dp, vertical = 2.dp)
                        .shadow(
                            elevation = if (isDragging) 10.dp else 4.dp,
                            shape = RoundedCornerShape(26.dp),
                            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                        )
                        .clip(RoundedCornerShape(26.dp))
                        .background(pillBackground)
                        .border(
                            1.dp,
                            Color.White.copy(alpha = if (isDark) 0.35f else 0.45f),
                            RoundedCornerShape(26.dp)
                        )
                )

                // Navigation Items Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        val isHighlighted = if (isDragging) activeHoverIndex == index else selectedIndex == index
                        val iconScale by animateFloatAsState(
                            targetValue = if (isHighlighted) 1.15f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = 0.65f,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "ios27_icon_scale"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag(item.testTag)
                                .pointerInput(index) {
                                    detectTapGestures {
                                        // Tap navigation: animate pill and switch tab
                                        scope.launch {
                                            pillOffsetAnim.animateTo(
                                                targetValue = index * tabWidthPx,
                                                animationSpec = spring(
                                                    dampingRatio = 0.72f,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            )
                                        }
                                        onItemSelected(index)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isHighlighted) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    tint = if (isHighlighted) Color.White
                                           else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                    modifier = Modifier
                                        .size(23.dp)
                                        .scale(iconScale)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isHighlighted) Color.White
                                           else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                )
                            }
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
 * Default navigation items:
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
 * Clean Liquid Glass Category Slider for platform filtering (non-techy, natural frosted look).
 */
@Composable
fun LiquidGlassCategorySlider(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.surface.let {
        it.red * 0.299f + it.green * 0.587f + it.blue * 0.114f < 0.5f
    }

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
                                .background(MaterialTheme.colorScheme.primary)
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = if (isDark) 0.30f else 0.50f),
                                    RoundedCornerShape(20.dp)
                                )
                        } else {
                            Modifier.liquidGlass(
                                shape = RoundedCornerShape(20.dp),
                                glowColor = MaterialTheme.colorScheme.surfaceVariant
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
