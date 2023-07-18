package com.drahovac.weatherstationdisplay.android.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.drahovac.weatherstationdisplay.domain.Destination
import com.drahovac.weatherstationdisplay.viewmodel.ExactlyOnceEventBus

fun ExactlyOnceEventBus<Destination>.navigateSingle(navController: NavController) {
    receive()?.let {
        navController.navigateSingle(it)
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
