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
import androidx.compose.runtime.Composable
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

class MainActivity : ComponentActivity() {

    private val showSplash: Boolean
        get() = false // TODO check credentials present

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition(::showSplash)
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            WeatherTheme {
                Scaffold(
                    isFloatingActionButtonDocked = true,
                    floatingActionButtonPosition = FabPosition.Center,
                    floatingActionButton = {
                        /**
                        if (isBottomNavigationVisible(navController = navController)) {
                        FloatingActionButton(
                        shape = CircleShape,
                        contentColor = MaterialTheme.colors.primary,
                        onClick = { navController.navigateSingleTop(Destination.CreateMedicine) }) {
                        Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(
                        id = MR.strings.create_medicine_title.resourceId
                        )
                        )
                        }
                        }
                         */
                    },
                    bottomBar = {
                        //BottomNavigation(navController)
                    }
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
