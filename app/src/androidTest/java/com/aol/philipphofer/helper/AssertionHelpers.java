package com.aol.philipphofer.helper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.logic.sudoku.Number;

public class AssertionHelpers {

    public static void isNewGame(MainActivity main) {
        assertTrue(main.timer.getTime() < 2);
        assertEquals(0, main.game.overallErrors);
        assertEquals(0, main.game.currentErrors());

        for (int block = 0; block < 9; block++)
            for (int row = 0; row < 3; row++)
                for (int col = 0; col < 3; col++) {
                    Position p = new Position(block, row, col);
                    Number n = main.game.getNumber(p);

                    if (n.isChangeable()) {
                        assertEquals(0, n.getNumber());
                        assertArrayEquals(new boolean[9], n.getNotes());
                    }
                }
    }
}
