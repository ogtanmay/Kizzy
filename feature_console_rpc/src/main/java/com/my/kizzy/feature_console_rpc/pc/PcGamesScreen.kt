/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2026
 *  *  * PcGamesScreen.kt is part of Kizzy
 *  *  * iOS 27 Liquid Glassmorphism & PC Games Discord Rich Presence
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_console_rpc.pc

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.my.kizzy.data.rpc.Constants
import com.my.kizzy.domain.model.Game
import com.my.kizzy.domain.model.rpc.RpcConfig
import com.my.kizzy.feature_rpc_base.services.AppDetectionService
import com.my.kizzy.feature_rpc_base.services.CustomRpcService
import com.my.kizzy.feature_rpc_base.services.ExperimentalRpc
import com.my.kizzy.feature_rpc_base.services.MediaRpcService
import com.my.kizzy.preference.Prefs
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.LiquidGlassCard
import com.my.kizzy.ui.components.LiquidGlassCategorySlider
import com.my.kizzy.ui.components.SearchBar
import com.my.kizzy.ui.components.SwitchBar
import com.my.kizzy.ui.components.liquidGlass
import com.my.kizzy.ui.components.shimmer.AnimatedShimmer
import com.my.kizzy.ui.components.shimmer.ShimmerGamesScreen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PcGamesScreen(
    onBackPressed: () -> Unit,
    onEvent: (PcUiEvent) -> Unit,
    state: PcGamesState,
    serviceEnabled: Boolean,
    isSearchBarVisible: Boolean,
) {
    var selected by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isPcRpcRunning by remember { mutableStateOf(serviceEnabled) }
    var searchText by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val intent = remember { Intent(context, CustomRpcService::class.java) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchBarVisible) {
                        SearchBar(
                            onTextChanged = {
                                searchText = it
                                onEvent(PcUiEvent.Search(it))
                            },
                            text = searchText,
                            placeholder = stringResource(id = R.string.search_placeholder),
                            onClose = {
                                onEvent(PcUiEvent.CloseSearchBar)
                            }
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.main_pcGamesRpc),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    if (!isSearchBarVisible) {
                        IconButton(
                            onClick = { onEvent(PcUiEvent.OpenSearchBar) },
                            modifier = Modifier.testTag("pc_search_button")
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.search)
                            )
                        }
                    }
                },
                navigationIcon = { BackButton { onBackPressed() } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_custom_pc_game_fab")
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_custom_pc_game)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                is PcGamesState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        )
                        Button(onClick = { onEvent(PcUiEvent.TryAgain) }) {
                            Text(text = stringResource(id = R.string.try_again))
                        }
                    }
                }

                PcGamesState.Loading -> {
                    Column(
                        Modifier.fillMaxSize()
                    ) {
                        AnimatedShimmer {
                            ShimmerGamesScreen(brush = it)
                        }
                    }
                }

                is PcGamesState.Success -> {
                    Column(
                        Modifier.fillMaxSize()
                    ) {
                        // Liquid Glass Switch Bar
                        SwitchBar(
                            title = stringResource(id = R.string.enable_pc_games_rpc),
                            isChecked = isPcRpcRunning
                        ) {
                            isPcRpcRunning = !isPcRpcRunning
                            when (isPcRpcRunning) {
                                true -> {
                                    if (intent.hasExtra("RPC")) {
                                        Prefs[Prefs.LAST_RUN_PC_GAMES_RPC] =
                                            intent.getStringExtra("RPC")
                                        context.stopService(
                                            Intent(context, AppDetectionService::class.java)
                                        )
                                        context.stopService(
                                            Intent(context, MediaRpcService::class.java)
                                        )
                                        context.stopService(
                                            Intent(context, ExperimentalRpc::class.java)
                                        )
                                        context.startService(intent)
                                    }
                                }

                                false -> context.stopService(
                                    Intent(context, CustomRpcService::class.java)
                                )
                            }
                        }

                        // Games List with Liquid Glass Cards
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.games) { game ->
                                SingleChoicePcGameItem(
                                    game = game,
                                    selected = game.game_title == selected
                                ) { info ->
                                    selected = game.game_title
                                    val string = Json.encodeToString(
                                        RpcConfig(
                                            name = info.game_title,
                                            details = "",
                                            state = "",
                                            timestampsStart = System.currentTimeMillis().toString(),
                                            status = "dnd",
                                            largeImg = info.large_image ?: "",
                                            largeText = info.game_title,
                                            smallImg = "",
                                            smallText = "",
                                            type = "0",
                                        )
                                    )
                                    intent.apply {
                                        removeExtra("RPC")
                                        putExtra("RPC", string)
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }

            // Add Custom PC Game Dialog
            if (showAddDialog) {
                AddCustomPcGameDialog(
                    onDismiss = { showAddDialog = false },
                    onAdd = { title, platform, image ->
                        onEvent(PcUiEvent.AddCustomGame(title, platform, image))
                        showAddDialog = false
                    }
                )
            }
        }
    }
}

/**
 * Liquid Glass PC Game Card with specular border, cover art, platform icon, and selection glow.
 */
@Composable
fun SingleChoicePcGameItem(
    game: Game,
    selected: Boolean,
    onClick: (game: Game) -> Unit,
) {
    LiquidGlassCard(
        onClick = { onClick(game) },
        shape = RoundedCornerShape(24.dp),
        glowColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("game_item_${game.game_title}")
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Game Art Box with Selection Checkmark
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                if (!game.large_image.isNullOrBlank()) {
                    AsyncImage(
                        model = game.large_image,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp)),
                        contentDescription = game.game_title,
                    )
                }

                // Selected Checkmark Badge
                androidx.compose.animation.AnimatedVisibility(
                    visible = selected,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(6.dp),
                    enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                    exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut()
                ) {
                    Icon(
                        Icons.Outlined.Check,
                        contentDescription = "Selected",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Game Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = game.game_title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                    ),
                    color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (selected) Icons.Filled.PlayArrow else Icons.Outlined.Timer,
                        contentDescription = "Time Elapsed",
                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (selected) "Playing • Time Elapsed Active" else "Time Elapsed • Ready",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * Dialog to add a custom PC game.
 */
@Composable
fun AddCustomPcGameDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, platform: String, image: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(id = R.string.add_custom_pc_game),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(id = R.string.pc_game_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Banner Image URL (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title.trim(), "PC", imageUrl.trim())
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(26.dp)
    )
}
