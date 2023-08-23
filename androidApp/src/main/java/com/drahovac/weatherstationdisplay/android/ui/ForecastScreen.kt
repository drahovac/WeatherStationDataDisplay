package com.drahovac.weatherstationdisplay.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.android.R
import com.drahovac.weatherstationdisplay.android.ui.component.LabelField
import com.drahovac.weatherstationdisplay.android.ui.component.LabelValueField
import com.drahovac.weatherstationdisplay.android.ui.component.LabelValueFieldPair
import com.drahovac.weatherstationdisplay.domain.toLocalizedShortDayName
import com.drahovac.weatherstationdisplay.viewmodel.DayPartState
import com.drahovac.weatherstationdisplay.viewmodel.ForecastActions
import com.drahovac.weatherstationdisplay.viewmodel.ForecastDayState
import com.drahovac.weatherstationdisplay.viewmodel.ForecastViewModel
import com.drahovac.weatherstationdisplay.viewmodel.MoonPhase
import kotlinx.datetime.LocalDateTime
import org.koin.androidx.compose.getViewModel

@Composable
fun ForecastScreen(viewModel: ForecastViewModel = getViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.days.isNotEmpty() -> ScreenContent(
            state.selectedDayIndex,
            state.selectedDay,
            state.days,
            viewModel
        )

        state.error != null -> ErrorScreen(
            error = state.error!!,
            onNewApiKey = viewModel::onNewApiKey,
            onNewDeviceId = viewModel::onNewDeviceId
        )

        else -> ProgressIndicator()
    }
}

