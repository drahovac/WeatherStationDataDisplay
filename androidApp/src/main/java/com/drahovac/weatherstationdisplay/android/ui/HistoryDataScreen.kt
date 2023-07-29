package com.drahovac.weatherstationdisplay.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.android.ui.component.LabelField
import com.drahovac.weatherstationdisplay.android.ui.component.LabelValueField
import com.drahovac.weatherstationdisplay.domain.toFormattedDate
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataActions
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataState
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataTab
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataViewModel
import com.drahovac.weatherstationdisplay.viewmodel.HistoryTabData
import kotlinx.datetime.LocalDate
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

        Row(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
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
    MaterialTheme {
        ScreenContent(
            state =
            HistoryDataState(
                selectedTab = HistoryDataTab.MONTH,
                tabData = mapOf(
                    HistoryDataTab.MONTH to HistoryTabData(
                        maxTemperature = 22.9,
                        minTemperature = 13.9,
                        minDate = LocalDate.parse("2023-05-13"),
                        maxDate = LocalDate.parse("2023-05-13"),
                    )
                )
            ),
            actions = ActionsInvocationHandler.createActionsProxy(),
        )
    }
}