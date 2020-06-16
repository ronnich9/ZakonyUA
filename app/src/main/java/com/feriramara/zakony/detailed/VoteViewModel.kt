package com.feriramara.zakony.detailed

import androidx.lifecycle.viewModelScope
import com.feriramara.zakony.utils.BaseViewModel
import com.feriramara.zakony.network.RadaApi
import com.feriramara.zakony.network.RadaApiService
import kotlinx.coroutines.launch

class VoteViewModel(private val api: RadaApiService = RadaApi.retrofitService): BaseViewModel<VoteUiState>() {

//    init {
//        performSingleNetworkRequest("32944")
//    }

    fun performSingleNetworkRequest(voteId: String?) {
        uiState.value = VoteUiState.Loading
        viewModelScope.launch {
            try {
                val vote = api.getVoteDetail(voteId)
                uiState.value = VoteUiState.Success(vote)
            } catch (exception: Exception) {
                uiState.value = VoteUiState.Error("Network Request failed!")
            }
        }
    }
}