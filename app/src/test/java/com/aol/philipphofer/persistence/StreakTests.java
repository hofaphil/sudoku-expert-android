package com.aol.philipphofer.persistence;

import static org.junit.Assert.assertEquals;

import com.aol.philipphofer.logic.help.Difficulty;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StreakTests {

    private final ZoneId zone = ZoneId.of("Europe/Vienna");
    private final LocalDate today = LocalDate.of(2026, 10, 25);

    private GameRecord game(LocalDateTime finished, boolean won) {
        long millis = finished.atZone(zone).toInstant().toEpochMilli();
        return new GameRecord(millis, Difficulty.BEGINNER, won, won ? 100 : null);
    }

    private GameRecord solved(int daysAgo) {
        return game(today.minusDays(daysAgo).atTime(12, 0), true);
    }

    private int streak(GameRecord... records) {
        return Streak.INSTANCE.days(Arrays.asList(records), today, zone);
    }

    @Test
    public void testNoGames() {
        assertEquals(0, Streak.INSTANCE.days(Collections.emptyList(), today, zone));
    }

    @Test
    public void testSolvedToday() {
        assertEquals(1, streak(solved(0)));
        // several games on one day count once
        assertEquals(1, streak(solved(0), solved(0), solved(0)));
    }

    @Test
    public void testConsecutiveDays() {
        assertEquals(3, streak(solved(0), solved(1), solved(2)));
        // order of the records does not matter
        assertEquals(3, streak(solved(2), solved(0), solved(1)));
    }

    @Test
    public void testNotYetSolvedToday() {
        // the streak of yesterday is kept until today is over
        assertEquals(2, streak(solved(1), solved(2)));
        // a full day without a solved game ends it
        assertEquals(0, streak(solved(2), solved(3)));
    }

    @Test
    public void testGapEndsStreak() {
        assertEquals(2, streak(solved(0), solved(1), solved(3), solved(4), solved(5)));
    }

    @Test
    public void testLostGamesDoNotCount() {
        GameRecord lostToday = game(today.atTime(9, 0), false);
        assertEquals(0, streak(lostToday));
        // a lost game today does not end the streak of yesterday
        assertEquals(1, streak(lostToday, solved(1)));
    }

    @Test
    public void testDaysFollowTheLocalTimeZone() {
        // just before and after midnight are two different days
        GameRecord lateYesterday = game(today.minusDays(1).atTime(23, 59), true);
        GameRecord earlyToday = game(today.atTime(0, 1), true);
        assertEquals(2, streak(lateYesterday, earlyToday));
    }

    @Test
    public void testLongStreak() {
        List<GameRecord> records = new ArrayList<>();
        for (int i = 0; i < 100; i++) records.add(solved(i));
        assertEquals(100, Streak.INSTANCE.days(records, today, zone));
    }
}
