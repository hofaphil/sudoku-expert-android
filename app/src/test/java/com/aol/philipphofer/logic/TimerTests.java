package com.aol.philipphofer.logic;

import static org.junit.Assert.*;

import static java.lang.Thread.sleep;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TimerTests {

    private Timer.TimerListener timerListener;
    private int time;

    @Before
    public void setup() {
        this.timerListener = t -> time = t;
    }

    /**
     * Constructor
     */
    @Test
    public void testTime() {
        Timer t = new Timer(this.timerListener);
        assertEquals(0, t.getTime());
        assertEquals(0, time);
        assertFalse(t.isRunning());
    }

    /**
     * static String timeToString(int timeInSeconds)
     */
    @Test
    public void testTimeToString() {
        int seconds = 0;
        assertEquals("00:00", Timer.timeToString(seconds));

        seconds = 5;
        assertEquals("00:05", Timer.timeToString(seconds));

        seconds = 15;
        assertEquals("00:15", Timer.timeToString(seconds));

        seconds = 69;
        assertEquals("01:09", Timer.timeToString(seconds));

        seconds = 722;
        assertEquals("12:02", Timer.timeToString(seconds));

        seconds = 4322;
        assertEquals("1:12:02", Timer.timeToString(seconds));
    }

    /**
     * void startTimer(int time)
     */
    @Test
    public void testStartTimer() {
        Timer t = new Timer(this.timerListener);

        t.startTimer(23);
        assertEquals(23, t.getTime());
        assertEquals(23, time);
        assertTrue(t.isRunning());

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(25, t.getTime());
        assertEquals(25, time);
        assertTrue(t.isRunning());
    }

    /**
     * void stopTimer()
     */
    @Test
    public void testStopTimer() {
        Timer t = new Timer(this.timerListener);
        t.startTimer(23);

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(25, t.getTime());
        assertEquals(25, time);
        assertTrue(t.isRunning());

        t.stopTimer();

        assertEquals(25, t.getTime());
        assertEquals(25, time);
        assertFalse(t.isRunning());

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(25, t.getTime());
        assertEquals(25, time);
        assertFalse(t.isRunning());
    }

    /**
     * void killTimer()
     */
    @Test
    public void testKillTimer() {
        Timer t = new Timer(this.timerListener);
        t.startTimer(23);

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(25, t.getTime());
        assertEquals(25, time);
        assertTrue(t.isRunning());

        t.killTimer();

        assertEquals(25, t.getTime());
        assertEquals(25, time);
        assertFalse(t.isRunning());

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(25, t.getTime());
        assertEquals(25, time);
        assertFalse(t.isRunning());
    }
}
