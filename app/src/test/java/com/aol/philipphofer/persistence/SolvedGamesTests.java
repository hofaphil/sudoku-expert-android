package com.aol.philipphofer.persistence;

import static org.junit.Assert.assertEquals;

import com.aol.philipphofer.logic.help.Difficulty;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SolvedGamesTests {

    private final ZoneId zone = ZoneId.of("Europe/Vienna");
    private final LocalDate today = LocalDate.of(2026, 10, 25);

    private GameRecord game(LocalDate day, Difficulty difficulty, boolean won) {
        long millis = day.atTime(12, 0).atZone(zone).toInstant().toEpochMilli();
        return new GameRecord(millis, difficulty, won, won ? 100 : null);
    }

    @Test
    public void testPerDay() {
        List<GameRecord> records = Arrays.asList(
                game(today, Difficulty.BEGINNER, true),
                game(today, Difficulty.BEGINNER, true),
                game(today.minusDays(3), Difficulty.BEGINNER, true),
                // lost and other difficulties do not count
                game(today, Difficulty.BEGINNER, false),
                game(today, Difficulty.EXPERT, true)
        );

        Map<LocalDate, Integer> perDay = SolvedGames.INSTANCE.perDay(records, Difficulty.BEGINNER, zone);
        assertEquals(2, perDay.size());
        assertEquals(2, (int) perDay.get(today));
        assertEquals(1, (int) perDay.get(today.minusDays(3)));

        // null stands for all difficulties
        assertEquals(3, (int) SolvedGames.INSTANCE.perDay(records, null, zone).get(today));
    }

    @Test
    public void testLastDays() {
        List<LocalDate> days = SolvedGames.INSTANCE.lastDays(today, SolvedGames.DAYS);
        assertEquals(SolvedGames.DAYS, days.size());
        // oldest first, today last, no gaps
        assertEquals(today.minusDays(6), days.get(0));
        assertEquals(today, days.get(6));
        for (int i = 1; i < days.size(); i++)
            assertEquals(days.get(i - 1).plusDays(1), days.get(i));
    }
}
