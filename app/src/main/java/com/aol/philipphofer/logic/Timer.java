package com.aol.philipphofer.logic;

import android.content.Context;

import java.util.Locale;
import java.util.Observable;

public class Timer extends Observable {

    private boolean stopped = false;
    private boolean running = false;
    private int time; //in sec

    public Timer() {
        setTime(0);
    }

    public static String timeToString(int timeInSeconds) {

        int hours = timeInSeconds / 3600;
        int minutes = (timeInSeconds % 3600) / 60;
        int seconds = timeInSeconds % 60;

        if (hours > 0)
            return String.format(Locale.ENGLISH, "%d:%02d:%02d", hours, minutes, seconds);
        return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                while (!stopped) {
                    while (running) {
                        try {
                            sleep(1000);
                            if (!MainActivity.pause)
                                time++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        setChanged();
                        notifyObservers();
                    }
                }
            }
        }.start();
    }

    public int getTime() {
        return this.time;
    }

    private void setTime(int time) {
        this.time = time;
    }

    private void startTimer() {
        running = true;
    }

    public void startTimer(int time) {
        this.setTime(time);
        this.startTimer();
    }

    public void stopTimer() {
        this.running = false;
    }

    public void killTimer() {
        this.stopped = true;
    }

}
