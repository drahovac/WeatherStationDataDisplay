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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.android.R
import com.drahovac.weatherstationdisplay.android.theme.rememberChartStyle
import com.drahovac.weatherstationdisplay.android.ui.component.LabelField
import com.drahovac.weatherstationdisplay.android.ui.component.LabelValueField
import com.drahovac.weatherstationdisplay.android.ui.component.LabelValueFieldWithUnits
import com.drahovac.weatherstationdisplay.android.ui.component.MarkerLineComponent
import com.drahovac.weatherstationdisplay.domain.History
import com.drahovac.weatherstationdisplay.domain.HistoryMetric
import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.domain.toFormattedDate
import com.drahovac.weatherstationdisplay.domain.toFormattedShortDate
import com.drahovac.weatherstationdisplay.domain.toLocalizedLongDayName
import com.drahovac.weatherstationdisplay.domain.toLocalizedMontName
import com.drahovac.weatherstationdisplay.domain.toLocalizedShortDayName
import com.drahovac.weatherstationdisplay.viewmodel.ChartState
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataActions
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataState
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataViewModel
import com.drahovac.weatherstationdisplay.viewmodel.HistoryTab
import com.drahovac.weatherstationdisplay.viewmodel.HistoryTabState
import com.drahovac.weatherstationdisplay.viewmodel.HumidityChartSelection
import com.drahovac.weatherstationdisplay.viewmodel.HumidityChartSets
import com.drahovac.weatherstationdisplay.viewmodel.PressureChartSelection
import com.drahovac.weatherstationdisplay.viewmodel.PressureSets
import com.drahovac.weatherstationdisplay.viewmodel.TempChartSelection
import com.drahovac.weatherstationdisplay.viewmodel.TempChartSets
import com.drahovac.weatherstationdisplay.viewmodel.toTabData
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
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
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.extension.copyColor
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerVisibilityChangeListener
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
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
            tabData?.let { IntervalInfo(tabData, state.selectedTab, actions) }
            when {
                tabData?.hasData == true -> Overview(tabData)
                tabData != null -> NoData()
            }

            Spacer(modifier = Modifier.height(32.dp))
            state.currentTabData?.takeIf { it.temperature.chart.hasMultipleItems }
                ?.let {
                    TemperatureChart(it.temperature.chart, actions)
                }
            Spacer(modifier = Modifier.height(16.dp))
            state.currentTabData?.takeIf { it.pressure.chart.hasMultipleItems }
                ?.let {
                    PressureChart(it.pressure.chart, actions)
                }
            Spacer(modifier = Modifier.height(16.dp))
            state.currentTabData?.takeIf { it.humidity.hasMultipleItems }
                ?.let {
                    HumidityChart(it.humidity, actions)
                }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun NoData() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .padding(top = 16.dp)
                .size(64.dp),
            painter = painterResource(id = R.drawable.baseline_block_24),
            contentDescription = null
        )

        Text(
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = MR.strings.history_no_data.resourceId),
            style = MaterialTheme.typography.displayMedium
        )
    }
}

