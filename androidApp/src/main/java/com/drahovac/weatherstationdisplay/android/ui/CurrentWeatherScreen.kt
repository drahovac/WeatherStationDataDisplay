package com.drahovac.weatherstationdisplay.android.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.drahovac.weatherstationdisplay.viewmodel.CurrentWeatherViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun CurrentWeatherScreen(
    viewModel: CurrentWeatherViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current

    LaunchedEffect(key1 = Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.observeWeather()
        }
    }

    Text(text = "Current weather ${state?.metric?.temp}")
}