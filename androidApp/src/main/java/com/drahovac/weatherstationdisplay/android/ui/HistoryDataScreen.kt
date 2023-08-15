package com.drahovac.weatherstationdisplay.android.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.drahovac.weatherstationdisplay.android.ui.component.MarkerLineComponent
import com.drahovac.weatherstationdisplay.domain.HistoryMetric
import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.domain.toFormattedDate
import com.drahovac.weatherstationdisplay.domain.toLocalizedLongDayName
import com.drahovac.weatherstationdisplay.domain.toLocalizedMontName
import com.drahovac.weatherstationdisplay.domain.toLocalizedShortDayName
import com.drahovac.weatherstationdisplay.viewmodel.ChartState
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataActions
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataState
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataViewModel
import com.drahovac.weatherstationdisplay.viewmodel.HistoryTab
import com.drahovac.weatherstationdisplay.viewmodel.HistoryTabState
import com.drahovac.weatherstationdisplay.viewmodel.TempChartSelection
import com.drahovac.weatherstationdisplay.viewmodel.TempChartSets
import com.drahovac.weatherstationdisplay.viewmodel.toTabData
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.overlayingComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.DefaultDimens.AXIS_LABEL_SIZE
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.DashedShape
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.extension.copyColor
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerVisibilityChangeListener
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
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
    val tabData = state.tabData[state.selectedTab]

    Column {
        TabRow(selectedTabIndex = state.selectedTab.ordinal) {
            HistoryTab.values().forEach {
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
            tabData?.let { IntervalInfo(tabData, state.selectedTab) }
            Overview(tabData)

            Spacer(modifier = Modifier.height(32.dp))
            state.currentTabData?.takeIf { it.temperature.tempChart.hasMultipleItems }
                ?.let {
                    TemperatureChart(it.temperature.tempChart, actions)
                }
        }
    }
}

@Composable
private fun TemperatureChart(
    chartState: ChartState<TempChartSelection>,
    actions: HistoryDataActions
) {
    val degree = stringResource(id = MR.strings.current_degree_celsius.resourceId)

    Text(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .padding(bottom = 4.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        text = stringResource(id = MR.strings.history_temperature.resourceId)
    )
    Row {
        Column(Modifier.weight(1f)) {
            TempChartLegend(
                chartColors = chartColors,
                tempChartSets = chartState.tempChartSets,
                actions = actions
            )
        }
        Column(
            Modifier
                .weight(1f)
                .padding(start = 4.dp)
        ) {
            chartState.selectedEntries?.let { selection ->
                Text(
                    text = selection.date.toFormattedDate(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                )
                selection.maxTemp?.let {
                    TemperatureEntryLine(
                        value = it,
                        labelId = MR.strings.history_max_temperature.resourceId
                    )
                }
                selection.avgTemp?.let {
                    TemperatureEntryLine(
                        value = it,
                        labelId = MR.strings.history_avg_temperature.resourceId
                    )
                }
                selection.minTemp?.let {
                    TemperatureEntryLine(
                        value = it,
                        labelId = MR.strings.history_min_temperature.resourceId
                    )
                }
            }
        }
    }

    val colors = chartColors.filterSets(chartState.tempChartSets)
    ProvideChartStyle(rememberChartStyle(colors)) {
        Chart(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp),
            chart = lineChart(
                axisValuesOverrider = object : AxisValuesOverrider<ChartEntryModel> {
                    override fun getMinY(model: ChartEntryModel): Float {
                        return model.minY
                    }
                }
            ),
            startAxis = startAxis(
                maxLabelCount = 6,
                label = rememberStartAxisLabel(),
                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
                valueFormatter = { value, _ ->
                    "$value$degree"
                }
            ),
            marker = rememberMarker(),
            markerVisibilityChangeListener = object : MarkerVisibilityChangeListener {
                override fun onMarkerShown(
                    marker: Marker,
                    markerEntryModels: List<Marker.EntryModel>
                ) {
                    super.onMarkerShown(marker, markerEntryModels)
                    actions.selectTempPoints(
                        markerEntryModels.map { it.entry },
                        markerEntryModels.firstOrNull()?.index ?: 0
                    )
                }

                override fun onMarkerHidden(marker: Marker) {
                    super.onMarkerHidden(marker)
                    actions.selectTempPoints(emptyList(), 0)
                }

                override fun onMarkerMoved(
                    marker: Marker,
                    markerEntryModels: List<Marker.EntryModel>
                ) {
                    super.onMarkerMoved(marker, markerEntryModels)
                    actions.selectTempPoints(
                        markerEntryModels.map { it.entry },
                        markerEntryModels.firstOrNull()?.index ?: 0
                    )
                }
            },
            bottomAxis = bottomAxis(
                label = null,
                itemPlacer = AxisItemPlacer.Horizontal.default(offset = 0)
            ),
            chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
            autoScaleUp = AutoScaleUp.Full,
            horizontalLayout = HorizontalLayout.FullWidth(),
            model = chartState.tempChartModel,
        )
    }
    Row(Modifier.padding(horizontal = 4.dp)) {
        Text(
            text = "30.0°C",
            fontSize = AXIS_LABEL_SIZE.sp,
            modifier = Modifier.alpha(0f)
        ) // Spacer for vert. axis
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
            chartState.bottomLabels.forEach {
                Text(text = it, fontSize = AXIS_LABEL_SIZE.sp)
            }
        }

    }
}

