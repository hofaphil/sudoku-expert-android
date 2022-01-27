package com.aol.philipphofer.logic;

import java.util.Locale;

public class Timer {

    private boolean stopped = false;
    private boolean running = false;
    private int time; // in seconds

    public Timer() {
        this.time = 0;
        this.init();
    }

    private void init() {
        new Thread() {
            @Override
            public void run() {
                while (!stopped) {
                    while (running) {
                        try {
                            sleep(1000);
                            if (running)
                                time++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    public int getTime() {
        return this.time;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void startTimer(int time) {
        this.time = time;
        this.running = true;
    }

    public void stopTimer() {
        this.running = false;
    }

    public void killTimer() {
        this.stopTimer();
        this.stopped = true;
    }

    public static String timeToString(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        int minutes = (timeInSeconds % 3600) / 60;
        int seconds = timeInSeconds % 60;

        if (hours > 0)
            return String.format(Locale.ENGLISH, "%d:%02d:%02d", hours, minutes, seconds);
        return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
    }
}
