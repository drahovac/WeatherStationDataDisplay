package com.drahovac.weatherstationdisplay.viewmodel

import com.patrykandpatrick.vico.core.chart.composed.ComposedChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.extension.floor
import com.patrykandpatrick.vico.core.extension.round
import kotlinx.datetime.LocalDate
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

actual class ChartModel(
    models: List<LineChartEntryModel>,
    defaultDaysCount: Float,
    minY: Float = 0f,
    maxY: Float = 25f
) :
    ComposedChartEntryModel<LineChartEntryModel> {
    override val composedEntryCollections: List<LineChartEntryModel> = models
    override val entries: List<List<ChartEntry>> = models.map { it.entries.first() }
    override val minX: Float = 0f
    override val maxX: Float = defaultDaysCount
    override val minY: Float =
        models.minOfOrNull { entry -> entry.minY.roundToInt().let { it - 5 - (it % 5) }.toFloat() }
            ?: minY
    override val maxY: Float =
        models.maxOfOrNull { entry -> entry.maxY.roundToInt().let { it + 5 - (it % 5) }.toFloat() }
            ?: maxY
    override val stackedPositiveY: Float = models.maxOfOrNull { it.stackedPositiveY } ?: 1f
    override val stackedNegativeY: Float = models.minOfOrNull { it.stackedNegativeY } ?: 0f
    override val xGcd: Float = models.fold<LineChartEntryModel, Float?>(null) { gcd, model ->
        gcd?.gcdWith(model.xGcd) ?: model.xGcd
    } ?: 1f
    override val id: Int = models.map { it.id }.hashCode()
}

actual typealias ChartPointEntry = ChartEntry

class LineChartEntryModel(
    chartEntries: List<ChartEntry>
) : ChartEntryModel {
    override val entries: List<List<ChartEntry>> = listOf(chartEntries)
    override val minX: Float = chartEntries.minOfOrNull { it.x } ?: 0f
    override val maxX: Float = chartEntries.maxOfOrNull { it.x } ?: 0f
    override val minY: Float = chartEntries.minOfOrNull { it.y } ?: 0f
    override val maxY: Float = chartEntries.maxOfOrNull { it.y } ?: 0f
    override val stackedPositiveY: Float = chartEntries.maxOfOrNull { it.y + 5 } ?: 0f
    override val stackedNegativeY: Float = chartEntries.minOfOrNull { it.y - 5 } ?: 0f
    override val xGcd: Float = listOf(chartEntries).calculateXGcd()
    override val id: Int = chartEntries.map { it.x + it.y }.hashCode()
}

actual fun List<List<Pair<LocalDate, Double>>>.toChartModel(
    defaultDaysCount: Float,
    minY: Float,
    maxY: Float,
    xOffset: Float
): ChartModel {
    val models = this.map {
        var firstDate = xOffset
        LineChartEntryModel(it.map { pair ->
            val date = pair.first.toEpochDays().toFloat()
            if (firstDate == xOffset) firstDate = date
            FloatEntry(
                date - firstDate + xOffset,
                pair.second.toFloat()
            )
        })
    }

    return ChartModel(models, defaultDaysCount, minY, maxY)
}

internal fun Iterable<Iterable<ChartEntry>>.calculateXGcd(): Float {
    var gcd: Float? = null
    forEach { entryCollection ->
        val iterator = entryCollection.iterator()
        var currentEntry: ChartEntry
        var previousEntry: ChartEntry? = null
        while (iterator.hasNext()) {
            currentEntry = iterator.next()
            previousEntry?.let { prevEntry ->
                val difference = abs(x = currentEntry.x - prevEntry.x)
                gcd = gcd?.gcdWith(other = difference) ?: difference
            }
            previousEntry = currentEntry
        }
        if (gcd == -1f) gcd = 1f
    }
    return gcd ?: 1f
}

internal fun Float.gcdWith(other: Float): Float = gcdWithImpl(
    other = other,
    threshold = 10f.pow(n = -FLOAT_GCD_DECIMALS - 1),
).round(decimals = FLOAT_GCD_DECIMALS)

private fun Float.gcdWithImpl(other: Float, threshold: Float): Float = when {
    this < other -> other.gcdWithImpl(other = this, threshold = threshold)
    abs(x = other) < threshold -> this
    else -> other.gcdWithImpl(other = this - (this / other).floor * other, threshold = threshold)
}

private fun Float.round(decimals: Int): Float {
    val multiplier = 10f.pow(n = decimals)
    return (this * multiplier).round / multiplier
}

private const val FLOAT_GCD_DECIMALS = 3
