package com.aol.philipphofer.gui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.aol.philipphofer.helper.CustomMatchers.backgroundColor;
import static com.aol.philipphofer.helper.CustomMatchers.blocks;
import static com.aol.philipphofer.helper.CustomMatchers.btn;
import static com.aol.philipphofer.persistence.Data.SETTINGS_MARK_ERRORS;
import static com.aol.philipphofer.persistence.Data.SETTINGS_MARK_LINES;
import static com.aol.philipphofer.persistence.Data.SETTINGS_MARK_NUMBERS;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.logic.sudoku.Sudoku;
import com.aol.philipphofer.persistence.Data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SettingsTests {

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
    public void testMarkLines() {
        Position position = new Position(0, 0, 0);

        ViewInteraction selectedField = onView(allOf(
                ViewMatchers.withId(btn[position.getRow()][position.getColumn()]),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.getBlock()]))
        ));

        ViewInteraction[] rowSelected = new ViewInteraction[9];
        ViewInteraction[] colSelected = new ViewInteraction[9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 3; j++) {
                rowSelected[i] = onView(allOf(
                        ViewMatchers.withId(btn[position.getRow()][j]),
                        ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[i < 3 ? 0 : i < 6 ? 1 : 2]))
                ));
                colSelected[i] = onView(allOf(
                        ViewMatchers.withId(btn[j][position.getColumn()]),
                        ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[i < 3 ? 0 : i < 6 ? 3 : 6]))
                ));
            }
        }

        // With mark-lines on
        for (int i = 0; i < 9; i++) {
            rowSelected[i].check(matches(backgroundColor(R.color.unselected)));
            colSelected[i].check(matches(backgroundColor(R.color.unselected)));
        }

        mainActivity.onActivity(main -> {
            main.select(position);
        });

        selectedField.check(matches(backgroundColor(R.color.selected)));
        for (int i = 1; i < 9; i++) {
            rowSelected[i].check(matches(backgroundColor(R.color.lightSelected)));
            colSelected[i].check(matches(backgroundColor(R.color.lightSelected)));
        }

        // Restart activity
        mainActivity.recreate();

        // With mark-lines off
        for (int i = 0; i < 9; i++) {
            rowSelected[i].check(matches(backgroundColor(R.color.unselected)));
            colSelected[i].check(matches(backgroundColor(R.color.unselected)));
        }

        mainActivity.onActivity(main -> {
            Data.instance(main).saveBoolean(SETTINGS_MARK_LINES, false);
            main.select(position);
        });

        selectedField.check(matches(backgroundColor(R.color.selected)));
        for (int i = 1; i < 9; i++) {
            rowSelected[i].check(matches(backgroundColor(R.color.unselected)));
            colSelected[i].check(matches(backgroundColor(R.color.unselected)));
        }
    }

    @Test
    public void testMarkNumbers() {
        Position position = new Position(0, 0, 0);
        final int[] number = {3};
        Sudoku sudoku = new Sudoku();

        // With mark-numbers on
        mainActivity.onActivity(main -> {
            Data.instance(main).saveBoolean(SETTINGS_MARK_LINES, false);
            main.select(position);

            if (main.game.getNumber(position).isChangeable())
                main.insert(number[0]);
            else
                number[0] = main.game.getNumber(position).getNumber();

            for (int block = 0; block < 9; block++)
                for (int row = 0; row < 3; row++)
                    for (int col = 0; col < 3; col++) {
                        Position pos = new Position(block, row, col);
                        sudoku.setNumber(pos, main.game.getNumber(pos));
                    }
        });

        for (int block = 0; block < 9; block++)
            for (int row = 0; row < 3; row++)
                for (int col = 0; col < 3; col++) {
                    Position pos = new Position(block, row, col);
                    if (pos.equals(position))
                        continue;
                    ViewInteraction field = onView(allOf(
                            ViewMatchers.withId(btn[row][col]),
                            ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[block]))
                    ));
                    if (sudoku.getNumber(pos).getNumber() == number[0])
                        field.check(matches(backgroundColor(R.color.lightSelected)));
                    else
                        field.check(matches(backgroundColor(R.color.unselected)));
                }

        // Restart activity
        mainActivity.recreate();

        // With mark-numbers off
        mainActivity.onActivity(main -> {
            Data.instance(main).saveBoolean(SETTINGS_MARK_LINES, false);
            Data.instance(main).saveBoolean(SETTINGS_MARK_NUMBERS, false);
            main.select(position);

            if (main.game.getNumber(position).isChangeable())
                main.insert(number[0]);
            else
                number[0] = main.game.getNumber(position).getNumber();

            for (int block = 0; block < 9; block++)
                for (int row = 0; row < 3; row++)
                    for (int col = 0; col < 3; col++) {
                        Position pos = new Position(block, row, col);
                        sudoku.setNumber(pos, main.game.getNumber(pos));
                    }
        });

        for (int block = 0; block < 9; block++)
            for (int row = 0; row < 3; row++)
                for (int col = 0; col < 3; col++) {
                    Position pos = new Position(block, row, col);
                    if (pos.equals(position))
                        continue;
                    ViewInteraction field = onView(allOf(
                            ViewMatchers.withId(btn[row][col]),
                            ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[block]))
                    ));
                    field.check(matches(backgroundColor(R.color.unselected)));
                }
    }

    @Test
    public void testMarkErrors() {
        Position position = new Position(0, 0, 0);

        // With mark-errors on
        mainActivity.onActivity(main -> {
            // insert a wrong number
            wrongNumberInserted:
            for (int block = 0; block < 9; block++)
                for (int row = 0; row < 3; row++)
                    for (int col = 0; col < 3; col++) {
                        Position pos = new Position(block, row, col);
                        if (main.game.getNumber(pos).isChangeable()) {
                            int solution = main.game.getNumber(pos).getSolution();
                            main.select(pos);
                            main.insert(solution == 9 ? solution - 1 : solution + 1);
                            position.setRow(pos.getRow());
                            position.setColumn(pos.getBlock());
                            position.setBlock(pos.getBlock());
                            break wrongNumberInserted;
                        }
                    }
        });

        ViewInteraction fieldWithWrongNumber = onView(allOf(
                ViewMatchers.withId(btn[position.getRow()][position.getColumn()]),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.getBlock()]))
        ));

        fieldWithWrongNumber.check(matches(backgroundColor(R.color.error)));

        ViewInteraction errorView = onView(ViewMatchers.withId(R.id.errorView));
        errorView.check(matches(withText(InstrumentationRegistry
                .getInstrumentation().getTargetContext().getResources()
                .getString(R.string.statusbar_errors, 1, MainActivity.MAX_ERROR))));

        // Restart app and create a new game
        mainActivity.onActivity(main -> {
            Data.instance(main).saveBoolean(SETTINGS_MARK_ERRORS, false);
            Data.instance(main).setLoadmode(false);
        });
        mainActivity.recreate();

        // With mark-errors off
        mainActivity.onActivity(main -> {
            // insert a wrong number
            wrongNumberInserted:
            for (int block = 0; block < 9; block++)
                for (int row = 0; row < 3; row++)
                    for (int col = 0; col < 3; col++) {
                        Position pos = new Position(block, row, col);
                        if (main.game.getNumber(pos).isChangeable()) {
                            int solution = main.game.getNumber(pos).getSolution();
                            main.select(pos);
                            main.insert(solution == 9 ? solution - 1 : solution + 1);
                            position.setRow(pos.getRow());
                            position.setColumn(pos.getColumn());
                            position.setBlock(pos.getBlock());
                            break wrongNumberInserted;
                        }
                    }
        });

        fieldWithWrongNumber = onView(allOf(
                ViewMatchers.withId(btn[position.getRow()][position.getColumn()]),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.getBlock()]))
        ));

        fieldWithWrongNumber.check(matches(backgroundColor(R.color.selected)));

        errorView = onView(ViewMatchers.withId(R.id.errorView));
        errorView.check(matches(withText(R.string.statusbar_errors_not_enabled)));
    }

    @Test
    public void testShowTime() {
        // With show-time off
        mainActivity.onActivity(main -> {
            Data.instance(main).saveBoolean(Data.SETTINGS_SHOW_TIME, false);
            Data.instance(main).setLoadmode(false);
        });

        mainActivity.recreate();

        ViewInteraction errorView = onView(ViewMatchers.withId(R.id.timeView));
        errorView.check(matches(withText("--:--")));

        // With show-time on
        mainActivity.onActivity(main -> {
            Data.instance(main).saveBoolean(Data.SETTINGS_SHOW_TIME, true);
            Data.instance(main).setLoadmode(false);
        });

        mainActivity.recreate();

        errorView = onView(ViewMatchers.withId(R.id.timeView));
        errorView.check(matches(withText("00:00")));
    }

    @Test
    public void testCheckNotes() {
        final int number = 4;
        boolean[] hasNote = new boolean[9];
        hasNote[number - 1] = true;
        boolean[] noNotes = new boolean[9];

        Position numberPosition = new Position(0, 0, 0);
        Position[] notePosition = {
                new Position(2, 0, 0),
                new Position(6, 0, 0),
                new Position(0, 1, 1)
        };

        // With check-notes on
        mainActivity.onActivity(main -> {
            // clear sudoku
            main.game = new Sudoku();
            main.sudokuGrid.init(main.game.getSudoku());

            main.notesMode();

            for (int i = 0; i < 3; i++) {
                main.select(notePosition[i]);
                main.insert(number);
            }

            main.notesMode();
            main.select(numberPosition);
            main.insert(number);

            for (int i = 0; i < 3; i++)
                assertArrayEquals(noNotes, main.game.getNumber(notePosition[i]).getNotes());
        });

        // With check-notes off
        mainActivity.onActivity(main -> {
            Data.instance(main).saveBoolean(Data.SETTINGS_CHECK_NOTES, false);
        });

        mainActivity.onActivity(main -> {
            // clear sudoku
            main.game = new Sudoku();
            main.sudokuGrid.init(main.game.getSudoku());

            main.notesMode();

            for (int i = 0; i < 3; i++) {
                main.select(notePosition[i]);
                main.insert(number);
            }

            main.notesMode();
            main.select(numberPosition);
            main.insert(number);

            for (int i = 0; i < 3; i++) {
                assertTrue(main.game.getNumber(notePosition[i]).isNotes());
                assertArrayEquals(hasNote, main.game.getNumber(notePosition[i]).getNotes());
            }
        });
    }

    // TODO: colors, reset
}
