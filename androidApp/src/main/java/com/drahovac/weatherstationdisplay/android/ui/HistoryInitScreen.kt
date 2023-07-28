package com.drahovac.weatherstationdisplay.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.android.R
import com.drahovac.weatherstationdisplay.android.theme.WeatherTheme
import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.domain.fromUTCEpochMillis

import com.drahovac.weatherstationdisplay.domain.toCurrentUTCMillis
import com.drahovac.weatherstationdisplay.domain.toFormattedDate
import com.drahovac.weatherstationdisplay.viewmodel.HistoryActions
import com.drahovac.weatherstationdisplay.viewmodel.HistoryState
import com.drahovac.weatherstationdisplay.viewmodel.HistoryInitViewModel
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.getViewModel
import java.time.ZoneOffset

@Composable
fun HistoryInitScreen(viewModel: HistoryInitViewModel = getViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.noData?.isPickerVisible == true) {
        DateDialog(
            startDate = state.noData?.startDate,
            actions = viewModel
        )
    }

    when {
        state.isLoading -> ProgressIndicator()
        state.noData != null && state.noData?.networkError == null -> NoHistoryScreenContent(
            state,
            viewModel
        )

        state.noData != null && state.noData?.networkError != null -> ErrorScreen(
            error = state.noData!!.networkError!!,
            onNewDeviceId = viewModel::onNewDeviceId,
            onNewApiKey = viewModel::onNewApiKey,
        )

        else -> {
            HistoryDataScreen()
        }
    }
}

@Composable
private fun NoHistoryScreenContent(
    state: HistoryState,
    actions: HistoryActions
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.displaySmall,
            text = stringResource(id = MR.strings.history_nav.resourceId),
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(id = MR.strings.history_no_data.resourceId),
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.bodyMedium,
            text = stringResource(id = MR.strings.history_no_data_message.resourceId),
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = actions::switchDateDialog,
                ),
            readOnly = true,
            label = {
                Text(text = stringResource(id = MR.strings.history_start_date.resourceId))
            },
            value = state.noData?.startDate?.toFormattedDate().orEmpty(),
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            onValueChange = {},
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                    contentDescription = null
                )
            },
            isError = false
        )

        Text(
            text = state.noData?.error?.let { stringResource(id = it.resourceId) } ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error,
        )

        OutlinedButton(
            modifier = Modifier.padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            onClick = actions::downloadInitialHistory,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_file_download_24),
                contentDescription = null
            )
            Spacer(modifier = Modifier.widthIn(8.dp))
            Text(text = stringResource(id = MR.strings.history_start_download.resourceId))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateDialog(
    startDate: LocalDate?,
    actions: HistoryActions
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startDate?.toCurrentUTCMillis(),

        )

    DatePickerDialog(
        onDismissRequest = actions::switchDateDialog,
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        actions.selectStartDate(
                            LocalDate.fromUTCEpochMillis(it)
                        )
                    }
                }) {
                Text(text = stringResource(id = MR.strings.history_start_select.resourceId))
            }
        }) {
        Column(
            Modifier
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            DatePicker(
                state = datePickerState,
                dateValidator = { date ->
                    date <= java.time.LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
                }
            )
        }
    }
}

@Preview
@Composable
fun HistoryScreenPreview() {
    WeatherTheme {
        NoHistoryScreenContent(
            state = HistoryState(),
            actions = ActionsInvocationHandler.createActionsProxy()
        )
    }
}

@Preview
@Composable
fun HistoryScreenDateDialogPreview() {
    WeatherTheme {
        DateDialog(
            startDate = null,
            actions = ActionsInvocationHandler.createActionsProxy()
        )
    }
}