@Composable
private fun TemperatureEntryLine(value: ChartEntry, @StringRes labelId: Int) {
    Text(
        text = "${stringResource(labelId)} : ${value.y}${
            stringResource(
                id = MR.strings.current_degree_celsius.resourceId
            )
        }",
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        style = MaterialTheme.typography.bodyMedium
    )
}

private fun List<Color>.filterSets(tempChartSets: TempChartSets): List<Color> {
    return listOfNotNull(
        this[0].takeIf { tempChartSets.isMaxAllowed },
        this[1].takeIf { tempChartSets.isAvgAllowed },
        this[2].takeIf { tempChartSets.isMinAllowed },
    ).takeUnless { it.isEmpty() } ?: this
}

@Composable
private fun TempChartLegend(
    tempChartSets: TempChartSets,
    chartColors: List<Color>,
    actions: HistoryDataActions,
) {
    LegendLine(
        label = stringResource(id = MR.strings.history_max_temperature.resourceId),
        color = chartColors.first(),
        checked = tempChartSets.isMaxAllowed,
        onClick = { actions.selectMaxTempChart(it) },
    )
    LegendLine(
        label = stringResource(id = MR.strings.history_avg_temperature.resourceId),
        color = chartColors[1],
        checked = tempChartSets.isAvgAllowed,
        onClick = { actions.selectAvgTempChart(it) },
    )
    LegendLine(
        label = stringResource(id = MR.strings.history_min_temperature.resourceId),
        color = chartColors[2],
        checked = tempChartSets.isMinAllowed,
        onClick = { actions.selectMinTempChart(it) },
    )
}

