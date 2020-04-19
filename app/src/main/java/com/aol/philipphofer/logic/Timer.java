package com.aol.philipphofer.logic;

import android.content.Context;

public class Timer extends Thread {

    private boolean stopped = false;
    private boolean running = false;
    private int time; //in sec
    private MainActivity mainActivity;

    Timer(Context context) {
        this(context, 0);
    }

    private Timer(Context context, int time) {
        mainActivity = (MainActivity) context;
        this.time = time;
    }

    public static String timeToString(int time) { //time in sec
        int hours = 0;
        int min10 = 0, min = 0;
        int sec10 = 0, sec;
        while (time - 3600 >= 0) {
            hours++;
            time -= 3600;
        }
        while (time - 600 >= 0) {
            min10++;
            time -= 600;
        }
        while (time - 60 >= 0) {
            min++;
            time -= 60;
        }
        while (time - 10 >= 0) {
            sec10++;
            time -= 10;
        }
        sec = time;
        if (hours > 0)
            return hours + ":" + min10 + "" + min + ":" + sec10 + "" + sec;
        return min10 + "" + min + ":" + sec10 + "" + sec;
    }

    @Override
    public void run() {
        while (!stopped) {
            while (running) {
                try {
                    mainActivity.runOnUiThread(() -> mainActivity.statusBar.setTime(this.time));
                    sleep(1000);
                    if (!MainActivity.pause)
                        time++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    int getTime() {
        return this.time;
    }

    private void setTime(int time) {
        this.time = time;
    }

    private void startTimer() {
        running = true;
    }

    void startTimer(int time) {
        this.setTime(time);
        this.startTimer();
    }

    void stopTimer() {
        this.running = false;
    }

    void killTimer() {
        this.stopped = true;
    }

}
