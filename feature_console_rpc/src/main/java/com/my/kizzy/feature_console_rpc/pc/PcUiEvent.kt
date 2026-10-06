/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2026
 *  *  * PcUiEvent.kt is part of Kizzy
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_console_rpc.pc

sealed interface PcUiEvent {
    object TryAgain : PcUiEvent
    object CloseSearchBar : PcUiEvent
    object OpenSearchBar : PcUiEvent
    data class Search(val query: String) : PcUiEvent
    data class SelectCategory(val category: String) : PcUiEvent
    data class AddCustomGame(val title: String, val platform: String, val imageUrl: String) : PcUiEvent
}
