/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2026
 *  *  * Features.kt is part of NeroX
 *  *  * Liquid Glass & Animated Interactive M3 Feature Grid
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_home.feature

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.KSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Features(
    homeItems: List<HomeFeature> = emptyList(),
    onValueUpdate: (Int) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val featureSize = (LocalConfiguration.current.screenWidthDp.dp / 2)
    val isDark = MaterialTheme.colorScheme.surface.let {
        it.red * 0.299f + it.green * 0.587f + it.blue * 0.114f < 0.5f
    }

    FlowRow(
        mainAxisSize = SizeMode.Expand,
        mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween
    ) {
        for (i in homeItems.indices) {
            val item = homeItems[i]
            val cardScale by animateFloatAsState(
                targetValue = if (item.isChecked) 1.02f else 1.0f,
                animationSpec = spring(
                    dampingRatio = 0.70f,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "feature_card_scale"
            )

            val animatedCardColor by animateColorAsState(
                targetValue = when {
                    item.isChecked -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDark) 0.55f else 0.85f)
                    isDark -> Color(0xFF1E2129).copy(alpha = 0.78f)
                    else -> Color(0xFFFFFFFF).copy(alpha = 0.85f)
                },
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "feature_card_color"
            )

            val borderAlpha by animateFloatAsState(
                targetValue = if (item.isChecked) 0.65f else 0.25f,
                label = "feature_border_alpha"
            )

            val content = @Composable {
                Box(
                    modifier = Modifier
                        .size(featureSize)
                        .padding(8.dp)
                        .aspectRatio(1f)
                        .scale(cardScale)
                        .shadow(
                            elevation = if (item.isChecked) 8.dp else 4.dp,
                            shape = item.shape,
                            ambientColor = if (item.isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.15f),
                            spotColor = if (item.isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f) else Color.Black.copy(alpha = 0.20f)
                        )
                        .clip(item.shape)
                        .background(animatedCardColor)
                        .border(
                            1.dp,
                            if (item.isChecked) MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha)
                            else if (isDark) Color.White.copy(alpha = borderAlpha * 0.4f)
                            else Color(0xFFE2E8F0).copy(alpha = borderAlpha),
                            item.shape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            item.route?.let { item.onClick(it) }
                        }
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp, 16.dp, 12.dp, 14.dp)
                    ) {
                        // Icon with gentle rounded badge
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (item.isChecked) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                tint = if (item.isChecked) Color.White else MaterialTheme.colorScheme.primary,
                                painter = painterResource(id = item.icon),
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = if (item.isChecked) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (item.showSwitch) {
                                Text(
                                    text = if (item.isChecked) stringResource(id = R.string.android_on)
                                    else stringResource(id = R.string.android_off),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (item.isChecked) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                                KSwitch(
                                    checked = item.isChecked,
                                    modifier = Modifier.rotate(-90f),
                                    onClick = {
                                        item.onCheckedChange(!item.isChecked)
                                        onValueUpdate(i)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (item.tooltipText.isNotBlank()) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    state = rememberTooltipState(),
                    tooltip = {
                        RichTooltip(
                            title = {
                                Text(
                                    item.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            text = {
                                Text(item.tooltipText)
                            },
                            action = {
                                TextButton(
                                    onClick = {
                                        uriHandler.openUri(item.featureDocsLink)
                                    },
                                ) {
                                    Text(text = stringResource(R.string.learn_more))
                                }
                            },
                        )
                    },
                ) {
                    content()
                }
            } else {
                content()
            }
        }
    }
}
