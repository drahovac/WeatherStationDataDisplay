package com.drahovac.weatherstationdisplay.android.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.drahovac.weatherstationdisplay.domain.Destination

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
