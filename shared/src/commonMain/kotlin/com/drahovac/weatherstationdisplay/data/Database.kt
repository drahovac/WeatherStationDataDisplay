package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.AppDatabase
import com.drahovac.weatherstationdisplay.Weather_data
import com.drahovac.weatherstationdisplay.domain.HistoryMetric
import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.domain.fromUTCEpochMillis
import com.drahovac.weatherstationdisplay.domain.toCurrentUTCMillisEndOFDay
import com.drahovac.weatherstationdisplay.domain.toEpochDays
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class Database(databaseDriverFactory: DatabaseDriver) {
    private val dbDriver = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = dbDriver.appDatabaseQueries

    suspend fun insertHistoryObservations(observations: List<HistoryObservation>) {
        withContext(Dispatchers.IO) {
            dbQuery.transaction {
                observations.forEach { observation ->
                    dbQuery.insertHistory(
                        stationID = observation.stationID,
                        tz = observation.tz,
                        obsTimeUtc = observation.obsTimeUtc.toEpochMilliseconds(),
                        obsDayUtc = observation.obsTimeUtc.toEpochDays(),
                        obsTimeLocal = observation.dateTimeLocal.toString(),
                        epoch = observation.epoch,
                        lat = observation.lat,
                        lon = observation.lon,
                        solarRadiationHigh = observation.solarRadiationHigh,
                        uvHigh = observation.uvHigh,
                        winddirAvg = observation.winddirAvg,
                        humidityHigh = observation.humidityHigh,
                        humidityLow = observation.humidityLow,
                        humidityAvg = observation.humidityAvg,
                        qcStatus = observation.qcStatus.toLong(),
                        metricTempHigh = observation.metric.tempHigh,
                        metricTempLow = observation.metric.tempLow,
                        metricTempAvg = observation.metric.tempAvg,
                        metricWindspeedHigh = observation.metric.windspeedHigh,
                        metricWindspeedLow = observation.metric.windspeedLow,
                        metricWindspeedAvg = observation.metric.windspeedAvg,
                        metricWindgustHigh = observation.metric.windgustHigh,
                        metricWindgustLow = observation.metric.windgustLow,
                        metricWindgustAvg = observation.metric.windgustAvg,
                        metricDewpointHigh = observation.metric.dewptHigh,
                        metricDewpointLow = observation.metric.dewptLow,
                        metricDewpointAvg = observation.metric.dewptAvg,
                        metricWindchillHigh = observation.metric.windchillHigh,
                        metricWindchillLow = observation.metric.windchillLow,
                        metricWindchillAvg = observation.metric.windchillAvg,
                        metricHeatindexHigh = observation.metric.heatindexHigh,
                        metricHeatindexLow = observation.metric.heatindexLow,
                        metricHeatindexAvg = observation.metric.heatindexAvg,
                        metricPressureMax = observation.metric.pressureMax,
                        metricPressureMin = observation.metric.pressureMin,
                        metricPressureTrend = observation.metric.pressureTrend,
                        metricPrecipRate = observation.metric.precipRate,
                        metricPrecipTotal = observation.metric.precipTotal,
                    )
                }
            }
        }
    }

    suspend fun selectHistory(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<HistoryObservation> {
        return withContext(Dispatchers.IO) {
            dbQuery.selectHistoryByDate(
                startDate = startDate.toCurrentUTCMillisEndOFDay(),
                endDate = endDate.toCurrentUTCMillisEndOFDay()
            ).executeAsList().map {
                it.toDomain()
            }
        }
    }

    suspend fun selectHistory(
        itemCount: Int,
    ): List<HistoryObservation> {
        return withContext(Dispatchers.IO) {
            dbQuery.selectHistoryByLimit(itemCount.toLong()).executeAsList().map {
                it.toDomain()
            }
        }
    }

    fun hasData(): Flow<Boolean> {
        return dbQuery.hasData().asFlow().map { it.executeAsOne() }
    }

    suspend fun selectNewestHistoryDate(): LocalDate? {
        return withContext(Dispatchers.IO) {
            dbQuery.newestHistoryDate().executeAsOneOrNull()?.let {
                LocalDate.fromUTCEpochMillis(it)
            }
        }
    }
}

private fun Weather_data.toDomain(): HistoryObservation {
    return HistoryObservation(
        stationID = station_id,
        metric = HistoryMetric(
            tempHigh = metric_temp_high,
            tempLow = metric_temp_low,
            tempAvg = metric_temp_avg,
            windspeedHigh = metric_windspeed_high,
            windspeedLow = metric_windspeed_low,
            windspeedAvg = metric_windspeed_avg,
            windgustHigh = metric_windgust_high,
            windgustLow = metric_windgust_low,
            windgustAvg = metric_windgust_avg,
            dewptHigh = metric_dewpt_high,
            dewptLow = metric_dewpt_low,
            dewptAvg = metric_dewpt_avg,
            windchillHigh = metric_windchill_high,
            windchillLow = metric_windchill_low,
            windchillAvg = metric_windchill_avg,
            heatindexHigh = metric_heatindex_high,
            heatindexLow = metric_heatindex_low,
            heatindexAvg = metric_heatindex_avg,
            pressureMax = metric_pressure_max,
            pressureMin = metric_pressure_min,
            pressureTrend = metric_pressure_trend,
            precipRate = metric_precip_rate,
            precipTotal = metric_precip_total
        ),
        tz = tz,
        obsTimeUtc = Instant.fromEpochMilliseconds(obs_time_utc),
        obsTimeLocal = this.obs_time_local,
        epoch = epoch,
        lat = lat,
        lon = lon,
        solarRadiationHigh = solar_radiation_high,
        uvHigh = uv_high,
        winddirAvg = winddir_avg,
        humidityHigh = humidity_high,
        humidityLow = humidity_low,
        humidityAvg = humidity_avg,
        qcStatus = qc_status.toInt()
    )
}

