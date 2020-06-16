package com.feriramara.zakony.filters

import androidx.lifecycle.viewModelScope
import com.feriramara.zakony.utils.BaseViewModel
import com.feriramara.zakony.network.RadaApi
import com.feriramara.zakony.network.RadaApiService
import kotlinx.coroutines.launch

class FilterViewModel(private val api: RadaApiService = RadaApi.retrofitService): BaseViewModel<FilterUiState>() {

    init {
        performSingleNetworkRequest()
    }

    fun performSingleNetworkRequest() {
        uiState.value = FilterUiState.Loading
        viewModelScope.launch {
            try {
                val recentVotes = api.getAllPolicies().reversed()
                uiState.value = FilterUiState.Success(recentVotes)
            } catch (exception: Exception) {
                uiState.value = FilterUiState.Error("Network Request failed!")
            }
        }
    }
}