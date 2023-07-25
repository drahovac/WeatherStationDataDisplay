package com.drahovac.weatherstationdisplay.android.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.android.R
import com.drahovac.weatherstationdisplay.android.theme.WeatherTheme
import com.drahovac.weatherstationdisplay.domain.CurrentWeatherObservation
import com.drahovac.weatherstationdisplay.domain.Metric
import com.drahovac.weatherstationdisplay.domain.NetworkError
import com.drahovac.weatherstationdisplay.viewmodel.CurrentWeatherViewModel
import org.koin.androidx.compose.getViewModel
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun CurrentWeatherScreen(
    navController: NavController,
    viewModel: CurrentWeatherViewModel = getViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dest by viewModel.navigationFlow.collectAsStateWithLifecycle()

    dest.popUp(navController)
    val lifecycle = LocalLifecycleOwner.current

    LaunchedEffect(key1 = Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.observeWeather()
        }
    }

    state.observation?.let { ScreenContent(it) }
    state.error?.let {
        ErrorContent(
            it,
            onNewApiKey = viewModel::onNewApiKey,
            onNewDeviceId = viewModel::onNewDeviceId
        )
    }
}

@Composable
private fun ScreenContent(
    state: CurrentWeatherObservation
) {
    val celsius = stringResource(id = MR.strings.current_degree_celsius.resourceId)

    Column(Modifier.verticalScroll(rememberScrollState())) {
        Row(
            Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ) {
            CurrentTemperature(
                temp = state.metric.temp,
                heatIndex = state.metric.heatIndex,
                celsius = celsius
            )
            UvIndex(state.uv.roundToInt())
        }
        Row(Modifier.padding(horizontal = 24.dp)) {
            Humidity(
                dewpt = state.metric.dewpt,
                humidity = state.humidity,
                pressure = state.metric.pressure,
                precRate = state.metric.precipRate,
                precTotal = state.metric.precipTotal,
                celsius = celsius,
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 24.dp),
                horizontalAlignment = CenterHorizontally,
            ) {
                SolarRadiation(state.solarRadiation)
                Wind(state.metric.windSpeed, state.winddir, state.metric.windGust)
            }
        }
    }
}

