/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2026
 *  *  * BadgeSpooferScreen.kt is part of Kizzy
 *  *  * 100% Android UI for revere-group/discord-badge-spoofer
 *  *  * iOS 27 Liquid Glassmorphism & Discord Badge Spoofer
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_profile.ui.spoofer

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.kizzy.feature_profile.spoofer.DiscordBadgeSpooferEngine
import com.my.kizzy.preference.Prefs
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.LiquidGlassCard
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class LogEntry(
    val message: String,
    val isSuccess: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeSpooferScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var token by remember { mutableStateOf(Prefs[Prefs.TOKEN, ""]) }
    var cfClearance by remember { mutableStateOf(Prefs[Prefs.CF_CLEARANCE, ""]) }
    var analyticsToken by remember { mutableStateOf(Prefs[Prefs.ANALYTICS_TOKEN, ""]) }
    var fingerprint by remember { mutableStateOf(Prefs[Prefs.SPOOFER_FINGERPRINT, ""]) }

    var totalGamesClaimed by remember { mutableIntStateOf(Prefs[Prefs.SPOOFER_TOTAL_GAMES, 0]) }
    var totalHoursClaimed by remember { mutableFloatStateOf(Prefs[Prefs.SPOOFER_TOTAL_HOURS, 0f]) }

    var selectedHours by remember { mutableIntStateOf(500) }
    var hoursSliderValue by remember { mutableFloatStateOf(500f) }
    var selectedVarietyCount by remember { mutableIntStateOf(50) }

    var isRunning by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableIntStateOf(0) }
    var totalProgress by remember { mutableIntStateOf(0) }
    var isCheckingAuth by remember { mutableStateOf(false) }

    val logs = remember { mutableStateListOf<LogEntry>() }
    val logListState = rememberLazyListState()
    var activeJob by remember { mutableStateOf<Job?>(null) }

    fun addLog(msg: String, isSuccess: Boolean = true) {
        logs.add(LogEntry(msg, isSuccess))
    }

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            logListState.animateScrollToItem(logs.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Discord Badge Spoofer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = { BackButton { onBackPressed() } },
                actions = {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://badge.revere.no/"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.testTag("open_revere_badge_checker")
                    ) {
                        Icon(
                            Icons.Default.OpenInBrowser,
                            contentDescription = "Check Badges Online",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Stats & Info Card (iOS 27 Liquid Glass)
            item {
                LiquidGlassCard(
                    shape = RoundedCornerShape(22.dp),
                    glowColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Discord Badge Spoofer",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Based on revere-group/discord-badge-spoofer",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Cumulative stats
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$totalGamesClaimed",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Games Claimed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${totalHoursClaimed.toInt()}h",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "Hours Claimed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Auth & Credentials Card
            item {
                LiquidGlassCard(
                    shape = RoundedCornerShape(24.dp),
                    glowColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Credentials & State",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Analytics Token Badge Indicator
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (analyticsToken.isNotBlank()) Color(0xFF10B981).copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (analyticsToken.isNotBlank()) "Analytics Ready" else "Auth Required",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (analyticsToken.isNotBlank()) Color(0xFF10B981) else MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        // Discord Token Field
                        OutlinedTextField(
                            value = token,
                            onValueChange = {
                                token = it
                                Prefs[Prefs.TOKEN] = it
                            },
                            label = { Text("Discord Account Token") },
                            placeholder = { Text("Paste user token (starts with user ID)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // CF Clearance Cookie Field
                        OutlinedTextField(
                            value = cfClearance,
                            onValueChange = {
                                cfClearance = it
                                Prefs[Prefs.CF_CLEARANCE] = it
                            },
                            label = { Text("Cloudflare cf_clearance (Optional)") },
                            placeholder = { Text("Paste cookie from /science devtools if required") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Executable Fingerprint (Optional, matching spoofer.py)
                        OutlinedTextField(
                            value = fingerprint,
                            onValueChange = {
                                fingerprint = it
                                Prefs[Prefs.SPOOFER_FINGERPRINT] = it
                            },
                            label = { Text("Executable Fingerprint (Optional)") },
                            placeholder = { Text("Optional PE signature hash from desktop client") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Refresh / Test Auth Button
                        OutlinedButton(
                            onClick = {
                                if (token.isBlank()) {
                                    Toast.makeText(context, "Please enter your Discord token first", Toast.LENGTH_SHORT).show()
                                    return@OutlinedButton
                                }
                                scope.launch {
                                    isCheckingAuth = true
                                    addLog("Connecting to Discord /users/@me for analytics_token...", true)
                                    val res = DiscordBadgeSpooferEngine.fetchAnalyticsToken(token.trim())
                                    if (res.isSuccess) {
                                        analyticsToken = res.getOrThrow()
                                        addLog("Auth verified! analytics_token: ${analyticsToken.take(12)}...", true)
                                        Toast.makeText(context, "Analytics token refreshed successfully!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val err = res.exceptionOrNull()?.message ?: "Unknown error"
                                        addLog("Auth test failed: $err", false)
                                        Toast.makeText(context, "Auth failed: $err", Toast.LENGTH_LONG).show()
                                    }
                                    isCheckingAuth = false
                                }
                            },
                            enabled = !isCheckingAuth && token.isNotBlank(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isCheckingAuth) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Verifying...")
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Test / Refresh Analytics Token")
                            }
                        }
                    }
                }
            }

            // Feature 1: Claim Playtime (Game Time Badge)
            item {
                LiquidGlassCard(
                    shape = RoundedCornerShape(24.dp),
                    glowColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Claim Playtime (Game Time Badge)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Sends heartbeat telemetry to Discord analytics endpoint with custom hours (supports diamond badge up to 5,000h).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Target Hours: $selectedHours hrs per game",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Slider(
                            value = hoursSliderValue,
                            onValueChange = {
                                hoursSliderValue = it
                                selectedHours = it.toInt()
                            },
                            valueRange = 10f..5000f,
                            steps = 49,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Preset hour buttons
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            val hourPresets = listOf(50, 100, 500, 1000, 2500, 5000)
                            items(hourPresets) { hours ->
                                FilterChip(
                                    selected = selectedHours == hours,
                                    onClick = {
                                        selectedHours = hours
                                        hoursSliderValue = hours.toFloat()
                                    },
                                    label = { Text("${hours}h") }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (token.isBlank()) {
                                    Toast.makeText(context, "Please enter your Discord token", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isRunning = true
                                currentProgress = 0
                                logs.clear()
                                addLog("Starting Playtime Claim: $selectedHours hours per game...", true)

                                activeJob = scope.launch {
                                    val games = DiscordBadgeSpooferEngine.fetchDetectableGames(token.trim()).take(50)
                                    totalProgress = games.size

                                    DiscordBadgeSpooferEngine.spoofGames(
                                        token = token.trim(),
                                        cfClearance = cfClearance.trim(),
                                        fingerprint = fingerprint.trim(),
                                        games = games,
                                        hours = selectedHours.toFloat(),
                                        isPlaytimeClaim = true,
                                        onProgress = { cur, tot, msg, success ->
                                            currentProgress = cur
                                            totalProgress = tot
                                            addLog(msg, success)
                                            totalGamesClaimed = Prefs[Prefs.SPOOFER_TOTAL_GAMES, 0]
                                            totalHoursClaimed = Prefs[Prefs.SPOOFER_TOTAL_HOURS, 0f]
                                        },
                                        isCancelled = { !isRunning }
                                    )
                                    isRunning = false
                                }
                            },
                            enabled = !isRunning && token.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Claim $selectedHours Hours Playtime", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Feature 2: Games Played (Game Variety Badge)
            item {
                LiquidGlassCard(
                    shape = RoundedCornerShape(24.dp),
                    glowColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Games Played (Game Variety Badge)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Marks games as launched and played for 1 minute each to level up the Game Variety badge (5, 10, 25, 50, 100 games).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Preset count chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val countPresets = listOf(10, 25, 50, 75, 100)
                            items(countPresets) { count ->
                                FilterChip(
                                    selected = selectedVarietyCount == count,
                                    onClick = { selectedVarietyCount = count },
                                    label = { Text("$count Games") }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (token.isBlank()) {
                                    Toast.makeText(context, "Please enter your Discord token", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isRunning = true
                                currentProgress = 0
                                logs.clear()
                                addLog("Starting Game Variety Spoofing: $selectedVarietyCount games...", true)

                                activeJob = scope.launch {
                                    val games = DiscordBadgeSpooferEngine.fetchDetectableGames(token.trim()).take(selectedVarietyCount)
                                    totalProgress = games.size

                                    DiscordBadgeSpooferEngine.spoofGames(
                                        token = token.trim(),
                                        cfClearance = cfClearance.trim(),
                                        fingerprint = fingerprint.trim(),
                                        games = games,
                                        hours = 1f,
                                        isPlaytimeClaim = false,
                                        onProgress = { cur, tot, msg, success ->
                                            currentProgress = cur
                                            totalProgress = tot
                                            addLog(msg, success)
                                            totalGamesClaimed = Prefs[Prefs.SPOOFER_TOTAL_GAMES, 0]
                                            totalHoursClaimed = Prefs[Prefs.SPOOFER_TOTAL_HOURS, 0f]
                                        },
                                        isCancelled = { !isRunning }
                                    )
                                    isRunning = false
                                }
                            },
                            enabled = !isRunning && token.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Spoof $selectedVarietyCount Games Played", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Live Execution Console & Progress
            if (isRunning || logs.isNotEmpty()) {
                item {
                    LiquidGlassCard(
                        shape = RoundedCornerShape(22.dp),
                        glowColor = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isRunning) "Posting Science Telemetry Batches..." else "Execution Log",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                if (isRunning) {
                                    IconButton(
                                        onClick = {
                                            isRunning = false
                                            activeJob?.cancel()
                                            addLog("Cancelled by user.", false)
                                        }
                                    ) {
                                        Icon(Icons.Default.Stop, contentDescription = "Stop", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }

                            if (totalProgress > 0) {
                                LinearProgressIndicator(
                                    progress = { if (totalProgress > 0) currentProgress.toFloat() / totalProgress else 0f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                                Text(
                                    text = "$currentProgress / $totalProgress games processed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Terminal Log Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF0F111A))
                                    .border(1.dp, Color(0xFF23283B), RoundedCornerShape(14.dp))
                                    .padding(8.dp)
                            ) {
                                LazyColumn(
                                    state = logListState,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(logs) { entry ->
                                        Text(
                                            text = entry.message,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = if (entry.isSuccess) Color(0xFF34D399) else Color(0xFFF87171),
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Notice & Badge Checker Link
            item {
                LiquidGlassCard(
                    shape = RoundedCornerShape(20.dp),
                    glowColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.HelpOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "How Badge Updates Work",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Discord recalculates game activity badges periodically on backend servers. Badges typically reflect on your profile within 1 to 2 days after sending the science telemetry events.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://badge.revere.no/"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verify Badges at badge.revere.no")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
