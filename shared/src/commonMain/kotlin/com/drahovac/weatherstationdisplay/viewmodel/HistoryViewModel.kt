package com.drahovac.weatherstationdisplay.viewmodel

import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

class HistoryViewModel : KMMViewModel(), HistoryActions {

    private val _state = MutableStateFlow(HistoryState(HistoryNoData()))

    @NativeCoroutines
    val state = _state.asStateFlow()

    override fun switchDateDialog() {
        _state.update {
            val newValue = (it.noData?.isPickerVisible ?: false).not()
            it.copy(noData = it.noData?.copy(isPickerVisible = newValue))
        }
    }

    override fun selectStartDate(date: LocalDate) {
        _state.update {
            it.copy(noData = it.noData?.copy(startDate = date, isPickerVisible = false))
        }
    }
}

interface HistoryActions {

    fun switchDateDialog()

    fun selectStartDate(date: LocalDate)
}
