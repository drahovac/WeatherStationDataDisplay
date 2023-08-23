package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.AppDatabase
import com.drahovac.weatherstationdisplay.Weather_data
import com.drahovac.weatherstationdisplay.domain.HistoryMetric
import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.domain.fromUTCEpochMillis
import com.drahovac.weatherstationdisplay.domain.orZero
import com.drahovac.weatherstationdisplay.domain.toCurrentUTCMillisEndOFDay
import com.drahovac.weatherstationdisplay.domain.toCurrentUTCMillisStartOFDay
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
                        solarRadiationHigh = observation.solarRadiationHigh.orZero,
                        uvHigh = observation.uvHigh.orZero,
                        winddirAvg = observation.winddirAvg.orZero,
                        humidityHigh = observation.humidityHigh.orZero,
                        humidityLow = observation.humidityLow.orZero,
                        humidityAvg = observation.humidityAvg.orZero,
                        qcStatus = observation.qcStatus.toLong(),
                        metricTempHigh = observation.metric.tempHigh.orZero,
                        metricTempLow = observation.metric.tempLow.orZero,
                        metricTempAvg = observation.metric.tempAvg.orZero,
                        metricWindspeedHigh = observation.metric.windspeedHigh.orZero,
                        metricWindspeedLow = observation.metric.windspeedLow.orZero,
                        metricWindspeedAvg = observation.metric.windspeedAvg.orZero,
                        metricWindgustHigh = observation.metric.windgustHigh.orZero,
                        metricWindgustLow = observation.metric.windgustLow.orZero,
                        metricWindgustAvg = observation.metric.windgustAvg.orZero,
                        metricDewpointHigh = observation.metric.dewptHigh.orZero,
                        metricDewpointLow = observation.metric.dewptLow.orZero,
                        metricDewpointAvg = observation.metric.dewptAvg.orZero,
                        metricWindchillHigh = observation.metric.windchillHigh.orZero,
                        metricWindchillLow = observation.metric.windchillLow.orZero,
                        metricWindchillAvg = observation.metric.windchillAvg.orZero,
                        metricHeatindexHigh = observation.metric.heatindexHigh.orZero,
                        metricHeatindexLow = observation.metric.heatindexLow.orZero,
                        metricHeatindexAvg = observation.metric.heatindexAvg.orZero,
                        metricPressureMax = observation.metric.pressureMax.orZero,
                        metricPressureMin = observation.metric.pressureMin.orZero,
                        metricPressureTrend = observation.metric.pressureTrend.orZero,
                        metricPrecipRate = observation.metric.precipRate.orZero,
                        metricPrecipTotal = observation.metric.precipTotal.orZero,
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
                startDate = startDate.toCurrentUTCMillisStartOFDay(),
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

