package com.drahovac.weatherstationdisplay.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.android.theme.WeatherTheme
import com.drahovac.weatherstationdisplay.domain.CurrentWeatherObservation
import com.drahovac.weatherstationdisplay.domain.Metric
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

    state?.let { ScreenContent(it) }
    // TODO error handling
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScreenContent(
    state: CurrentWeatherObservation
) {
    val celsius = stringResource(id = MR.strings.current_degree_celsius.resourceId)

    Row(Modifier.padding(24.dp)) {
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(id = MR.strings.current_temperature.resourceId),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            FlowRow(
                Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.alignByBaseline(),
                    maxLines = 1,
                    text = state.metric.temp.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .alignByBaseline(),
                    text = celsius,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                )
            }

            Text(
                text = "${stringResource(id = MR.strings.current_feels_like.resourceId)} ${
                    state.metric.heatIndex
                } $celsius",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Column(
            Modifier
                .weight(1f)
                .padding(start = 16.dp)) {
            Text(
                text = stringResource(id = MR.strings.current_dew_point.resourceId),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "${state.metric.dewpt} $celsius",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = MR.strings.current_humidity.resourceId),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "${state.humidity}%",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview
@Composable
fun CurrentWeatherScreenPreview() {
    WeatherTheme {
        ScreenContent(
            state = CurrentWeatherObservation(
                stationID = "KSFO",
                obsTimeUtc = "2023-07-20T12:47:54Z",
                obsTimeLocal = "2023-07-20T19:47:54-07:00",
                neighborhood = "San Francisco",
                softwareType = null,
                country = "United States",
                solarRadiation = 123.45,
                lon = -122.45,
                realtimeFrequency = null,
                epoch = 1658115674,
                lat = 37.77,
                uv = 4.56,
                winddir = 123,
                humidity = 65.78,
                qcStatus = 1,
                metric = Metric(
                    temp = 23.45,
                    heatIndex = 25.67,
                    dewpt = 12.34,
                    windChill = 10.0,
                    windSpeed = 12.34,
                    windGust = 15.67,
                    pressure = 1013.25,
                    precipRate = 0.0,
                    precipTotal = 0.0,
                    elev = 100.0
                )
            )
        )
    }
}