@Composable
fun SolarRadiation(solarRadiation: Double) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = MR.strings.current_solar_radiation.resourceId),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        textAlign = TextAlign.Start,
    )
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = solarRadiation.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Start,
        )
        Text(
            modifier = Modifier
                .alignByBaseline()
                .padding(start = 4.dp),
            text = stringResource(id = MR.strings.current_radiation_units.resourceId),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
private fun RowScope.UvIndex(uv: Int) {
    Column(
        Modifier
            .weight(1f)
            .padding(start = 16.dp)
    ) {
        Text(
            text = stringResource(id = MR.strings.current_uv.resourceId),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.align(CenterHorizontally)
        )

        Box(Modifier.fillMaxWidth()) {
            UvChart(uv)
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                maxLines = 1,
                text = uv.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun BoxScope.UvChart(uv: Int) {
    Column(
        Modifier
            .align(BottomCenter)
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        for (i in 0..9) {
            val widthIncrease = i * 8
            val currentStep = 10 - i

            Spacer(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .border(
                        if (currentStep <= uv) 0.dp else 1.dp,
                        MaterialTheme.colorScheme.onPrimary
                    )
                    .background(getUvColor(currentStep, uv))
                    .height(6.dp)
                    .width(60.dp + widthIncrease.dp)
            )
            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
            )
        }
    }
}

fun getUvColor(stepValue: Int, currentUV: Int): Color {
    return when {
        stepValue > currentUV -> Color.Transparent
        stepValue in 0..2 -> Color(0xFF00FF00)
        stepValue in 3..5 -> Color(0xFFFFFF00)
        stepValue in 6..7 -> Color(0xFFFF9900)
        stepValue in 8..9 -> Color(0xFFFF0000)
        else -> Color(0xFFFF0099)
    }

}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun RowScope.CurrentTemperature(
    temp: Double,
    heatIndex: Double,
    celsius: String
) {
    Column(Modifier.Companion.weight(1f)) {
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
                text = temp.toString(),
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
            text = "${
                stringResource(id = MR.strings.current_feels_like.resourceId)
            } $heatIndex $celsius",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun RowScope.Humidity(
    dewpt: Double,
    humidity: Double,
    pressure: Double,
    precRate: Double,
    precTotal: Double,
    celsius: String,
) {
    Column(
        Modifier
            .weight(1f)
            .padding(top = 24.dp)
    ) {
        Text(
            text = stringResource(id = MR.strings.current_dew_point.resourceId),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "$dewpt $celsius",
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
            text = "${humidity}%",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = MR.strings.current_pressure.resourceId),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "$pressure ${stringResource(id = MR.strings.current_hpa.resourceId)}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = MR.strings.current_prec_rate.resourceId),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "$precRate ${stringResource(id = MR.strings.current_mm_hr.resourceId)}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = MR.strings.current_prec_total.resourceId),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "$precTotal ${stringResource(id = MR.strings.current_mm.resourceId)}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun Wind(
    windSpeed: Double,
    windDir: Int,
    gust: Double,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        text = stringResource(id = MR.strings.current_gust.resourceId),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        textAlign = TextAlign.Start,
    )
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "$gust ${stringResource(id = MR.strings.current_km_h.resourceId)}",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        textAlign = TextAlign.Start,
    )
    Text(
        modifier = Modifier.padding(top = 8.dp),
        text = stringResource(id = MR.strings.current_north.resourceId),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
    Row(verticalAlignment = CenterVertically) {
        Text(
            modifier = Modifier.padding(end = 4.dp),
            text = stringResource(id = MR.strings.current_west.resourceId),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Compass(windSpeed, windDir)
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = stringResource(id = MR.strings.current_east.resourceId),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
    Text(
        text = stringResource(id = MR.strings.current_south.resourceId),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
private fun RowScope.Compass(
    windSpeed: Double,
    windDir: Int,
) {
    val sizeDp = 100.dp
    val radius = with(LocalDensity.current) { (sizeDp / 2).toPx() }.toFloat()
    val circleRadius = radius - with(LocalDensity.current) { 3.dp.toPx() }.toFloat()
    val center = Offset(radius, radius)

    val lines = remember {
        mutableListOf<Offset>().apply {
            val numLines = 72
            val angleIncrement: Float = ((2 * Math.PI) / numLines.toFloat()).toFloat()

            for (i in 0..numLines) {
                val angle = i.toFloat() * angleIncrement
                add(computeCircleOffset(angle, radius, center))
            }
        }
    }
    val windArrowLine = remember(windDir) {
        computeCircleOffset(
            ((2 * Math.PI) / 360f * (windDir - 90)).toFloat(),
            circleRadius,
            center
        )
    }
    val lineColor = MaterialTheme.colorScheme.primary
    val circleColor = MaterialTheme.colorScheme.error
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer

    Column(modifier = Modifier
        .size(sizeDp)
        .align(CenterVertically)
        .drawWithCache {
            onDrawBehind {
                lines.forEach {
                    drawLine(
                        lineColor,
                        center,
                        it,
                        1.dp.toPx()
                    )
                }
                drawCircle(
                    backgroundColor,
                    radius * 0.9F
                )
                drawCircle(
                    circleColor,
                    6.dp.toPx(),
                    windArrowLine
                )
            }
        }) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 28.dp),
            maxLines = 1,
            text = windSpeed.toString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            maxLines = 1,
            text = stringResource(id = MR.strings.current_km_h.resourceId),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
        )

    }
}

private fun computeCircleOffset(
    angle: Float,
    radius: Float,
    center: Offset,
): Offset {
    val x = center.x + radius * cos(angle)
    val y = center.y + radius * sin(angle)
    return Offset(x, y)
}

@Composable
fun ErrorContent(
    error: NetworkError,
    onNewDeviceId: () -> Unit,
    onNewApiKey: () -> Unit,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleLarge,
            text = stringResource(id = MR.strings.current_error_title.resourceId)
        )

        Image(
            modifier = Modifier
                .size(120.dp)
                .align(CenterHorizontally)
                .padding(bottom = 8.dp),
            painter = painterResource(id = R.drawable.baseline_error_24),
            contentDescription = stringResource(id = MR.strings.current_error_content.resourceId),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error)
        )

        Text(
            text = getErrorText(error),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.bodyLarge,
        )

        if (error is NetworkError.InvalidDeviceId) {
            RenewButton(
                text = MR.strings.current_error_new_station_id.resourceId,
                onNewDeviceId
            )
        }
        if (error is NetworkError.InvalidApiKey) {
            RenewButton(
                text = MR.strings.current_error_new_api_key.resourceId,
                onNewApiKey
            )
        }

    }
}

@Composable
private fun RenewButton(
    @StringRes text: Int,
    action: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 32.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        onClick = action
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_autorenew_24),
            contentDescription = null
        )
        Spacer(modifier = Modifier.widthIn(8.dp))
        Text(text = stringResource(id = text))
    }
}

@Composable
fun getErrorText(error: NetworkError): String {
    return when (error) {
        is NetworkError.InvalidApiKey -> stringResource(id = MR.strings.current_error_new_api_key_message.resourceId)
        is NetworkError.InvalidDeviceId -> stringResource(id = MR.strings.current_error_station_id_message.resourceId)
        is NetworkError.TooManyRequests -> stringResource(id = MR.strings.current_error_quota.resourceId)
        else -> error.message.orEmpty()
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

@Preview
@Composable
fun CurrentWeatherScreenErrorPreview() {
    WeatherTheme {
        ErrorContent(error = NetworkError.InvalidApiKey, {}, {})
    }
}