@Composable
private fun TemperatureChart(
    chartState: ChartState<TempChartSelection, TempChartSets>,
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
                chartColors = tempChartColors,
                tempChartSets = chartState.chartSets,
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

    val colors = tempChartColors.filterSets(chartState.chartSets)
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
            startAxis = rememberStartAxis(
                itemPlacer = verticalItemPlacer(),
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
            bottomAxis = rememberBottomAxis(
                label = null,
                itemPlacer = AxisItemPlacer.Horizontal.default(offset = 0)
            ),
            chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
            autoScaleUp = AutoScaleUp.Full,
            horizontalLayout = HorizontalLayout.FullWidth(),
            model = chartState.chartModel,
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
private fun verticalItemPlacer() = remember { AxisItemPlacer.Vertical.default(maxItemCount = 6) }

@Composable
private fun PressureChart(
    chartState: ChartState<PressureChartSelection, PressureSets>,
    actions: HistoryDataActions
) {
    Text(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .padding(bottom = 4.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        text = stringResource(id = MR.strings.current_pressure.resourceId)
    )

    Row {
        Column(
            Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
        ) {
            PressureChartLegend(
                chartColors = pressureChartColors,
                pressureSets = chartState.chartSets,
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
                selection.maxPressure?.let {
                    PressureEntryLine(
                        value = it,
                        labelId = MR.strings.history_max_pressure.resourceId
                    )
                }
                selection.minPressure?.let {
                    PressureEntryLine(
                        value = it,
                        labelId = MR.strings.history_min_pressure.resourceId
                    )
                }
                selection.trend?.let {
                    PressureEntryLine(
                        value = FloatEntry(0f, it.toFloat()),
                        labelId = MR.strings.history_trend.resourceId
                    )
                }
            }
        }
    }

    val colors = pressureChartColors.filterPressureSets(chartState.chartSets)
    val hpa = stringResource(id = MR.strings.current_hpa.resourceId)
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
            startAxis = rememberStartAxis(
                itemPlacer = verticalItemPlacer(),
                label = rememberStartAxisLabel(),
                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
                valueFormatter = { value, _ ->
                    "$value $hpa"
                }
            ),
            marker = rememberMarker(),
            markerVisibilityChangeListener = object : MarkerVisibilityChangeListener {
                override fun onMarkerShown(
                    marker: Marker,
                    markerEntryModels: List<Marker.EntryModel>
                ) {
                    super.onMarkerShown(marker, markerEntryModels)
                    actions.selectPressurePoints(
                        markerEntryModels.map { it.entry },
                        markerEntryModels.firstOrNull()?.index ?: 0
                    )
                }

                override fun onMarkerHidden(marker: Marker) {
                    super.onMarkerHidden(marker)
                    actions.selectPressurePoints(emptyList(), 0)
                }

                override fun onMarkerMoved(
                    marker: Marker,
                    markerEntryModels: List<Marker.EntryModel>
                ) {
                    super.onMarkerMoved(marker, markerEntryModels)
                    actions.selectPressurePoints(
                        markerEntryModels.map { it.entry },
                        markerEntryModels.firstOrNull()?.index ?: 0
                    )
                }
            },
            bottomAxis = rememberBottomAxis(
                label = null,
                itemPlacer = AxisItemPlacer.Horizontal.default(offset = 0)
            ),
            chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
            autoScaleUp = AutoScaleUp.Full,
            horizontalLayout = HorizontalLayout.FullWidth(),
            model = chartState.chartModel,
        )
    }
    Row(Modifier.padding(horizontal = 4.dp)) {
        Text(
            text = "1000.0 Hpa",
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
private fun HumidityChart(
    chartState: ChartState<HumidityChartSelection, HumidityChartSets>,
    actions: HistoryDataActions
) {
    Text(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .padding(bottom = 4.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        text = stringResource(id = MR.strings.history_humidity.resourceId)
    )
    Row {
        Column(Modifier.weight(1f)) {
            HumidityChartLegend(
                chartColors = humidityChartColors,
                humidityChartSets = chartState.chartSets,
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
                selection.max?.let {
                    HumidityEntryLine(
                        value = it,
                        labelId = MR.strings.history_humidity_max.resourceId
                    )
                }
                selection.avg?.let {
                    HumidityEntryLine(
                        value = it,
                        labelId = MR.strings.history_humidity_avg.resourceId
                    )
                }
                selection.min?.let {
                    HumidityEntryLine(
                        value = it,
                        labelId = MR.strings.history_humidity_min.resourceId
                    )
                }
            }
        }
    }

    val colors = humidityChartColors.filterHumiditySets(chartState.chartSets)
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
            startAxis = rememberStartAxis(
                itemPlacer = verticalItemPlacer(),
                label = rememberStartAxisLabel(),
                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
                valueFormatter = { value, _ ->
                    "$value %"
                }
            ),
            marker = rememberMarker(),
            markerVisibilityChangeListener = object : MarkerVisibilityChangeListener {
                override fun onMarkerShown(
                    marker: Marker,
                    markerEntryModels: List<Marker.EntryModel>
                ) {
                    super.onMarkerShown(marker, markerEntryModels)
                    actions.selectHumidityPoints(
                        markerEntryModels.map { it.entry },
                        markerEntryModels.firstOrNull()?.index ?: 0
                    )
                }

                override fun onMarkerHidden(marker: Marker) {
                    super.onMarkerHidden(marker)
                    actions.selectHumidityPoints(emptyList(), 0)
                }

                override fun onMarkerMoved(
                    marker: Marker,
                    markerEntryModels: List<Marker.EntryModel>
                ) {
                    super.onMarkerMoved(marker, markerEntryModels)
                    actions.selectHumidityPoints(
                        markerEntryModels.map { it.entry },
                        markerEntryModels.firstOrNull()?.index ?: 0
                    )
                }
            },
            bottomAxis = rememberBottomAxis(
                label = null,
                itemPlacer = AxisItemPlacer.Horizontal.default(offset = 0)
            ),
            chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
            autoScaleUp = AutoScaleUp.Full,
            horizontalLayout = HorizontalLayout.FullWidth(),
            model = chartState.chartModel,
        )
    }
    Row(Modifier.padding(horizontal = 4.dp)) {
        Text(
            text = "99 %",
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

@Composable
private fun PressureEntryLine(value: ChartEntry, @StringRes labelId: Int) {
    Text(
        text = "${stringResource(labelId)} : ${value.y}${
            stringResource(
                id = MR.strings.current_hpa.resourceId
            )
        }",
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun HumidityEntryLine(value: ChartEntry, @StringRes labelId: Int) {
    Text(
        text = "${stringResource(labelId)} : ${value.y} %",
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

private fun List<Color>.filterPressureSets(pressureSets: PressureSets): List<Color> {
    return listOfNotNull(
        this[0].takeIf { pressureSets.isMaxAllowed },
        this[1].takeIf { pressureSets.isMinAllowed },
    ).takeUnless { it.isEmpty() } ?: this
}

private fun List<Color>.filterHumiditySets(sets: HumidityChartSets): List<Color> {
    return listOfNotNull(
        this[0].takeIf { sets.isMaxAllowed },
        this[1].takeIf { sets.isAvgAllowed },
        this[2].takeIf { sets.isMinAllowed },
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
private fun HumidityChartLegend(
    humidityChartSets: HumidityChartSets,
    chartColors: List<Color>,
    actions: HistoryDataActions,
) {
    LegendLine(
        label = stringResource(id = MR.strings.history_humidity_max.resourceId),
        color = chartColors.first(),
        checked = humidityChartSets.isMaxAllowed,
        onClick = { actions.selectMaxHumidity(it) },
    )
    LegendLine(
        label = stringResource(id = MR.strings.history_humidity_avg.resourceId),
        color = chartColors[1],
        checked = humidityChartSets.isAvgAllowed,
        onClick = { actions.selectAvgHumidity(it) },
    )
    LegendLine(
        label = stringResource(id = MR.strings.history_humidity_min.resourceId),
        color = chartColors[2],
        checked = humidityChartSets.isMinAllowed,
        onClick = { actions.selectMinHumidity(it) },
    )
}

@Composable
private fun PressureChartLegend(
    pressureSets: PressureSets,
    chartColors: List<Color>,
    actions: HistoryDataActions,
) {
    LegendLine(
        label = stringResource(id = MR.strings.history_max_pressure.resourceId),
        color = chartColors.first(),
        checked = pressureSets.isMaxAllowed,
        onClick = { actions.selectMaxPressureChart(it) },
    )
    LegendLine(
        label = stringResource(id = MR.strings.history_min_pressure.resourceId),
        color = chartColors[1],
        checked = pressureSets.isMinAllowed,
        onClick = { actions.selectMinPressureChart(it) },
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
private fun Overview(tabData: HistoryTabState) {
    Row(
        Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Column(Modifier.weight(1f)) {
            val maxDate = tabData.temperature.maxDate.toFormattedDate().let {
                "${stringResource(MR.strings.history_min_on.resourceId)} $it"
            }
            val maxUVDate = tabData.uv.maxUvDate.toFormattedDate().let {
                "${stringResource(MR.strings.history_min_on.resourceId)} $it"
            }
            val maxWindDate = tabData.maxWindSpeedDate.toFormattedDate().let {
                "${stringResource(MR.strings.history_min_on.resourceId)} $it"
            }
            LabelValueField(
                label = stringResource(id = MR.strings.history_max_temperature.resourceId),
                value = tabData.temperature.maxTemperature.toString()
            )
            LabelField(label = maxDate)
            Spacer(modifier = Modifier.height(8.dp))
            LabelValueField(
                label = stringResource(id = MR.strings.history_high_uv_index.resourceId),
                value = tabData.uv.maxUvIndex.toString()
            )
            LabelField(label = maxUVDate)
            Spacer(modifier = Modifier.height(8.dp))
            LabelValueFieldWithUnits(
                label = stringResource(id = MR.strings.history_high_wind.resourceId),
                value = tabData.maxWindSpeed.toString(),
                units = stringResource(id = MR.strings.current_km_h.resourceId),
            )
            LabelField(label = maxWindDate)
        }
        Column(Modifier.weight(1f)) {
            val minDate = tabData.temperature.minDate.toFormattedDate().let {
                "${stringResource(MR.strings.history_min_on.resourceId)} $it"
            }
            val maxRadiationDate = tabData.uv.maxRadiationDate.toFormattedDate().let {
                "${stringResource(MR.strings.history_min_on.resourceId)} $it"
            }
            LabelValueField(
                label = stringResource(id = MR.strings.history_min_temperature.resourceId),
                value = tabData.temperature.minTemperature.toString()
            )
            LabelField(label = minDate)
            Spacer(modifier = Modifier.height(8.dp))
            LabelValueFieldWithUnits(
                label = stringResource(id = MR.strings.history_max_solar_radiation.resourceId),
                value = tabData.uv.maxRadiation.toString(),
                units = stringResource(id = MR.strings.current_radiation_units.resourceId),
            )
            LabelField(label = maxRadiationDate)
            Spacer(modifier = Modifier.height(8.dp))
            LabelValueFieldWithUnits(
                label = stringResource(id = MR.strings.history_high_presc_total.resourceId),
                value = tabData.prescriptionForPeriod.toFormattedString(),
                units = stringResource(id = MR.strings.current_mm.resourceId),
            )
        }
    }
}

private fun Double.toFormattedString(): String {
    return String.format("%.2f", this)
}

@Composable
private fun IntervalInfo(tabData: HistoryTabState, tab: HistoryTab, actions: HistoryDataActions) {
    Row {
        Text(
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            text = when (tab) {
                HistoryTab.YESTERDAY -> "${tabData.startDate.toLocalizedLongDayName()}, ${tabData.startDate.toFormattedDate()}"
                HistoryTab.WEEK -> "${tabData.startDate.toLocalizedShortDayName()}, ${tabData.startDate.toFormattedShortDate()} - ${
                    tabData.startDate.plus(
                        6,
                        DateTimeUnit.DAY
                    ).toLocalizedShortDayName()
                }, ${tabData.startDate.plus(6, DateTimeUnit.DAY).toFormattedShortDate()}"

                HistoryTab.MONTH -> "${tabData.startDate.toLocalizedMontName()} ${tabData.startDate.year}"
            }
        )

        if (tab != HistoryTab.YESTERDAY) {
            val actionNext =
                if (tab == HistoryTab.MONTH) actions::selectNextMonth else actions::selectNextWeek
            val actionPrev =
                if (tab == HistoryTab.MONTH) actions::selectPreviousMonth else actions::selectPreviousWeek
            val timeUnit = if (tab == HistoryTab.MONTH) DateTimeUnit.MONTH else DateTimeUnit.WEEK

            IconButton(onClick = actionPrev) {
                Icon(
                    modifier = Modifier.rotate(180f),
                    painter = painterResource(id = R.drawable.baseline_navigate_next_24),
                    contentDescription = stringResource(id = MR.strings.history_previous_month.resourceId)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                enabled = tabData.startDate.plus(1, timeUnit) <= Clock.System.now()
                    .toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    ).date,
                onClick = actionNext
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_navigate_next_24),
                    contentDescription = stringResource(id = MR.strings.history_next_month.resourceId)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
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
                    HistoryTab.MONTH to History(
                        LocalDate.parse("2023-01-01"),
                        LocalDate.parse("2023-01-31"),
                        listOf(observation)
                    ).toTabData(
                        TempChartSets(),
                        PressureSets(),
                        HumidityChartSets(),
                        HistoryTab.MONTH,
                    )
                ),
            ),
            actions = ActionsInvocationHandler.createActionsProxy(),
        )
    }
}


@Preview
@Composable
fun HistoryDataScreenEmptyPreview() {
    MaterialTheme {
        ScreenContent(
            state =
            HistoryDataState(
                selectedTab = HistoryTab.MONTH,
                tabData = emptyMap(),
            ),
            actions = ActionsInvocationHandler.createActionsProxy(),
        )
    }
}

private val tempChartColors
    @Composable
    get() = listOf(
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.primary,
    )

private val humidityChartColors
    @Composable
    get() = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
    )

private val pressureChartColors
    @Composable
    get() = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary
    )
