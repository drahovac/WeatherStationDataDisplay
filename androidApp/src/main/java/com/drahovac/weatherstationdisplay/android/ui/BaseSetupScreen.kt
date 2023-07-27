package com.drahovac.weatherstationdisplay.android.ui

import androidx.annotation.StringRes
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.android.R
import com.drahovac.weatherstationdisplay.viewmodel.SetupActions
import com.drahovac.weatherstationdisplay.viewmodel.SetupState

@Composable
fun BaseSetupScreen(
    state: SetupState,
    urlLink: String,
    @StringRes title: Int,
    @StringRes inputCaption: Int,
    @StringRes inputLabel: Int,
    @StringRes guide: Int,
    actions: SetupActions,
    submit: () -> Unit,
) {
    val context = LocalContext.current
    val openInBrowser = {
        context.openLinkInBrowser(urlLink)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = stringResource(id = inputCaption),
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
                    .align(Alignment.CenterHorizontally),
                label = {
                    Text(text = stringResource(id = inputLabel))
                },
                value = state.value.orEmpty(),
                colors = TextFieldDefaults.colors(),
                onValueChange = actions::setValue,
                isError = state.error != null
            )

            state.error?.let {
                Text(
                    text = stringResource(id = it.resourceId),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Text(
                text = stringResource(id = guide),
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
                .align(Alignment.BottomEnd)
                .padding(32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                contentDescription = stringResource(id = MR.strings.setup_continue.resourceId)
            )
        }
    }
}