package com.drahovac.weatherstationdisplay.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun HistoryDataScreen(viewModel: HistoryDataViewModel = getViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> ProgressIndicator()
        else -> ScreenContent(state.history)
    }
}

@Composable
private fun ScreenContent(
    history: List<HistoryObservation>
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        history.forEach {
            Text(text = it.obsTimeUtc.toString(), modifier = Modifier.padding(16.dp))
        }
    }
}
