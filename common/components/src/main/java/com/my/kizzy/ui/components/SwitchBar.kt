/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * SwitchBar.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.kizzy.ui.theme.getColorScheme

@Composable
fun SwitchBar(
    title: String,
    isChecked: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val glowColor = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .liquidGlass(
                shape = RoundedCornerShape(26.dp),
                glowColor = glowColor,
                borderAlpha = if (isChecked) 0.5f else 0.3f,
                surfaceAlpha = if (isChecked) 0.25f else 0.12f
            )
            .toggleable(enabled) {
                onClick()
            }
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = if (isChecked) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.SemiBold
                ),
                color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis
            )
            KSwitch(
                checked = isChecked,
                enable = enabled
            ) {
                if (enabled) onClick()
            }
        }
    }
}

@Preview
@Composable
fun PreviewSwitchBar() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black), contentAlignment = Alignment.Center){
        var state by remember { mutableStateOf(false) }
        SwitchBar(title = "SwitchBar", isChecked = state) {
            state = !state
        }
    }
}