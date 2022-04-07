package com.aol.philipphofer.gui;

import static org.junit.Assert.*;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.logic.sudoku.Sudoku;
import com.aol.philipphofer.persistence.Data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTests {

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

    /**
     * first start of the app
     */
    @Test
    public void testStartup() {
        mainActivity.onActivity(mainActivity -> {
            // check modes
            assertTrue(MainActivity.LOAD_MODE);
            assertFalse(MainActivity.SHARED);

            // difficulty
            assertEquals(Difficulty.BEGINNER, MainActivity.DIFFICULTY);

            // timer
            assertTrue(mainActivity.timer.isRunning());
            assertEquals(0, mainActivity.timer.getTime());

            // pause
            assertFalse(mainActivity.pause);

            // sudoku
            assertNotNull(mainActivity.game);
            assertEquals(MainActivity.DIFFICULTY.getFreeFields(), mainActivity.game.freeFields());

            // selected
            assertNull(mainActivity.selected);
        });
    }

    /**
     * restart of the app
     */
    @Test
    public void testRestart() {
        Sudoku sudoku = new Sudoku();
        sudoku.overallErrors = 1;
        final int gameTime = 300;

        mainActivity.onActivity(mainActivity -> {
            boolean inserted = false;
            for (int b = 0; b < 9; b++)
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++) {
                        Position pos = new Position(b, i, j);
                        if (!inserted && mainActivity.game.getNumber(pos).isChangeable()) {
                            int solution = mainActivity.game.getNumber(pos).getSolution();
                            mainActivity.select(pos);
                            // insert false number
                            mainActivity.insert(solution == 3 ? 4 : 3);
                            inserted = true;
                        }
                        // save game state
                        sudoku.setNumber(pos, mainActivity.game.getNumber(pos));
                    }

            // set new timer
            mainActivity.timer.startTimer(gameTime);

            assertEquals(Difficulty.BEGINNER, MainActivity.DIFFICULTY);
        });

        // restart activity
        mainActivity.recreate();

        mainActivity.onActivity(mainActivity -> {
            // check for equal sudoku
            assertEquals(sudoku, mainActivity.game);

            // check for correct game-time
            assertEquals(gameTime, mainActivity.timer.getTime());

            // check for same errors
            assertEquals(1, mainActivity.game.currentErrors());
            assertEquals(1, mainActivity.game.overallErrors);

            assertEquals(Difficulty.BEGINNER, MainActivity.DIFFICULTY);
        });
    }
}
