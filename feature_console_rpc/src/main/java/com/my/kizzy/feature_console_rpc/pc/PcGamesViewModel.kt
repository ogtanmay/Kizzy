/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2026
 *  *  * PcGamesViewModel.kt is part of Kizzy
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_console_rpc.pc

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.kizzy.domain.model.Game
import com.my.kizzy.preference.Prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class PcGamesViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state: MutableState<PcGamesState> = mutableStateOf(PcGamesState.Loading)
    val state: State<PcGamesState> = _state
    private val allGames = mutableListOf<Game>()
    val isSearchBarVisible = mutableStateOf(false)
    private var currentCategory = "All"
    private var currentQuery = ""

    private var searchJob: Job? = null

    init {
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            _state.value = PcGamesState.Loading
            allGames.clear()
            allGames.addAll(PcGamesProvider.defaultGames)

            // Load saved custom PC games
            val customGamesJson = Prefs[Prefs.CUSTOM_PC_GAMES, "[]"]
            try {
                val customList: List<Game> = Json.decodeFromString(customGamesJson)
                allGames.addAll(0, customList)
            } catch (_: Exception) {}

            applyFilter()
        }
    }

    fun onUiEvent(uiEvent: PcUiEvent) {
        when (uiEvent) {
            PcUiEvent.CloseSearchBar -> {
                isSearchBarVisible.value = false
                currentQuery = ""
                applyFilter()
            }
            PcUiEvent.OpenSearchBar -> isSearchBarVisible.value = true
            is PcUiEvent.Search -> onSearch(uiEvent.query)
            is PcUiEvent.SelectCategory -> {
                currentCategory = uiEvent.category
                applyFilter()
            }
            is PcUiEvent.AddCustomGame -> {
                val newGame = Game(
                    platform = uiEvent.platform,
                    small_image = PcGamesProvider.platformIcon(uiEvent.platform),
                    large_image = uiEvent.imageUrl.ifBlank { null },
                    game_title = uiEvent.title
                )
                allGames.add(0, newGame)
                // Persist custom games
                val customGamesJson = Prefs[Prefs.CUSTOM_PC_GAMES, "[]"]
                val customList: MutableList<Game> = try {
                    Json.decodeFromString<List<Game>>(customGamesJson).toMutableList()
                } catch (_: Exception) {
                    mutableListOf()
                }
                customList.add(0, newGame)
                Prefs[Prefs.CUSTOM_PC_GAMES] = Json.encodeToString(customList)
                applyFilter()
            }
            PcUiEvent.TryAgain -> loadGames()
        }
    }

    private fun onSearch(query: String) {
        currentQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            applyFilter()
        }
    }

    private fun applyFilter() {
        var filtered = allGames.toList()
        if (currentCategory != "All") {
            filtered = filtered.filter { it.platform.equals(currentCategory, ignoreCase = true) }
        }
        if (currentQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.game_title.contains(currentQuery, ignoreCase = true) ||
                        it.platform.contains(currentQuery, ignoreCase = true)
            }
        }
        _state.value = PcGamesState.Success(
            games = filtered,
            selectedCategory = currentCategory
        )
    }
}
