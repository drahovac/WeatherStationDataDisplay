package com.drahovac.weatherstationdisplay.viewmodel

import kotlinx.datetime.LocalDate

actual class ChartModel

actual interface ChartPointEntry

actual fun List<List<Pair<LocalDate, Double>>>.toChartModel(
    defaultDaysCount: Float, minY: Float,
    maxY: Float,
    xOffset: Float,
): ChartModel {
    return ChartModel()
}
