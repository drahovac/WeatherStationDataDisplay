package com.drahovac.weatherstationdisplay.android.ui.component

import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.dimensions.HorizontalDimensions
import com.patrykandpatrick.vico.core.chart.draw.ChartDrawContext
import com.patrykandpatrick.vico.core.context.MeasureContext

class ChartHorizontalItemPlacer : AxisItemPlacer.Horizontal {

    override fun getEndHorizontalAxisInset(
        context: MeasureContext,
        horizontalDimensions: HorizontalDimensions,
        tickThickness: Float
    ): Float {
        return 100f
    }

    override fun getLabelValues(
        context: ChartDrawContext,
        visibleXRange: ClosedFloatingPointRange<Float>,
        fullXRange: ClosedFloatingPointRange<Float>
    ): List<Float> {
        return listOf(0f)
    }

    override fun getLineValues(
        context: ChartDrawContext,
        visibleXRange: ClosedFloatingPointRange<Float>,
        fullXRange: ClosedFloatingPointRange<Float>
    ): List<Float> {
        return listOf(0f)
    }

    override fun getMeasuredLabelClearance(
        context: MeasureContext,
        horizontalDimensions: HorizontalDimensions,
        fullXRange: ClosedFloatingPointRange<Float>
    ): Float {
        return 100f
    }

    override fun getMeasuredLabelValues(
        context: MeasureContext,
        horizontalDimensions: HorizontalDimensions,
        fullXRange: ClosedFloatingPointRange<Float>
    ): List<Float> {
        return listOf(0f, 1f, 2f, 3f, 4f, 5f, 6f)
    }

    override fun getStartHorizontalAxisInset(
        context: MeasureContext,
        horizontalDimensions: HorizontalDimensions,
        tickThickness: Float
    ): Float {
        return 0f
    }
}