package com.drahovac.weatherstationdisplay.data

import com.squareup.sqldelight.db.SqlDriver

expect class DatabaseDriver {
    fun createDriver(): SqlDriver
}
