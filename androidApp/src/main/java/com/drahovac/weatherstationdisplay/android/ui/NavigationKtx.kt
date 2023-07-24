package com.drahovac.weatherstationdisplay.android.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.drahovac.weatherstationdisplay.domain.Destination
import com.drahovac.weatherstationdisplay.viewmodel.ExactlyOnceEventBus

fun ExactlyOnceEventBus<Destination>.navigateSingle(navController: NavController) {
    receive()?.let {
        navController.navigateSingle(it)
    }
}

fun ExactlyOnceEventBus<Destination>.popUp(navController: NavController) {
    receive()?.let {
        navController.navigate(it.route()) {
            popUpTo(navController.graph.id)
        }
    }
}

fun NavController.navigateSingle(destination: Destination) {
    navigate(destination.route()) {
        launchSingleTop = true
    }
}

fun Context.openLinkInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    startActivity(intent)
}
