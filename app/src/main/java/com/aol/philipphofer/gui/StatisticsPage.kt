package com.aol.philipphofer.gui

import android.view.View
import android.widget.TextView
import com.aol.philipphofer.R
import com.aol.philipphofer.logic.Timer
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.persistence.Data

/**
 * Statistics tab of the home screen: overall or of one difficulty, chosen by the tabs on top.
 */
class StatisticsPage(root: View) {

    private val data = Data.instance(root.context)

    // null stands for all difficulties together
    private val tabs: Map<Difficulty?, TextView> = mapOf(
        null to root.findViewById(R.id.statisticsOverall),
        Difficulty.BEGINNER to root.findViewById(R.id.statisticsBeginner),
        Difficulty.ADVANCED to root.findViewById(R.id.statisticsAdvanced),
        Difficulty.EXPERT to root.findViewById(R.id.statisticsExpert)
    )
    private var selected: Difficulty? = null

    private val empty: View = root.findViewById(R.id.statisticsEmpty)
    private val emptyText: TextView = root.findViewById(R.id.statisticsEmptyText)
    private val best: View = root.findViewById(R.id.statisticsBest)
    private val details: View = root.findViewById(R.id.statisticsDetails)

    private val averageTime: TextView = root.findViewById(R.id.statisticsAverageTime)
    private val bestTime: TextView = root.findViewById(R.id.statisticsBestTime)
    private val totalTime: TextView = root.findViewById(R.id.statisticsTotalTime)
    private val timesPlayed: TextView = root.findViewById(R.id.statisticsTimesPlayed)
    private val chart: BarChartView = root.findViewById(R.id.statisticsChart)

    init {
        for ((difficulty, tab) in tabs)
            tab.setOnClickListener {
                selected = difficulty
                update()
            }
    }

    fun update() {
        // colored by the Statistics.Tab style
        for ((difficulty, tab) in tabs) tab.isSelected = difficulty == selected

        val statistics = data.statistics(selected)

        // a card with a hint instead of empty statistics
        val played = statistics.solved > 0 || statistics.lost > 0
        empty.visibility = if (played) View.GONE else View.VISIBLE
        best.visibility = if (played) View.VISIBLE else View.GONE
        details.visibility = best.visibility
        if (!played) {
            emptyText.text = selected?.let { emptyText.context.getString(R.string.statistics_empty_text, it.getText(emptyText.context)) }
                ?: emptyText.context.getString(R.string.statistics_empty_text_overall)
            return
        }

        // played, but maybe only games from before the history, which have no times
        averageTime.text = Timer.timeOrNone(statistics.averageTime)
        bestTime.text = Timer.timeOrNone(statistics.bestTime)
        totalTime.text = Timer.timeOrNone(statistics.totalTime)
        timesPlayed.text = statistics.solved.toString()
        chart.setSolvedPerDay(data.solvedPerDay(selected))
    }
}
