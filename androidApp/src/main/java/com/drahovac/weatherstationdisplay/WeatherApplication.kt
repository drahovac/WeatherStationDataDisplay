package com.drahovac.weatherstationdisplay

import android.app.Application
import com.drahovac.weatherstationdisplay.di.initApplication
import org.koin.android.ext.koin.androidContext

class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initApplication(isProd = !BuildConfig.DEBUG) {
            androidContext(this@WeatherApplication)
        }
    }
}