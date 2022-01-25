package com.aol.philipphofer.sudokuTests;

import com.aol.philipphofer.logic.Timer;

import static org.junit.Assert.*;

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
}
