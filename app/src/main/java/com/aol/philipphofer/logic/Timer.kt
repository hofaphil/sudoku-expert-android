package com.aol.philipphofer.logic

import java.util.Locale

class Timer(private val timerListener: TimerListener) {

    var isRunning = false
        private set
    var time = 0 // in seconds
        set(time) {
            field = time
            timerListener.timeUpdate(time)
        }
    private var stopped = false

    init {
        init()
        time = 0
    }

    private fun init() {
        object : Thread() {
            override fun run() {
                while (!stopped) {
                    while (isRunning) {
                        try {
                            sleep(1000)
                            if (isRunning) time = ++time
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }.start()
    }

    fun startTimer(time: Int) {
        this.time = time
        isRunning = true
    }

    fun startTimer() {
        isRunning = true
    }

    fun stopTimer() {
        isRunning = false
    }

    fun killTimer() {
        stopTimer()
        stopped = true
    }

    interface TimerListener {
        fun timeUpdate(time: Int)
    }

    companion object {
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