@Composable
private fun LegendLine(
    label: String,
    color: Color,
    checked: Boolean,
    onClick: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .fillMaxWidth()
            .clickable {
                onClick(!checked)
            }
            .padding(vertical = 4.dp)) {
        Checkbox(
            checked = checked, onCheckedChange = null, colors = CheckboxDefaults.colors(
                checkedColor = color
            )
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = label,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.bodyMedium
        )
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
private fun Overview(tabData: HistoryTabState?) {
    Row(
        Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Column(Modifier.weight(1f)) {
            val maxDate = tabData?.temperature?.maxDate?.toFormattedDate()?.let {
                "${stringResource(MR.strings.history_min_on.resourceId)} $it"
            }
            val maxUVDate = tabData?.uv?.maxUvDate?.toFormattedDate()?.let {
                "${stringResource(MR.strings.history_min_on.resourceId)} $it"
            }
            LabelValueField(
                label = stringResource(id = MR.strings.history_max_temperature.resourceId),
                value = tabData?.temperature?.maxTemperature?.toString().orEmpty()
            )
            LabelField(label = maxDate.orEmpty())
            Spacer(modifier = Modifier.height(8.dp))
            LabelValueField(
                label = stringResource(id = MR.strings.history_high_uv_index.resourceId),
                value = tabData?.uv?.maxUvIndex?.toString().orEmpty()
            )
            LabelField(label = maxUVDate.orEmpty())
        }
        Column(Modifier.weight(1f)) {
            val minDate = tabData?.temperature?.minDate?.toFormattedDate()?.let {
                "${stringResource(MR.strings.history_min_on.resourceId)} $it"
            }
            val maxRadiationDate = tabData?.uv?.maxRadiationDate?.toFormattedDate()?.let {
                "${stringResource(MR.strings.history_min_on.resourceId)} $it"
            }
            LabelValueField(
                label = stringResource(id = MR.strings.history_min_temperature.resourceId),
                value = tabData?.temperature?.minTemperature?.toString().orEmpty()
            )
            LabelField(label = minDate.orEmpty())
            Spacer(modifier = Modifier.height(8.dp))
            LabelField(stringResource(id = MR.strings.history_max_solar_radiation.resourceId))
            Row {
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = tabData?.uv?.maxRadiation?.toString().orEmpty(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .alignByBaseline(),
                    text = stringResource(id = MR.strings.current_radiation_units.resourceId),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            LabelField(label = maxRadiationDate.orEmpty())
        }
    }
}

@Composable
private fun IntervalInfo(tabData: HistoryTabState, tab: HistoryTab) {
    Text(
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp),
        text = when (tab) {
            HistoryTab.YESTERDAY -> "${tabData.startDate.toLocalizedLongDayName()}, ${tabData.startDate.toFormattedDate()}"
            HistoryTab.WEEK -> "${tabData.startDate.toLocalizedShortDayName()}, ${tabData.startDate.toFormattedDate()} - ${
                tabData.startDate.plus(
                    6,
                    DateTimeUnit.DAY
                ).toLocalizedShortDayName()
            }, ${tabData.startDate.plus(6, DateTimeUnit.DAY).toFormattedDate()}"

            HistoryTab.MONTH -> tabData.startDate.toLocalizedMontName()
        }
    )
}

private val HistoryTab.label: String
    @Composable
    get() {
        return when (this) {
            HistoryTab.YESTERDAY -> stringResource(id = MR.strings.history_tab_yesterday.resourceId)
            HistoryTab.WEEK -> stringResource(id = MR.strings.history_tab_week.resourceId)
            HistoryTab.MONTH -> stringResource(id = MR.strings.history_tab_month.resourceId)
        }
    }

@Composable
internal fun rememberMarker(): Marker {
    val indicatorInnerComponent =
        shapeComponent(Shapes.pillShape, MaterialTheme.colorScheme.surface)
    val indicatorCenterComponent = shapeComponent(Shapes.pillShape, Color.White)
    val indicatorOuterComponent = shapeComponent(Shapes.pillShape, Color.White)
    val indicator = overlayingComponent(
        outer = indicatorOuterComponent,
        inner = overlayingComponent(
            outer = indicatorCenterComponent,
            inner = indicatorInnerComponent,
            innerPaddingAll = indicatorInnerAndCenterComponentPaddingValue,
        ),
        innerPaddingAll = indicatorCenterAndOuterComponentPaddingValue,
    )
    val guideline = lineComponent(
        MaterialTheme.colorScheme.onSurface.copy(GUIDELINE_ALPHA),
        guidelineThickness,
        guidelineShape,
    )
    return remember(indicator, guideline) {
        MarkerLineComponent(indicator, guideline, onApplyEntryColor = { entryColor ->
            indicatorOuterComponent.color =
                entryColor.copyColor(INDICATOR_OUTER_COMPONENT_ALPHA)
            with(indicatorCenterComponent) {
                color = entryColor
            }
        })
    }
}

private const val GUIDELINE_ALPHA = .2f
private const val INDICATOR_OUTER_COMPONENT_ALPHA = 32
private const val GUIDELINE_DASH_LENGTH_DP = 8f
private const val GUIDELINE_GAP_LENGTH_DP = 4f

private val indicatorInnerAndCenterComponentPaddingValue = 5.dp
private val indicatorCenterAndOuterComponentPaddingValue = 8.dp
private val guidelineThickness = 2.dp
private val guidelineShape =
    DashedShape(Shapes.pillShape, GUIDELINE_DASH_LENGTH_DP, GUIDELINE_GAP_LENGTH_DP)


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
                selectedTab = HistoryTab.MONTH,
                tabData = mapOf(
                    HistoryTab.MONTH to listOf(observation).toTabData(
                        TempChartSets(),
                        HistoryTab.MONTH,
                    )
                ),
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
