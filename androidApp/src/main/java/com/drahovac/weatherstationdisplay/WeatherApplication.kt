package com.drahovac.weatherstationdisplay

import android.app.Application
import com.drahovac.weatherstationdisplay.di.initKoin
import org.koin.android.ext.koin.androidContext

class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@WeatherApplication)
        }
    }
}