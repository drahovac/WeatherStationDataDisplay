package com.drahovac.weatherstationdisplay.android.ui

import android.graphics.Typeface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.android.theme.rememberChartStyle
import com.drahovac.weatherstationdisplay.android.ui.component.LabelField
import com.drahovac.weatherstationdisplay.android.ui.component.LabelValueField
import com.drahovac.weatherstationdisplay.domain.HistoryMetric
import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.domain.toFormattedDate
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataActions
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataState
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataTab
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataViewModel
import com.drahovac.weatherstationdisplay.viewmodel.HistoryTabData
import com.drahovac.weatherstationdisplay.viewmodel.toTabData
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.legend.legendItem
import com.patrykandpatrick.vico.compose.legend.verticalLegend
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.component.shape.Shapes
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.koin.androidx.compose.getViewModel

@Composable
fun HistoryDataScreen(viewModel: HistoryDataViewModel = getViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> ProgressIndicator()
        else -> ScreenContent(state, viewModel)
    }
}

@Composable
private fun ScreenContent(
    state: HistoryDataState,
    actions: HistoryDataActions,
) {
    val tabData = state.tabData.get(state.selectedTab)

    Column {
        TabRow(selectedTabIndex = state.selectedTab.ordinal) {
            HistoryDataTab.values().forEach {
                Tab(
                    selected = it == state.selectedTab, onClick = { actions.selectTab(it) }) {
                    Text(
                        modifier = Modifier.padding(vertical = 16.dp),
                        text = it.label
                    )
                }
            }
        }

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Overview(tabData)

            Spacer(modifier = Modifier.height(32.dp))
            TemperatureChart(state)
        }
    }
}

@Composable
private fun TemperatureChart(state: HistoryDataState) {
    state.currentTabData?.let { tabData ->
        ProvideChartStyle(rememberChartStyle(chartColors)) {
            Chart(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                chart = lineChart(),
                startAxis = startAxis(
                    label = rememberStartAxisLabel(),
                    horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
                ),
                bottomAxis = bottomAxis(
                    label  = axisLabelComponent(horizontalPadding = 0.dp),
                    titleComponent = textComponent(color = MaterialTheme.colorScheme.onPrimaryContainer),
                    title = "Fuck",
                    valueFormatter = { value, chartValue ->
                        println("vaclav " + value)
                        value.toString()
                    },
                    itemPlacer = AxisItemPlacer.Horizontal.default(offset = 0)
                ),
                legend = rememberLegend(),
                chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
                autoScaleUp = AutoScaleUp.Full,
                horizontalLayout = HorizontalLayout.FullWidth(),
                model = tabData.tempChartModel,
            )
        }
    }
}

@Composable
private fun rememberStartAxisLabel() = axisLabelComponent(
    color = MaterialTheme.colorScheme.onPrimaryContainer,
    verticalPadding = 2.dp,
    horizontalPadding = 2.dp,
    verticalMargin = 2.dp,
    horizontalMargin = 2.dp,
    background = lineComponent(Color.Transparent),
)


@Composable
private fun rememberLegend() = verticalLegend(
    items = chartColors.mapIndexed { index, chartColor ->
        legendItem(
            icon = shapeComponent(Shapes.pillShape, chartColor),
            label = textComponent(
                color = currentChartStyle.axis.axisLabelColor,
                textSize = 8.sp,
                typeface = Typeface.MONOSPACE,
            ),
            labelText = "Legend",
        )
    },
    iconSize = 4.dp,
    iconPadding = 4.dp,
    spacing = 4.dp,
)

@Composable
private fun Overview(tabData: HistoryTabData?) {
    Row(Modifier.padding(16.dp)) {
        Column(Modifier.weight(1f)) {
            val maxDate = tabData?.maxDate?.toFormattedDate()?.let {
                "${stringResource(MR.strings.history_min_on.resourceId)} $it"
            }
            LabelValueField(
                label = stringResource(id = MR.strings.history_max_temperature.resourceId),
                value = tabData?.maxTemperature?.toString().orEmpty()
            )
            LabelField(label = maxDate.orEmpty())
        }
        Column(Modifier.weight(1f)) {
            val minDate = tabData?.minDate?.toFormattedDate()?.let {
                "${stringResource(MR.strings.history_min_on.resourceId)} $it"
            }
            LabelValueField(
                label = stringResource(id = MR.strings.history_min_temperature.resourceId),
                value = tabData?.minTemperature?.toString().orEmpty()
            )
            LabelField(label = minDate.orEmpty())
        }
    }
}

private val HistoryDataTab.label: String
    @Composable
    get() {
        return when (this) {
            HistoryDataTab.YESTERDAY -> stringResource(id = MR.strings.history_tab_yesterday.resourceId)
            HistoryDataTab.WEEK -> stringResource(id = MR.strings.history_tab_week.resourceId)
            HistoryDataTab.MONTH -> stringResource(id = MR.strings.history_tab_month.resourceId)
        }
    }

@Preview
@Composable
fun HistoryDataScreenPreview() {
    val observation = HistoryObservation(
        stationID = "ABC123",
        tz = "UTC",
        obsTimeUtc = LocalDateTime.parse("2023-07-29T10:34:56").toInstant(TimeZone.UTC),
        obsTimeLocal = "2023-07-29T12:34:56",
        epoch = 1627568096L,
        lat = 37.7749,
        lon = -122.4194,
        solarRadiationHigh = 1450.2,
        uvHigh = 8.3,
        winddirAvg = 180.0,
        humidityHigh = 85.0,
        humidityLow = 50.0,
        humidityAvg = 68.5,
        qcStatus = 1,
        metric = HistoryMetric(
            tempHigh = 30.5,
            tempLow = 20.0,
            tempAvg = 25.7,
            windspeedHigh = 15.2,
            windspeedLow = 2.5,
            windspeedAvg = 8.9,
            windgustHigh = 18.7,
            windgustLow = 3.2,
            windgustAvg = 10.1,
            dewptHigh = 22.5,
            dewptLow = 18.3,
            dewptAvg = 20.1,
            windchillHigh = 19.8,
            windchillLow = 14.2,
            windchillAvg = 17.6,
            heatindexHigh = 32.0,
            heatindexLow = 24.3,
            heatindexAvg = 28.1,
            pressureMax = 1013.2,
            pressureMin = 1008.7,
            pressureTrend = -2.3,
            precipRate = 0.5,
            precipTotal = 12.3
        )
    )

    MaterialTheme {
        ScreenContent(
            state =
            HistoryDataState(
                selectedTab = HistoryDataTab.MONTH,
                tabData = mapOf(HistoryDataTab.MONTH to listOf(observation).toTabData()),
            ),
            actions = ActionsInvocationHandler.createActionsProxy(),
        )
    }
}


private val chartColors
    @Composable
    get() = listOf(
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.primary,
    )
