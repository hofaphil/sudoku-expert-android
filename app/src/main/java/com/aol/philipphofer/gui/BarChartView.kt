package com.aol.philipphofer.gui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.aol.philipphofer.R
import com.aol.philipphofer.persistence.SolvedGames
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Solved games of the last days as bars, today's bar is yellow.
 * Each bar with games shows its count above it, the weekdays are below the bars.
 */
class BarChartView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {

    private var days: List<LocalDate> = SolvedGames.lastDays(LocalDate.now())
    private var solvedPerDay: Map<LocalDate, Int> = emptyMap()

    private val plotHeight = resources.getDimension(R.dimen.barchart_height)
    private val maxBarWidth = resources.getDimension(R.dimen.barchart_bar_max)
    private val gap = resources.getDimension(R.dimen.barchart_gap)
    private val border = resources.getDimension(R.dimen.border)
    private val line = resources.getDimension(R.dimen.line)

    private val barColor = context.getColor(R.color.chart_bar)
    private val todayColor = context.getColor(R.color.chart_bar_today)
    private val onSurface = context.getColor(R.color.on_surface)
    private val onSurfaceVariant = context.getColor(R.color.on_surface_variant)

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = context.getColor(R.color.outline)
    }
    private val regular = ResourcesCompat.getFont(context, R.font.poppins)
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimension(R.dimen.barchart_text)
        textAlign = Paint.Align.CENTER
        typeface = regular
    }
    private val bold = Typeface.create(regular, Typeface.BOLD)

    private val labelHeight = text.fontSpacing
    private val bar = RectF()

    fun setSolvedPerDay(solvedPerDay: Map<LocalDate, Int>, today: LocalDate = LocalDate.now()) {
        this.solvedPerDay = solvedPerDay
        days = SolvedGames.lastDays(today)

        // the chart as text for screen readers: the total, then every day
        val counts = days.map { solvedPerDay[it] ?: 0 }
        val total = counts.sum()
        contentDescription = resources.getQuantityString(R.plurals.statistics_chart_description, total, total) +
                ": " + days.zip(counts).joinToString { (day, count) -> "${weekday(day)} $count" }
        invalidate()
    }

    private fun weekday(day: LocalDate) = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = paddingTop + labelHeight + plotHeight + gap + labelHeight + paddingBottom
        setMeasuredDimension(
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
            resolveSize(height.toInt(), heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        val columnWidth = (width - paddingLeft - paddingRight).toFloat() / days.size
        val barWidth = minOf(columnWidth * 0.6f, maxBarWidth)
        val baseline = paddingTop + labelHeight + plotHeight
        // at least one game high, so a single game does not fill the whole chart alone
        val max = maxOf(days.maxOf { solvedPerDay[it] ?: 0 }, 1)

        for ((index, day) in days.withIndex()) {
            val count = solvedPerDay[day] ?: 0
            val isToday = index == days.lastIndex
            val center = paddingLeft + columnWidth * (index + 0.5f)

            if (count > 0) {
                val top = baseline - plotHeight * count / max
                bar.set(center - barWidth / 2, top, center + barWidth / 2, baseline)
                fill.color = if (isToday) todayColor else barColor
                canvas.drawRect(bar, fill)
                // the border lies inside the bar
                stroke.strokeWidth = border
                bar.inset(border / 2, border / 2)
                canvas.drawRect(bar, stroke)

                text.typeface = bold
                text.color = onSurface
                canvas.drawText(count.toString(), center, top - gap - text.descent(), text)
            }

            text.typeface = if (isToday) bold else regular
            text.color = if (isToday) onSurface else onSurfaceVariant
            canvas.drawText(weekday(day), center, baseline + gap - text.ascent(), text)
        }

        // baseline below the bars, also visible on days without games
        stroke.strokeWidth = line
        canvas.drawLine(paddingLeft.toFloat(), baseline, (width - paddingRight).toFloat(), baseline, stroke)
    }
}
