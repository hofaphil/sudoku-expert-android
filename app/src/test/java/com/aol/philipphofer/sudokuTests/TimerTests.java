package com.aol.philipphofer.sudokuTests;

import com.aol.philipphofer.logic.Timer;

import static org.junit.Assert.*;

import static java.lang.Thread.sleep;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TimerTests {

    /**
     * Constructor
     */
    @Test
    public void testTime() {
        Timer t = new Timer();
        assertEquals(0, t.getTime());
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
        Timer t = new Timer();

        t.startTimer(23);
        assertEquals(23, t.getTime());
        assertTrue(t.isRunning());

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(25, t.getTime());
        assertTrue(t.isRunning());
    }

    /**
     * void stopTimer()
     */
    @Test
    public void testStopTimer() {
        Timer t = new Timer();
        t.startTimer(23);

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(25, t.getTime());
        assertTrue(t.isRunning());

        t.stopTimer();

        assertEquals(25, t.getTime());
        assertFalse(t.isRunning());

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(25, t.getTime());
        assertFalse(t.isRunning());
    }

    /**
     * void killTimer()
     */
    @Test
    public void testKillTimer() {
        Timer t = new Timer();
        t.startTimer(23);

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(25, t.getTime());
        assertTrue(t.isRunning());

        t.killTimer();

        assertEquals(25, t.getTime());
        assertFalse(t.isRunning());

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(25, t.getTime());
        assertFalse(t.isRunning());
    }
}
