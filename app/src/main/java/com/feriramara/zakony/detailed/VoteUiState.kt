package com.feriramara.zakony.detailed

import com.feriramara.zakony.model.VoteDetail

sealed class VoteUiState {
    object Loading : VoteUiState()
    data class Success(val vote: VoteDetail) : VoteUiState()
    data class Error(val message: String) : VoteUiState()
}