@Composable
private fun ScreenContent(
    selectedDayIndex: Int,
    selectedDayState: ForecastDayState?,
    days: List<ForecastDayState>,
    actions: ForecastActions,
) {
    Column {
        TabRow(selectedTabIndex = selectedDayIndex) {
            days.forEachIndexed { index, forecastDayState ->
                Tab(
                    selected = index == selectedDayIndex,
                    onClick = { actions.selectDay(index) }) {
                    Column(Modifier.padding(8.dp), horizontalAlignment = CenterHorizontally) {
                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = forecastDayState.dateTime.date.toLocalizedShortDayName()
                        )
                        Text(
                            text = forecastDayState.temperatureMax.degrees
                        )
                        Text(
                            text = forecastDayState.temperatureMin.degrees
                        )
                    }
                }
            }
        }

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            selectedDayState?.let { day ->
                Spacer(modifier = Modifier.height(8.dp))
                ForecastDesc(
                    icon = day.icon,
                    narrative = day.narrative,
                    label = stringResource(id = MR.strings.weather_24_forecast.resourceId)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LabelValueFieldPair(
                    label1 = stringResource(id = MR.strings.history_max_temperature.resourceId),
                    value1 = day.temperatureMax.degrees,
                    label2 = stringResource(id = MR.strings.history_min_temperature.resourceId),
                    value2 = day.temperatureMin.degrees,
                )
                Spacer(modifier = Modifier.height(8.dp))
                LabelValueFieldPair(
                    label1 = stringResource(id = MR.strings.weather_rain_outlook.resourceId),
                    value1 = "${day.rainOutlook} ${stringResource(id = MR.strings.current_mm.resourceId)}",
                    label2 = stringResource(id = MR.strings.weather_snowfall_outlook.resourceId),
                    value2 = "${day.snowOutlook} ${stringResource(id = MR.strings.weather_snowfall_cm.resourceId)}",
                )
                Spacer(modifier = Modifier.height(8.dp))
                LabelValueFieldPair(
                    label1 = stringResource(id = MR.strings.current_uv.resourceId),
                    value1 = day.uvIndex,
                    label2 = stringResource(id = MR.strings.weather_sunrise.resourceId),
                    value2 = day.sunrise,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Column(Modifier.weight(1f)) {
                        MoonPhase(day)
                    }
                    Column(Modifier.weight(1f)) {
                        LabelValueField(
                            label = stringResource(id = MR.strings.weather_sunset.resourceId),
                            value = day.sunset,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                day.dayParts.forEach {
                    DayPart(it)
                }
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun DayPart(part: DayPartState) {
    Divider(Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    ForecastDesc(
        icon = part.icon,
        narrative = part.narrative,
        label = part.name,
        precChance = "${
            part.precipDesc?.let { stringResource(id = it.resourceId) }.orEmpty()
        }: ${part.precipChance} % \n${
            stringResource(id = MR.strings.weather_relative_humidity.resourceId)
        }: ${part.relativeHumidity} %"
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun MoonPhase(day: ForecastDayState) {
    LabelField(stringResource(id = MR.strings.weather_moonphase.resourceId))
    Icon(
        modifier = Modifier.size(64.dp),
        painter = painterResource(id = day.moonPhase.drawableRes),
        contentDescription = null
    )
    Text(
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        text = day.moonPhaseDesc
    )
}

@Composable
private fun ForecastDesc(
    label: String,
    icon: Int,
    narrative: String,
    precChance: String? = null,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon.WeatherIcon(
            Modifier
                .padding(top = 16.dp)
                .size(64.dp)
        )
        Column(Modifier.padding(start = 24.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = narrative,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            precChance?.let {
                Text(
                    text = precChance,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}


@Composable
private fun Int.WeatherIcon(modifier: Modifier = Modifier) {
    val iconWithLabel = when (this) {
        0 -> Pair(R.drawable.weather_tornado, MR.strings.weather_tornado)
        1 -> Pair(R.drawable.weather_tornado, MR.strings.weather_tropical_storm)
        2 -> Pair(R.drawable.weather_hurricane_outline, MR.strings.weather_hurricane)
        3 -> Pair(R.drawable.weather_lightning_rainy, MR.strings.weather_strong_storms)
        4 -> Pair(R.drawable.weather_lightning_rainy, MR.strings.weather_thunderstorms)
        5 -> Pair(R.drawable.weather_snowy_rainy, MR.strings.weather_rain_snow)
        6 -> Pair(R.drawable.weather_snowy_rainy, MR.strings.weather_sleet)
        7 -> Pair(R.drawable.weather_snowy_rainy, MR.strings.weather_wintry_mix)
        8 -> Pair(R.drawable.weather_rainy, MR.strings.weather_freezing_drizzle)
        9 -> Pair(R.drawable.weather_rainy, MR.strings.weather_drizzle)
        10 -> Pair(R.drawable.weather_rainy, MR.strings.weather_freezing_rain)
        11 -> Pair(R.drawable.weather_pouring, MR.strings.weather_showers)
        12 -> Pair(R.drawable.weather_pouring, MR.strings.weather_rain)
        13 -> Pair(R.drawable.weather_pouring, MR.strings.weather_flurries)
        14 -> Pair(R.drawable.weather_snowy, MR.strings.weather_snow_showers)
        15 -> Pair(R.drawable.weather_snowy_heavy, MR.strings.weather_blowing_drifting_snow)
        16 -> Pair(R.drawable.weather_snowy_heavy, MR.strings.weather_snow)
        17 -> Pair(R.drawable.weather_hail, MR.strings.weather_hail)
        18 -> Pair(R.drawable.weather_hail, MR.strings.weather_sleet)
        19 -> Pair(R.drawable.weather_fog, MR.strings.weather_blowing_dust_sandstorm)
        20 -> Pair(R.drawable.weather_fog, MR.strings.weather_foggy)
        21 -> Pair(R.drawable.weather_fog, MR.strings.weather_haze)
        22 -> Pair(R.drawable.weather_fog, MR.strings.weather_smoke)
        23 -> Pair(R.drawable.weather_windy, MR.strings.weather_breezy)
        24 -> Pair(R.drawable.weather_windy, MR.strings.weather_windy)
        25 -> Pair(R.drawable.weather_windy, MR.strings.weather_frigid_ice_crystals)
        26 -> Pair(R.drawable.weather_cloudy, MR.strings.weather_cloudy)
        27 -> Pair(R.drawable.weather_night_partly_cloudy, MR.strings.weather_mostly_cloudy)
        28 -> Pair(R.drawable.weather_partly_cloudy, MR.strings.weather_mostly_cloudy)
        29 -> Pair(R.drawable.weather_night_partly_cloudy, MR.strings.weather_partly_cloudy)
        30 -> Pair(R.drawable.weather_partly_cloudy, MR.strings.weather_partly_cloudy)
        31 -> Pair(R.drawable.weather_night, MR.strings.weather_clear)
        32 -> Pair(R.drawable.weather_sunny, MR.strings.weather_sunny)
        33 -> Pair(R.drawable.weather_night, MR.strings.weather_fair_mostly_clear)
        34 -> Pair(R.drawable.weather_sunny, MR.strings.weather_fair_mostly_sunny)
        35 -> Pair(R.drawable.weather_hail, MR.strings.weather_mixed_rain_hail)
        36 -> Pair(R.drawable.weather_sunny, MR.strings.weather_hot)
        37 -> Pair(R.drawable.weather_partly_lightning, MR.strings.weather_isolated_thunderstorms)
        38 -> Pair(R.drawable.weather_partly_lightning, MR.strings.weather_scattered_thunderstorms)
        39 -> Pair(R.drawable.weather_rainy, MR.strings.weather_scattered_showers)
        40 -> Pair(R.drawable.weather_pouring, MR.strings.weather_heavy_rain)
        41 -> Pair(R.drawable.weather_partly_snowy, MR.strings.weather_scattered_snow_showers)
        42 -> Pair(R.drawable.weather_snowy_heavy, MR.strings.weather_heavy_snow)
        43 -> Pair(R.drawable.weather_snowy_heavy, MR.strings.weather_blizzard)
        45 -> Pair(R.drawable.weather_rainy, MR.strings.weather_scattered_showers)
        46 -> Pair(R.drawable.weather_snowy, MR.strings.weather_scattered_snow_showers)
        47 -> Pair(R.drawable.weather_lightning, MR.strings.weather_scattered_thunderstorms)
        else -> Pair(R.drawable.baseline_block_24, MR.strings.weather_not_available)
    }
    Icon(
        modifier = modifier,
        painter = painterResource(id = iconWithLabel.first),
        contentDescription = stringResource(id = iconWithLabel.second.resourceId)
    )
}

private val MoonPhase.drawableRes: Int
    get() {
        return when (this) {
            MoonPhase.NEW -> R.drawable.moon_new
            MoonPhase.FIRST_QUARTER -> R.drawable.moon_first_quarter
            MoonPhase.FULL -> R.drawable.moon_full
            MoonPhase.LAST_QUARTER -> R.drawable.moon_last_quarter
            MoonPhase.WANING_CRESCENT -> R.drawable.moon_waning_crescent
            MoonPhase.WANING_GIBBOUS -> R.drawable.moon_waning_gibbous
            MoonPhase.WAXING_CRESCENT -> R.drawable.moon_waxing_crescent
            MoonPhase.WAXING_GIBBOUS -> R.drawable.moon_waxing_gibbous
        }
    }
private val Int?.degrees: String
    @Composable
    get() {
        return this?.let {
            "$it${stringResource(id = MR.strings.current_degree_celsius.resourceId)}"
        }.orEmpty()
    }

@Preview
@Composable
fun ForecastScreenPreview() {
    MaterialTheme {
        val day = ForecastDayState(
            dateTime = LocalDateTime.parse("2023-07-31T03:06"),
            temperatureMax = 23,
            temperatureMin = 22,
            icon = 3,
            rainOutlook = 2.0,
            snowOutlook = 3.0,
            narrative = "Mrazivá vánice",
            sunrise = "4:32",
            sunset = "20:00",
            moonPhaseDesc = "Úplněk",
            moonPhase = MoonPhase.FULL,
            dayParts = listOf(
                DayPartState(
                    "Night",
                    2,
                    "Bude hezky",
                    30,
                    MR.strings.weather_precip,
                    13,
                ),
                DayPartState(
                    "Day",
                    5,
                    "Bude ošklivo",
                    45,
                    MR.strings.weather_snow,
                    99,
                ),
            ),
            uvIndex = "6, Vysoký"
        )

        ScreenContent(
            selectedDayIndex = 2,
            days = listOf(
                day,
                day.copy(dateTime = LocalDateTime.parse("2023-08-01T03:06")),
                day.copy(dateTime = LocalDateTime.parse("2023-08-02T03:06")),
                day.copy(dateTime = LocalDateTime.parse("2023-08-03T03:06")),
                day.copy(dateTime = LocalDateTime.parse("2023-08-04T03:06")),
            ),
            selectedDayState = day.copy(dateTime = LocalDateTime.parse("2023-08-02T03:06")),
            actions = ActionsInvocationHandler.createActionsProxy(),
        )
    }
}
