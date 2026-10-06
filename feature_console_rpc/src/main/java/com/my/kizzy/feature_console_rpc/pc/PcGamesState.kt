/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2026
 *  *  * PcGamesState.kt is part of Kizzy
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_console_rpc.pc

import androidx.compose.runtime.Stable
import com.my.kizzy.domain.model.Game

@Stable
sealed interface PcGamesState {
    object Loading : PcGamesState
    data class Success(
        val games: List<Game>,
        val selectedCategory: String = "All"
    ) : PcGamesState
    data class Error(val error: String) : PcGamesState
}
