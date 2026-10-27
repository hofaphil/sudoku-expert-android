package com.aol.philipphofer.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.aol.philipphofer.logic.help.Difficulty;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameHistoryTests {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private final GameRecord timed = new GameRecord(1700000000000L, Difficulty.ADVANCED, true, 245);
    private final GameRecord untimed = new GameRecord(1700000001000L, Difficulty.BEGINNER, true, null);
    private final GameRecord lost = new GameRecord(1700000002000L, Difficulty.EXPERT, false, null);

    @Test
    public void testRecordRoundTrip() {
        for (GameRecord record : Arrays.asList(timed, untimed, lost))
            assertEquals(record, GameRecord.Companion.parse(record.serialize()));

        assertEquals("1700000000000;1;1;245", timed.serialize());
        assertEquals("1700000001000;0;1;", untimed.serialize());
    }

    @Test
    public void testRecordBrokenLines() {
        assertNull(GameRecord.Companion.parse(""));
        assertNull(GameRecord.Companion.parse("1700000000000;1;1"));
        assertNull(GameRecord.Companion.parse("abc;1;1;245"));
        assertNull(GameRecord.Companion.parse("1700000000000;7;1;245"));
        assertNull(GameRecord.Companion.parse("1700000000000;1;2;245"));
        assertNull(GameRecord.Companion.parse("1700000000000;1;1;x"));
    }

    @Test
    public void testHistoryAppendsAndReloads() {
        File file = new File(folder.getRoot(), "history.csv");
        GameHistory history = new GameHistory(file);
        assertTrue(history.records().isEmpty());

        history.add(timed);
        history.add(lost);
        assertEquals(Arrays.asList(timed, lost), history.records());

        // a new instance reads the same games from the file
        assertEquals(Arrays.asList(timed, lost), new GameHistory(file).records());
    }

    @Test
    public void testHistorySkipsBrokenLines() throws IOException {
        File file = new File(folder.getRoot(), "history.csv");
        Files.write(file.toPath(), (timed.serialize() + "\n1700000000000;1\n" + lost.serialize() + "\n")
                .getBytes(StandardCharsets.UTF_8));

        assertEquals(Arrays.asList(timed, lost), new GameHistory(file).records());
    }

    @Test
    public void testHistoryClear() {
        File file = new File(folder.getRoot(), "history.csv");
        GameHistory history = new GameHistory(file);
        history.add(timed);

        history.clear();
        assertTrue(history.records().isEmpty());
        assertFalse(file.exists());
        assertTrue(new GameHistory(file).records().isEmpty());
    }

    @Test
    public void testStatisticsWithoutGames() {
        DifficultyStatistics statistics = DifficultyStatistics.Companion.of(
                Difficulty.BEGINNER, new LegacyStatistics(), Collections.emptyList());
        assertEquals(new DifficultyStatistics(0, 0, 0, 0, 0), statistics);
    }

    @Test
    public void testStatisticsOfRecords() {
        List<GameRecord> records = Arrays.asList(
                new GameRecord(1L, Difficulty.BEGINNER, true, 100),
                new GameRecord(2L, Difficulty.BEGINNER, true, 200),
                // without the timer: solved, but no time
                new GameRecord(3L, Difficulty.BEGINNER, true, null),
                new GameRecord(4L, Difficulty.BEGINNER, false, null),
                // other difficulty
                new GameRecord(5L, Difficulty.EXPERT, true, 50)
        );

        DifficultyStatistics statistics = DifficultyStatistics.Companion.of(
                Difficulty.BEGINNER, new LegacyStatistics(), records);
        assertEquals(new DifficultyStatistics(3, 1, 100, 150, 300), statistics);
    }

    @Test
    public void testOverallStatistics() {
        List<GameRecord> records = Arrays.asList(
                new GameRecord(1L, Difficulty.BEGINNER, true, 100),
                new GameRecord(2L, Difficulty.EXPERT, true, 300),
                new GameRecord(3L, Difficulty.ADVANCED, false, null)
        );
        LegacyStatistics legacy = new LegacyStatistics(2, 2, 400, 150)
                .plus(new LegacyStatistics(1, 0, 0, 0))
                .plus(new LegacyStatistics(1, 1, 90, 90));
        assertEquals(new LegacyStatistics(4, 3, 490, 90), legacy);

        // null stands for all difficulties: 6 solved, 5 timed with 890 seconds
        DifficultyStatistics statistics = DifficultyStatistics.Companion.of(null, legacy, records);
        assertEquals(new DifficultyStatistics(6, 1, 90, 178, 890), statistics);
    }

    @Test
    public void testStatisticsWithLegacy() {
        // 4 solved before the history, 3 of them timed with 600 seconds, best 120
        LegacyStatistics legacy = new LegacyStatistics(4, 3, 600, 120);
        List<GameRecord> records = Arrays.asList(
                new GameRecord(1L, Difficulty.ADVANCED, true, 200),
                new GameRecord(2L, Difficulty.ADVANCED, false, null)
        );

        DifficultyStatistics statistics = DifficultyStatistics.Companion.of(Difficulty.ADVANCED, legacy, records);
        assertEquals(new DifficultyStatistics(5, 1, 120, 200, 800), statistics);

        // a faster recorded game beats the legacy best time
        statistics = DifficultyStatistics.Companion.of(Difficulty.ADVANCED, legacy,
                Collections.singletonList(new GameRecord(3L, Difficulty.ADVANCED, true, 60)));
        assertEquals(60, statistics.getBestTime());
    }
}
