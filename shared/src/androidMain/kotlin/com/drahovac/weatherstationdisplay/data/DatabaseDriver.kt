package com.drahovac.weatherstationdisplay.data

import android.content.Context
import com.drahovac.weatherstationdisplay.AppDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriver(
    private val applicationContext: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, applicationContext, "data.db")
    }
}
