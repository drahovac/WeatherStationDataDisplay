package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.usecase.HistoryUseCase
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.coroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryDataViewModel(
    historyUseCase: HistoryUseCase,
) : KMMViewModel() {

    private val _state = MutableStateFlow(
        HistoryDataState()
    )

    @NativeCoroutines
    val state = _state

    init {
        viewModelScope.coroutineScope.launch {
            historyUseCase.history.collectLatest { history ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        history = history,
                    )
                }
            }
        }
        viewModelScope.coroutineScope.launch {
            _state.update { it.copy(isLoading = true) }
            historyUseCase.fetchHistoryUpToDate()
            _state.update { it.copy(isLoading = false) }
        }
    }

}