package com.drahovac.weatherstationdisplay.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.drahovac.weatherstationdisplay.android.theme.WeatherTheme
import com.drahovac.weatherstationdisplay.android.ui.SetupApiKeyScreen
import com.drahovac.weatherstationdisplay.android.ui.SetupDeviceIdScreen
import com.drahovac.weatherstationdisplay.domain.Destination
import com.drahovac.weatherstationdisplay.viewmodel.InitialDestinationViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {

    private val viewModel: InitialDestinationViewModel by inject()

    private val showSplash: Boolean
        get() {
            return initialDestination.value == null
        }

    private var initialDestination: MutableState<Destination?> = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition(::showSplash)
        super.onCreate(savedInstanceState)

        setContent {
            initialDestination = remember { mutableStateOf(null) }
            FetchInitialDestination()
            initialDestination.value?.let {
                MainContent()
            }
        }
    }

    @Composable
    private fun MainContent() {
        val navController = rememberNavController()

        WeatherTheme {
            Scaffold(
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center,
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    MainContent(Destination.SetupDeviceId, navController)
                }
            }
        }
    }

    @Composable
    private fun FetchInitialDestination() {
        LaunchedEffect(key1 = Unit) {
            initialDestination.value = viewModel.getInitialDestination()
        }
    }
}

@Composable
private fun MainContent(
    nextDestination: Destination?,
    navController: NavHostController
) {
    nextDestination?.let { dest ->
        NavHost(
            navController = navController,
            startDestination = dest.route()
        ) {
            composable(Destination.SetupDeviceId.route()) {
                SetupDeviceIdScreen(navController)
            }

            composable(Destination.SetupApiKey.route()) {
                SetupApiKeyScreen()
            }

            composable(Destination.CurrentWeather.route()) {
                // TODO
                Text("Destination.CurrentWeather")
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    WeatherTheme {
        MainContent(
            nextDestination = Destination.SetupApiKey,
            navController = rememberNavController()
        )
    }
}
