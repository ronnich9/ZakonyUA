package com.feriramara.zakony.main

import com.feriramara.zakony.model.Vote

sealed class UiState() {
    object Loading : UiState()
    data class Success(val votes: List<Vote>) : UiState()
    data class Error(val message: String) : UiState()
}