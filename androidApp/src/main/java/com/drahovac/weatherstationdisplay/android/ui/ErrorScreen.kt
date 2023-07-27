package com.drahovac.weatherstationdisplay.android.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.android.R
import com.drahovac.weatherstationdisplay.android.theme.WeatherTheme
import com.drahovac.weatherstationdisplay.domain.NetworkError

// TODO retry
@Composable
fun ErrorScreen(
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
                .align(Alignment.CenterHorizontally)
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
private fun getErrorText(error: NetworkError): String {
    return when (error) {
        is NetworkError.InvalidApiKey -> stringResource(id = MR.strings.current_error_new_api_key_message.resourceId)
        is NetworkError.InvalidDeviceId -> stringResource(id = MR.strings.current_error_station_id_message.resourceId)
        is NetworkError.TooManyRequests -> stringResource(id = MR.strings.current_error_quota.resourceId)
        else -> error.message.orEmpty()
    }
}

@Preview
@Composable
fun ErrorScreenPreview() {
    WeatherTheme {
        ErrorScreen(error = NetworkError.InvalidApiKey, {}, {})
    }
}
