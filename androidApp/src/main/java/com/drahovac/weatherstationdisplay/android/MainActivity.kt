package com.drahovac.weatherstationdisplay.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.android.theme.WeatherTheme
import com.drahovac.weatherstationdisplay.android.ui.CurrentWeatherScreen
import com.drahovac.weatherstationdisplay.android.ui.HistoryInitScreen
import com.drahovac.weatherstationdisplay.android.ui.SetupApiKeyScreen
import com.drahovac.weatherstationdisplay.android.ui.SetupDeviceIdScreen
import com.drahovac.weatherstationdisplay.android.ui.popCurrent
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
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            initialDestination = remember { mutableStateOf(null) }
            FetchInitialDestination()
            initialDestination.value?.let {
                MainContent(it)
            }
        }
    }

    @Composable
    private fun MainContent(destination: Destination) {
        val navController = rememberNavController()

        WeatherTheme {
            Scaffold(
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center,
                bottomBar = {
                    BottomNavigation(navController)
                },
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    MainContent(destination, navController)
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
            modifier = Modifier.safeDrawingPadding(),
            navController = navController,
            startDestination = dest.route()
        ) {
            composable(Destination.SetupDeviceId.route()) {
                SetupDeviceIdScreen(navController)
            }

            composable(Destination.SetupApiKey.route()) {
                SetupApiKeyScreen(navController)
            }

            composable(Destination.CurrentWeather.route()) {
                CurrentWeatherScreen(navController)
            }

            composable(Destination.History.route()) {
                HistoryInitScreen()
            }
        }
    }
}

@Composable
private fun BottomNavigation(navController: NavHostController) {
    val destination = remember(navController.currentBackStackEntryAsState().value) {
        navController.currentBackStackEntry?.destination?.route?.destination()
    }

    if (isBottomNavigationVisible(destination)) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
        ) {
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_thermostat_24),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(id = MR.strings.current_nav.resourceId)) },
                selected = destination == Destination.CurrentWeather,
                onClick = {
                    navController.popCurrent(Destination.CurrentWeather)
                })
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(id = MR.strings.history_nav.resourceId)) },
                selected = destination == Destination.History,
                onClick = {
                    navController.popCurrent(Destination.History)
                })
        }
    }
}

private fun String?.destination(): Destination? {
    return Destination.values().find { it.route() == this }
}

fun isBottomNavigationVisible(destination: Destination?): Boolean {
    return destination != null && destination != Destination.SetupApiKey && destination != Destination.SetupDeviceId
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
