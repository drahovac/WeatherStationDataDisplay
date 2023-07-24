package com.drahovac.weatherstationdisplay.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.viewmodel.SetupActions
import com.drahovac.weatherstationdisplay.viewmodel.SetupAipKeyViewModel
import com.drahovac.weatherstationdisplay.viewmodel.SetupState
import org.koin.androidx.compose.getViewModel

@Composable
fun SetupApiKeyScreen(
    navController: NavController,
    viewModel: SetupAipKeyViewModel = getViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dest by viewModel.navigationFlow.collectAsStateWithLifecycle()

    dest.popUp(navController)

    ScreenContent(
        state,
        viewModel
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
        title = MR.strings.setup_finalize.resourceId,
        inputCaption = MR.strings.setup_enter_api_key.resourceId,
        inputLabel = MR.strings.setup_api_key.resourceId,
        guide = MR.strings.setup_api_key_guide.resourceId,
        urlLink = API_KEY_LINK,
        submit = submit
    )
}

private const val API_KEY_LINK = "https://www.wunderground.com/member/api-keys"

@Preview
@Composable
fun SetupApiKeyScreenPreview() {
    ScreenContent(
        state = SetupState("ApiKEy"),
        actions = ActionsInvocationHandler.createActionsProxy(),
        submit = {})
}
