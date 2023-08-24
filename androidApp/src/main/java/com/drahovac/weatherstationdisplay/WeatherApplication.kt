package com.drahovac.weatherstationdisplay

import android.app.Application
import android.content.pm.ApplicationInfo
import com.drahovac.weatherstationdisplay.di.initApplication
import org.koin.android.ext.koin.androidContext

class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val isDebuggable = 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
        initApplication(isProd = !isDebuggable) {
            androidContext(this@WeatherApplication)
        }
    }
}