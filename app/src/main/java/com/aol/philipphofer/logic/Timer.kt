package com.aol.philipphofer.logic

import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class Timer(private val timerListener: TimerListener) {

    private val executor = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "sudoku-timer").apply { isDaemon = true }
    }
    private var tick: ScheduledFuture<*>? = null

    val isRunning: Boolean
        @Synchronized get() = tick != null

    @Volatile
    var time = 0 // in seconds
        set(time) {
            field = time
            timerListener.timeUpdate(time)
        }

    init {
        time = 0
    }

    fun startTimer(time: Int) {
        this.time = time
        startTimer()
    }

    @Synchronized
    fun startTimer() {
        if (tick != null || executor.isShutdown) return
        tick = executor.scheduleWithFixedDelay({ time++ }, 1, 1, TimeUnit.SECONDS)
    }

    @Synchronized
    fun stopTimer() {
        tick?.cancel(false)
        tick = null
    }

    @Synchronized
    fun killTimer() {
        stopTimer()
        executor.shutdownNow()
    }

    interface TimerListener {
        fun timeUpdate(time: Int)
    }

    companion object {
        // shown instead of a time: the timer is hidden or there is no time yet
        const val NO_TIME = "--:--"

        // 0 stands for no time yet, e.g. no best time before the first solved game
        @JvmStatic
        fun timeOrNone(timeInSeconds: Int): String =
            if (timeInSeconds == 0) NO_TIME else timeToString(timeInSeconds)

        @JvmStatic
        fun timeToString(timeInSeconds: Int): String {
            val hours = timeInSeconds / 3600
            val minutes = timeInSeconds % 3600 / 60
            val seconds = timeInSeconds % 60
            return if (hours > 0) String.format(
                Locale.ENGLISH,
                "%d:%02d:%02d",
                hours,
                minutes,
                seconds
            ) else String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
        }
    }
}
