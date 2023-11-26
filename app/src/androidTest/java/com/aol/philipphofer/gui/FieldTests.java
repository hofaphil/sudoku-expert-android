package com.aol.philipphofer.gui;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.aol.philipphofer.helper.CustomMatchers.backgroundColor;
import static org.hamcrest.CoreMatchers.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.aol.philipphofer.R;
import com.aol.philipphofer.helper.TestHelpers;
import com.aol.philipphofer.helper.ViewFinder;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.persistence.Data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FieldTests {

    public ActivityScenario<MainActivity> mainActivity;

    public ViewInteraction selectedField, selectedFieldNumber;
    public Position position;
    public int solution;

    @Before
    public void setup() {
        Data.Constants.instance(InstrumentationRegistry.getInstrumentation().getContext()).drop();
        mainActivity = ActivityScenario.launch(MainActivity.class);
        mainActivity.onActivity(main -> {
            for (int b = 0; b < 9; b++)
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++) {
                        position = new Position(b, i, j);
                        if (main.game.getNumber(position).isChangeable()) {
                            solution = main.game.getNumber(position).getSolution();
                            main.select(position);
                            selectedField = ViewFinder.getField(position);
                            selectedFieldNumber = ViewFinder.getFieldNumber(position);
                            return;
                        }
                    }
        });
    }

    @After
    public void tearDown() {
        mainActivity.close();
    }

    @Test
    public void insertNumber() {
        mainActivity.onActivity(main -> main.insert(solution));

        selectedField.check(matches(backgroundColor(R.color.yellow)));
        selectedFieldNumber.check(matches(withText("" + solution)));

        mainActivity.onActivity(main -> main.insert(solution));

        selectedField.check(matches(backgroundColor(R.color.yellow)));
        selectedFieldNumber.check(matches(withText("")));
    }

    @Test
    public void insertNumberError() {
        int falseNumber = TestHelpers.generateFalseNumber(solution);
        mainActivity.onActivity(main -> main.insert(falseNumber));

        selectedField.check(matches(backgroundColor(R.color.error)));
        selectedFieldNumber.check(matches(withText("" + falseNumber)));

        mainActivity.onActivity(main -> main.insert(falseNumber));

        selectedField.check(matches(backgroundColor(R.color.yellow)));
        selectedFieldNumber.check(matches(withText("")));
    }

    @Test
    public void insertNote() {
        int number = TestHelpers.generateFalseNumber(solution);
        mainActivity.onActivity(main -> {
            main.notesMode();
            main.insert(number);
        });

        ViewInteraction selectedFieldGrid = ViewFinder.getFieldGrid(position);
        ViewInteraction selectedFieldGridNote = ViewFinder.getFieldGridNote(position, number);
        selectedFieldGrid.check(matches(isDisplayed()));
        selectedFieldGridNote.check(matches(isDisplayed()));
        selectedFieldNumber.check(matches(withText("")));
        selectedField.check(matches(backgroundColor(R.color.yellow)));
        for (int i = 1; i < 10; i++) {
            if (i == number)
                continue;
            ViewFinder.getFieldGridNote(position, i).check(matches(not(isDisplayed())));
        }

        mainActivity.onActivity(main -> main.insert(number));

        selectedFieldGrid.check(matches(isDisplayed()));
        selectedFieldGridNote.check(matches(not(isDisplayed())));
        selectedField.check(matches(backgroundColor(R.color.yellow)));
        selectedFieldNumber.check(matches(withText("")));
        for (int i = 1; i < 10; i++)
            ViewFinder.getFieldGridNote(position, i).check(matches(not(isDisplayed())));
    }

    @Test
    public void insertMultipleNotes() {
        int number = TestHelpers.generateFalseNumber(solution);
        mainActivity.onActivity(main -> {
            main.notesMode();
            main.insert(number);
            main.insert(solution);
        });

        ViewInteraction selectedFieldGrid = ViewFinder.getFieldGrid(position);
        selectedFieldGrid.check(matches(isDisplayed()));

        ViewInteraction selectedFieldGridNote1 = ViewFinder.getFieldGridNote(position, number);
        ViewInteraction selectedFieldGridNote2 = ViewFinder.getFieldGridNote(position, solution);
        selectedFieldGridNote1.check(matches(isDisplayed()));
        selectedFieldGridNote2.check(matches(isDisplayed()));

        selectedFieldNumber.check(matches(withText("")));
        selectedField.check(matches(backgroundColor(R.color.yellow)));
        for (int i = 1; i < 10; i++) {
            if (i == number || i == solution)
                continue;
            ViewFinder.getFieldGridNote(position, i).check(matches(not(isDisplayed())));
        }

        mainActivity.onActivity(main -> main.insert(number));

        selectedFieldGrid.check(matches(isDisplayed()));
        selectedFieldGridNote1.check(matches(not(isDisplayed())));
        selectedFieldGridNote2.check(matches(isDisplayed()));
        selectedField.check(matches(backgroundColor(R.color.yellow)));
        selectedFieldNumber.check(matches(withText("")));
        for (int i = 1; i < 10; i++) {
            if (i == solution)
                continue;
            ViewFinder.getFieldGridNote(position, i).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void insertNoteWhenNumber() {
        int number = TestHelpers.generateFalseNumber(solution);
        mainActivity.onActivity(main -> {
            main.insert(number);
            main.notesMode();
            main.insert(number);
        });

        ViewInteraction selectedFieldGrid = ViewFinder.getFieldGrid(position);
        ViewInteraction selectedFieldGridNote = ViewFinder.getFieldGridNote(position, number);
        selectedFieldGrid.check(matches(isDisplayed()));
        selectedFieldGridNote.check(matches(isDisplayed()));
        selectedFieldNumber.check(matches(withText("")));
        selectedField.check(matches(backgroundColor(R.color.yellow)));
        for (int i = 1; i < 10; i++) {
            if (i == number)
                continue;
            ViewFinder.getFieldGridNote(position, i).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void insertNumberWhenNote() {
        int number = TestHelpers.generateFalseNumber(solution);
        mainActivity.onActivity(main -> {
            main.notesMode();
            main.insert(number);
            main.notesMode();
            main.insert(number);
        });

        ViewInteraction selectedFieldGrid = ViewFinder.getFieldGrid(position);
        ViewInteraction selectedFieldGridNote = ViewFinder.getFieldGridNote(position, number);
        selectedFieldGrid.check(matches(not(isDisplayed())));
        selectedFieldGridNote.check(matches(not(isDisplayed())));
        selectedFieldNumber.check(matches(withText("" + number)));
        selectedField.check(matches(backgroundColor(R.color.error)));
        for (int i = 1; i < 10; i++)
            ViewFinder.getFieldGridNote(position, i).check(matches(not(isDisplayed())));
    }
}
