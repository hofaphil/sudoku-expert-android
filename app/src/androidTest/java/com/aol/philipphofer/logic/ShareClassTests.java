package com.aol.philipphofer.logic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.aol.philipphofer.logic.sudoku.Number;
import com.aol.philipphofer.logic.sudoku.Sudoku;
import com.aol.philipphofer.persistence.Data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ShareClassTests {

    // 5 6 0   2 0 3   0 9 0         5 6 4   2 8 3   1 9 7
    // 1 9 0   7 0 4   0 5 8         1 9 3   7 6 4   2 5 8
    // 7 8 0   0 0 5   0 3 6         7 8 2   1 9 5   4 3 6
    //
    // 3 0 0   5 2 9   0 6 4         3 1 8   5 2 9   7 6 4
    // 0 0 0   3 0 8   9 1 0    ->   2 7 6   3 4 8   9 1 5
    // 4 5 9   6 0 7   0 2 3         4 5 9   6 1 7   8 2 3
    //
    // 0 0 0   0 0 0   0 0 2         9 3 1   8 7 6   5 4 2
    // 6 0 0   4 0 0   0 0 0         6 2 7   4 5 1   3 8 9
    // 0 4 0   9 0 0   6 0 0         8 4 5   9 3 2   6 7 1
    private static final String link = "https://philipphofer.de/share?id=m6i9o8jk7la5qa58k63d4595293p6on491a23b6b4c4a9c2b6a";
    private final int[][][] game = {
            {{5, 6, 0}, {1, 9, 0}, {7, 8, 0}},
            {{2, 0, 3}, {7, 0, 4}, {0, 0, 5}},
            {{0, 9, 0}, {0, 5, 8}, {0, 3, 6}},
            //
            {{3, 0, 0}, {0, 0, 0}, {4, 5, 9}},
            {{5, 2, 9}, {3, 0, 8}, {6, 0, 7}},
            {{0, 6, 4}, {9, 1, 0}, {0, 2, 3}},
            //
            {{0, 0, 0}, {6, 0, 0}, {0, 4, 0}},
            {{0, 0, 0}, {4, 0, 0}, {9, 0, 0}},
            {{0, 0, 2}, {0, 0, 0}, {6, 0, 0}},
    };
    private final int[][][] solution = {
            {{5, 6, 4}, {1, 9, 3}, {7, 8, 2}},
            {{2, 8, 3}, {7, 6, 4}, {1, 9, 5}},
            {{1, 9, 7}, {2, 5, 8}, {4, 3, 6}},
            //
            {{3, 1, 8}, {2, 7, 6}, {4, 5, 9}},
            {{5, 2, 9}, {3, 4, 8}, {6, 1, 7}},
            {{7, 6, 4}, {9, 1, 5}, {8, 2, 3}},
            //
            {{9, 3, 1}, {6, 2, 7}, {8, 4, 5}},
            {{8, 7, 6}, {4, 5, 1}, {9, 3, 2}},
            {{5, 4, 2}, {3, 8, 9}, {6, 7, 1}},
    };

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
    public void testLoad() throws Exception {
        Sudoku sudoku = ShareClass.load(Uri.parse(link));

        assertEquals(0, sudoku.overallErrors);
        assertEquals(0, sudoku.currentErrors());

        for (int block = 0; block < 9; block++)
            for (int row = 0; row < 3; row++)
                for (int col = 0; col < 3; col++) {
                    Position p = new Position(block, row, col);
                    Number n = sudoku.getNumber(p);
                    assertEquals(game[block][row][col], n.getNumber());
                    assertEquals(solution[block][row][col], n.getSolution());
                    assertFalse(n.isNotes());
                    assertFalse(n.isError());
                    assertArrayEquals(new boolean[9], n.getNotes());
                    if (n.getNumber() == 0)
                        assertTrue(n.isChangeable());
                    else
                        assertFalse(n.isChangeable());
                }
    }

    @Test
    public void testShare() throws Exception {
        Sudoku sudoku = ShareClass.load(Uri.parse(link));

        mainActivity.onActivity(main -> {
            main.game = sudoku;
            assertEquals(link, main.share());
        });
    }
}
