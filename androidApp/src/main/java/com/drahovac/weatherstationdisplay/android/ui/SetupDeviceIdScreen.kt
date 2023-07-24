package com.drahovac.weatherstationdisplay.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.viewmodel.SetupActions
import com.drahovac.weatherstationdisplay.viewmodel.SetupDeviceIdViewModel
import com.drahovac.weatherstationdisplay.viewmodel.SetupState
import org.koin.androidx.compose.getViewModel

@Composable
fun SetupDeviceIdScreen(
    navController: NavController,
    viewModel: SetupDeviceIdViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dest by viewModel.navigationFlow.collectAsStateWithLifecycle()

    dest.popUp(navController)

    ScreenContent(
        state,
        viewModel,
    ) {
        viewModel.saveValue()
    }
}

@Composable
private fun ScreenContent(
    state: SetupState,
    actions: SetupActions,
    submit: () -> Unit,
) {
    BaseSetupScreen(
        state = state,
        actions = actions,
        title = MR.strings.setup_welcome.resourceId,
        inputCaption = MR.strings.setup_enter_device_id.resourceId,
        inputLabel = MR.strings.setup_id_input_label.resourceId,
        guide = MR.strings.setup_id_guid.resourceId,
        urlLink = DEVICE_ID_LINK,
        submit = submit
    )
}

private const val DEVICE_ID_LINK = "https://www.wunderground.com/member/devices"

@Preview
@Composable
fun SetupDeviceIdScreenPreview() {
    ScreenContent(
        state = SetupState("DeviceId"),
        actions = ActionsInvocationHandler.createActionsProxy(),
        submit = {})
}

