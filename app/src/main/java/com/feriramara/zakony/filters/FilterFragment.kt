package com.feriramara.zakony.filters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import com.feriramara.zakony.R
import com.feriramara.zakony.utils.bindView
import com.feriramara.zakony.main.MainActivity

class FilterFragment : Fragment() {



    val viewModel: FilterViewModel by viewModels()

    private val filtersMotionLayout: FiltersMotionLayout by bindView(R.id.filters_motion_layout)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        (context as MainActivity).newConnection.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                viewModel.performSingleNetworkRequest()
            }
        })

        viewModel.uiState().observe(viewLifecycleOwner, Observer { uiState ->
            if (uiState != null) {
                render(uiState)
            }
        })

        return inflater.inflate(R.layout.filter_fragment, container, false)

    }

    private fun render(uiState: FilterUiState) {
        when (uiState) {
            is FilterUiState.Loading -> {
                onLoad()
            }
            is FilterUiState.Success -> {
                onSuccess(uiState)
            }
            is FilterUiState.Error -> {
                onError(uiState)
            }
        }
    }

    private fun onLoad() {
    }

    private fun onSuccess(uiState: FilterUiState.Success) {
        filtersMotionLayout.loadData(uiState.policies)
    }


    private fun onError(uiState: FilterUiState.Error) {
    }



}
