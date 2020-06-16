package com.feriramara.zakony.filters

import com.feriramara.zakony.model.Policy

sealed class FilterUiState {
    object Loading : FilterUiState()
    data class Success(val policies: List<Policy>) : FilterUiState()
    data class Error(val message: String) : FilterUiState()
}