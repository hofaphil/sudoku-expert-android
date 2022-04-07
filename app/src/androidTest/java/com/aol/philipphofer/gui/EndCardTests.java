package com.aol.philipphofer.gui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static com.aol.philipphofer.persistence.Data.GAME_DIFFICULTY;
import static com.aol.philipphofer.persistence.Data.STATISTICS_BESTTIME;
import static com.aol.philipphofer.persistence.Data.STATISTICS_TIMEOVERALL;
import static com.aol.philipphofer.persistence.Data.STATISTICS_TIMESPLAYED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.aol.philipphofer.R;
import com.aol.philipphofer.helper.AssertionHelpers;
import com.aol.philipphofer.helper.TestHelpers;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.logic.sudoku.Number;
import com.aol.philipphofer.persistence.Data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EndCardTests {

    public ActivityScenario<MainActivity> mainActivity;

    @Before
    public void setup() {
        Data.instance(InstrumentationRegistry.getInstrumentation().getContext()).drop();
        mainActivity = ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void tearDown() {
        mainActivity.close();
    }

    @Test
    public void testSuccessSudoku() {
        // At the beginning everything should be zero
        mainActivity.onActivity(main -> {
            assertEquals(0, Data.instance(main).loadInt(STATISTICS_TIMESPLAYED + Difficulty.BEGINNER.getNumber()));
            assertEquals(0, Data.instance(main).loadInt(STATISTICS_BESTTIME + Difficulty.BEGINNER.getNumber()));
            assertEquals(0, Data.instance(main).loadInt(STATISTICS_TIMEOVERALL + Difficulty.BEGINNER.getNumber()));
        });

        // Restart app and create a new beginner game
        mainActivity.onActivity(main -> {
            Data.instance(main).saveInt(GAME_DIFFICULTY, Difficulty.BEGINNER.getNumber());
            Data.instance(main).setLoadmode(false);
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
        mainActivity.onActivity(main -> {
            assertEquals(1, Data.instance(main).loadInt(STATISTICS_TIMESPLAYED + Difficulty.BEGINNER.getNumber()));
            assertTrue(Data.instance(main).loadInt(STATISTICS_BESTTIME + Difficulty.BEGINNER.getNumber()) > 1);
            assertTrue(Data.instance(main).loadInt(STATISTICS_TIMEOVERALL + Difficulty.BEGINNER.getNumber()) > 1);
        });

        TestHelpers.waitForView();
        ViewInteraction okButton = onView(ViewMatchers.withId(R.id.ok));
        okButton.perform(click());

        mainActivity.onActivity(AssertionHelpers::isNewGame);
    }

    @Test
    public void testFailureSudoku() {
        // At the beginning everything should be zero
        mainActivity.onActivity(main -> {
            assertEquals(0, Data.instance(main).loadInt(STATISTICS_TIMESPLAYED + Difficulty.BEGINNER.getNumber()));
            assertEquals(0, Data.instance(main).loadInt(STATISTICS_BESTTIME + Difficulty.BEGINNER.getNumber()));
            assertEquals(0, Data.instance(main).loadInt(STATISTICS_TIMEOVERALL + Difficulty.BEGINNER.getNumber()));
        });

        // Restart app and create a new beginner game
        mainActivity.onActivity(main -> {
            Data.instance(main).saveInt(GAME_DIFFICULTY, Difficulty.BEGINNER.getNumber());
            Data.instance(main).setLoadmode(false);
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

        // Statistics should not have been updated
        mainActivity.onActivity(main -> {
            assertEquals(0, Data.instance(main).loadInt(STATISTICS_TIMESPLAYED + Difficulty.BEGINNER.getNumber()));
            assertEquals(0, Data.instance(main).loadInt(STATISTICS_BESTTIME + Difficulty.BEGINNER.getNumber()));
            assertEquals(0, Data.instance(main).loadInt(STATISTICS_TIMEOVERALL + Difficulty.BEGINNER.getNumber()));
        });

        TestHelpers.waitForView();
        ViewInteraction okButton = onView(ViewMatchers.withId(R.id.ok));
        okButton.perform(click());

        mainActivity.onActivity(AssertionHelpers::isNewGame);
    }
}
