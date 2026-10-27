package com.aol.philipphofer.gui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.aol.philipphofer.persistence.Data.GAME_DIFFICULTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.aol.philipphofer.R;
import com.aol.philipphofer.helper.TestHelpers;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.logic.sudoku.Number;
import com.aol.philipphofer.persistence.Data;
import com.aol.philipphofer.persistence.DifficultyStatistics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GameResultTests {

    public ActivityScenario<MainActivity> mainActivity;

    @Before
    public void setup() {
        Data.Constants.instance(InstrumentationRegistry.getInstrumentation().getTargetContext()).drop();
        mainActivity = ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void tearDown() {
        mainActivity.close();
    }

    @Test
    public void testSuccessSudoku() {
        // At the beginning there are no statistics
        assertEquals(new DifficultyStatistics(0, 0, 0, 0, 0), data().statistics(Difficulty.BEGINNER));

        // Restart app and create a new beginner game
        mainActivity.onActivity(main -> {
            Data.Constants.instance(main).saveInt(GAME_DIFFICULTY, Difficulty.BEGINNER.getNumber());
            Data.Constants.instance(main).setLoadmode(false);
        });
        mainActivity.recreate();

        // Solve the sudoku
        mainActivity.onActivity(main -> {
            TestHelpers.wait(2000);

            for (int block = 0; block < 9; block++) {
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        Position currentPosition = new Position(block, row, col);
                        Number currentNumber = main.game.getNumber(currentPosition);

                        if (currentNumber.isChangeable()) {
                            main.select(currentPosition);
                            main.insert(currentNumber.getSolution());
                        }
                    }
                }
            }
        });

        // Statistics should be updated
        DifficultyStatistics statistics = data().statistics(Difficulty.BEGINNER);
        assertEquals(1, statistics.getSolved());
        assertTrue(statistics.getBestTime() > 1);
        assertTrue(statistics.getAverageTime() > 1);
        assertTrue(data().gameHistory().get(0).getWon());

        // the result screen replaces the game
        assertResultShown(R.string.result_solved);
    }

    @Test
    public void testSuccessSudokuWithoutTimer() {
        // Create a new beginner game without the timer
        mainActivity.onActivity(main -> {
            Data.Constants.instance(main).saveBoolean(Data.SETTINGS_SHOW_TIME, false);
            Data.Constants.instance(main).saveInt(GAME_DIFFICULTY, Difficulty.BEGINNER.getNumber());
            Data.Constants.instance(main).setLoadmode(false);
        });
        mainActivity.recreate();

        // Solve the sudoku
        mainActivity.onActivity(main -> {
            TestHelpers.wait(2000);

            for (int block = 0; block < 9; block++)
                for (int row = 0; row < 3; row++)
                    for (int col = 0; col < 3; col++) {
                        Position currentPosition = new Position(block, row, col);
                        Number currentNumber = main.game.getNumber(currentPosition);

                        if (currentNumber.isChangeable()) {
                            main.select(currentPosition);
                            main.insert(currentNumber.getSolution());
                        }
                    }
        });

        // the hidden timer still ran: the game counts for the times
        Integer time = data().gameHistory().get(0).getTime();
        assertNotNull(time);
        assertEquals(new DifficultyStatistics(1, 0, time, time, time), data().statistics(Difficulty.BEGINNER));

        // the result screen has no time tile
        assertResultShown(R.string.result_solved);
        onView(ViewMatchers.withId(R.id.resultTimeTile)).check(doesNotExist());
    }

    @Test
    public void testFailureSudoku() {
        // At the beginning there are no statistics
        assertEquals(new DifficultyStatistics(0, 0, 0, 0, 0), data().statistics(Difficulty.BEGINNER));

        // Restart app and create a new beginner game
        mainActivity.onActivity(main -> {
            Data.Constants.instance(main).saveInt(GAME_DIFFICULTY, Difficulty.BEGINNER.getNumber());
            Data.Constants.instance(main).setLoadmode(false);
        });
        mainActivity.recreate();

        // Insert 3 false numbers
        mainActivity.onActivity(main -> {
            outer:
            for (int block = 0; block < 9; block++) {
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        Position currentPosition = new Position(block, row, col);
                        Number currentNumber = main.game.getNumber(currentPosition);

                        if (currentNumber.isChangeable()) {
                            for (int i = 0; i < MainActivity.MAX_ERROR; i++) {
                                main.select(currentPosition);
                                main.insert(currentNumber.getSolution() == 9 ? 8 : currentNumber.getSolution() + 1);
                                main.delete();
                            }
                            break outer;
                        }
                    }
                }
            }
        });

        // only recorded as lost
        assertEquals(new DifficultyStatistics(0, 1, 0, 0, 0), data().statistics(Difficulty.BEGINNER));

        // the result screen replaces the game
        assertResultShown(R.string.result_lost);
    }

    /**
     * helper
     */
    private Data data() {
        return Data.Constants.instance(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    private void assertResultShown(int title) {
        TestHelpers.wait(1000);
        assertEquals(Lifecycle.State.DESTROYED, mainActivity.getState());
        onView(ViewMatchers.withId(R.id.resultTitle)).check(matches(withText(title)));
    }
}
