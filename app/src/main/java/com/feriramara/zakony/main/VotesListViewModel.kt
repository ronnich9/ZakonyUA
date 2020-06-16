package com.feriramara.zakony.main

import androidx.lifecycle.*
import com.feriramara.zakony.utils.BaseViewModel
import com.feriramara.zakony.model.Vote
import com.feriramara.zakony.network.RadaApi
import com.feriramara.zakony.network.RadaApiService
import kotlinx.coroutines.launch

class VotesListViewModel(private val api: RadaApiService = RadaApi.retrofitService): BaseViewModel<UiState>() {

    init {
        performSingleNetworkRequest(-1)
    }

    fun performSingleNetworkRequest(policyId: Int) {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                if (policyId == -1) {
                    val recentVotes = api.getAllVotes()
                    uiState.value = UiState.Success(recentVotes)
                } else {
                    val policy = api.getPolicyDetail(policyId)
                    val bigVotes = policy.policy_divisions
                    val votes: MutableList<Vote> = mutableListOf()
                    for (vote in bigVotes) {
                        votes.add(vote.division)
                    }
                    votes.sortBy { it.date }
                    uiState.value = UiState.Success(votes.asReversed())


                }

            } catch (exception: Exception) {
                uiState.value = UiState.Error("Немає інтернет з'єднання!")
            }
        }
    }

}