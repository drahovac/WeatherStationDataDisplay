package com.drahovac.weatherstationdisplay.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.android.R
import com.drahovac.weatherstationdisplay.viewmodel.SetupDeviceIdActions
import com.drahovac.weatherstationdisplay.viewmodel.SetupDeviceIdState
import com.drahovac.weatherstationdisplay.viewmodel.SetupDeviceIdViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun SetupDeviceIdScreen(
    navController: NavController,
    viewModel: SetupDeviceIdViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dest by viewModel.navigationFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current

    dest.navigateSingle(navController)

    ScreenContent(
        state,
        viewModel,
        {
            context.openLinkInBrowser(DEVICE_ID_LINK)
        }
    ) {
        viewModel.saveDeviceId()
    }
}

@Composable
private fun ScreenContent(
    state: SetupDeviceIdState,
    actions: SetupDeviceIdActions,
    openInBrowser: () -> Unit,
    submit: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = MR.strings.setup_welcome.resourceId),
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = stringResource(id = MR.strings.setup_enter_device_id.resourceId),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally),
                label = {
                    Text(text = stringResource(id = MR.strings.setup_id_input_label.resourceId))
                },
                value = state.id.orEmpty(),
                colors = TextFieldDefaults.colors(),
                onValueChange = actions::setDeviceId,
                isError = state.error != null
            )

            state.error?.let {
                Text(
                    text = stringResource(id = it),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Text(
                text = stringResource(id = MR.strings.setup_id_guid.resourceId),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            OutlinedButton(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                onClick = openInBrowser
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_language_24),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.widthIn(8.dp))
                Text(text = stringResource(id = MR.strings.setup_open_browser.resourceId))
            }
        }
        FloatingActionButton(
            onClick = submit,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(BottomEnd)
                .padding(32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                contentDescription = stringResource(id = MR.strings.setup_continue.resourceId)
            )
        }
    }
}

private const val DEVICE_ID_LINK = "https://www.wunderground.com/member/devices"

@Preview
@Composable
fun SetupDeviceIdScreenPreview() {
    ScreenContent(
        state = SetupDeviceIdState("DeviceId"),
        actions = ActionsInvocationHandler.createActionsProxy(),
        {}) {}
}

