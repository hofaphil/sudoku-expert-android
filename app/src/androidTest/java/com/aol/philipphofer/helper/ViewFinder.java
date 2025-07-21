package com.aol.philipphofer.helper;

import static androidx.test.espresso.Espresso.onView;
import static org.hamcrest.CoreMatchers.allOf;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.Position;

public class ViewFinder {
    public static final int[] keys = {
            R.id.num1, R.id.num2, R.id.num3, R.id.num4, R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9
    };

    public static final int[][] btn = {
            {R.id.btn00, R.id.btn01, R.id.btn02},
            {R.id.btn10, R.id.btn11, R.id.btn12},
            {R.id.btn20, R.id.btn21, R.id.btn22}
    };

    public static final int[] blocks = {
            R.id.blk0, R.id.blk1, R.id.blk2, R.id.blk3, R.id.blk4, R.id.blk5, R.id.blk6, R.id.blk7, R.id.blk8
    };

    private static int getNoteGridId(int number) {
        return switch (number) {
            case 1 -> R.id.tv0;
            case 2 -> R.id.tv1;
            case 3 -> R.id.tv2;
            case 4 -> R.id.tv3;
            case 5 -> R.id.tv4;
            case 6 -> R.id.tv5;
            case 7 -> R.id.tv6;
            case 8 -> R.id.tv7;
            default -> R.id.tv8;
        };
    }

    // Fields
    public static ViewInteraction getField(Position position) {
        return onView(allOf(
                ViewMatchers.withId(btn[position.getRow()][position.getColumn()]),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.getBlock()]))
        ));
    }

    public static ViewInteraction getFieldNumber(Position position) {
        return onView(allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(btn[position.getRow()][position.getColumn()])),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.getBlock()])),
                ViewMatchers.withId(R.id.numberView)
        ));
    }

    public static ViewInteraction getFieldGrid(Position position) {
        return onView(allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(btn[position.getRow()][position.getColumn()])),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.getBlock()])),
                ViewMatchers.withId(R.id.notesGrid)
        ));
    }

    public static ViewInteraction getFieldGridNote(Position position, int solution) {
        return onView(allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(btn[position.getRow()][position.getColumn()])),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.getBlock()])),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.notesGrid)),
                ViewMatchers.withId(getNoteGridId(solution))
        ));
    }
}
