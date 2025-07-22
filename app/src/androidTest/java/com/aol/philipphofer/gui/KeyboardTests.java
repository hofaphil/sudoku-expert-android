package com.aol.philipphofer.gui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.aol.philipphofer.helper.CustomMatchers.backgroundColor;
import static com.aol.philipphofer.helper.CustomMatchers.btn;
import static com.aol.philipphofer.helper.CustomMatchers.blocks;
import static com.aol.philipphofer.helper.CustomMatchers.keys;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.persistence.Data;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class KeyboardTests {

    private Position position;

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivity = new ActivityScenarioRule<>(MainActivity.class);

    ViewInteraction pauseButton = onView(ViewMatchers.withId(R.id.pause));
    ViewInteraction noteButton = onView(ViewMatchers.withId(R.id.notes));
    ViewInteraction deleteButton = onView(ViewMatchers.withId(R.id.delete));
    ViewInteraction selectedField;
    ViewInteraction selectedFieldNumber;
    ViewInteraction selectedFieldNotes;

    @Before
    public void setup() {
        mainActivity.getScenario().onActivity(main -> {
            Data.Constants.instance(main).drop();
            for (int b = 0; b < 9; b++)
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++) {
                        position = new Position(b, i, j);
                        if (main.game.getNumber(position).isChangeable()) {
                            main.select(position);
                            selectedField = onView(allOf(
                                    ViewMatchers.withId(btn[position.getRow()][position.getColumn()]),
                                    ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.getBlock()]))
                            ));
                            selectedFieldNumber = onView(allOf(
                                    ViewMatchers.isDescendantOfA(ViewMatchers.withId(btn[position.getRow()][position.getColumn()])),
                                    ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.getBlock()])),
                                    ViewMatchers.withId(R.id.numberView)
                            ));
                            selectedFieldNotes = onView(allOf(
                                    ViewMatchers.isDescendantOfA(ViewMatchers.withId(btn[position.getRow()][position.getColumn()])),
                                    ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.getBlock()])),
                                    ViewMatchers.withId(R.id.notesGrid)
                            ));
                            return;
                        }
                    }
        });
    }

    @Test
    public void testPauseButton() {
        // check that the note button is not affected
        noteButton.perform(click());

        pauseButton.perform(click());

        // pause
        mainActivity.getScenario().onActivity(main -> {
            assertFalse(main.timer.isRunning());
        });

        for (int key : keys)
            onView(ViewMatchers.withId(key)).check(matches(isNotEnabled()));
        noteButton.check(matches(isNotEnabled()));
        deleteButton.check(matches(isNotEnabled()));
        pauseButton.check(matches(backgroundColor(R.color.yellow)));
        noteButton.check(matches(backgroundColor(R.color.yellow)));
        selectedField.check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.grid)).check(matches(not(isDisplayed())));

        pauseButton.perform(click());

        // no pause
        mainActivity.getScenario().onActivity(main -> {
            assertTrue(main.timer.isRunning());
        });

        for (int key : keys)
            onView(ViewMatchers.withId(key)).check(matches(isEnabled()));
        noteButton.check(matches(isEnabled()));
        deleteButton.check(matches(isEnabled()));
        pauseButton.check(matches(backgroundColor(R.color.transparent)));
        noteButton.check(matches(backgroundColor(R.color.yellow)));
        selectedField.check(matches(backgroundColor(R.color.selected)));
        onView(ViewMatchers.withId(R.id.grid)).check(matches(isDisplayed()));
    }

    @Test
    public void testNoteButton() {
        noteButton.perform(click());

        // everything should stay the same
        for (int key : keys)
            onView(ViewMatchers.withId(key)).check(matches(isEnabled()));
        noteButton.check(matches(isEnabled()));
        deleteButton.check(matches(isEnabled()));
        pauseButton.check(matches(backgroundColor(R.color.transparent)));
        noteButton.check(matches(backgroundColor(R.color.yellow)));
        selectedField.check(matches(backgroundColor(R.color.selected)));

        noteButton.perform(click());

        // everything should stay the same
        for (int key : keys)
            onView(ViewMatchers.withId(key)).check(matches(isEnabled()));
        noteButton.check(matches(isEnabled()));
        deleteButton.check(matches(isEnabled()));
        pauseButton.check(matches(backgroundColor(R.color.transparent)));
        noteButton.check(matches(backgroundColor(R.color.transparent)));
        selectedField.check(matches(backgroundColor(R.color.selected)));
    }

    @Test
    public void testDeleteButton() {
        int random = new Random().nextInt(9);
        onView(ViewMatchers.withId(keys[random])).perform(click());

        deleteButton.perform(click());

        // everything should stay the same
        for (int key : keys)
            onView(ViewMatchers.withId(key)).check(matches(isEnabled()));
        noteButton.check(matches(isEnabled()));
        deleteButton.check(matches(isEnabled()));
        pauseButton.check(matches(backgroundColor(R.color.transparent)));
        noteButton.check(matches(backgroundColor(R.color.transparent)));
        selectedField.check(matches(backgroundColor(R.color.selected)));

        // try second time without any number
        deleteButton.perform(click());

        // everything should stay the same
        for (int key : keys)
            onView(ViewMatchers.withId(key)).check(matches(isEnabled()));
        noteButton.check(matches(isEnabled()));
        deleteButton.check(matches(isEnabled()));
        pauseButton.check(matches(backgroundColor(R.color.transparent)));
        noteButton.check(matches(backgroundColor(R.color.transparent)));
        selectedField.check(matches(backgroundColor(R.color.selected)));
    }

    @Test
    public void testInsertNumber() {
        int random = new Random().nextInt(9);

        onView(ViewMatchers.withId(keys[random])).perform(click());

        // everything should stay the same
        for (int key : keys)
            onView(ViewMatchers.withId(key)).check(matches(isEnabled()));
        noteButton.check(matches(isEnabled()));
        deleteButton.check(matches(isEnabled()));
        pauseButton.check(matches(backgroundColor(R.color.transparent)));
        noteButton.check(matches(backgroundColor(R.color.transparent)));
        selectedField.check(matches(anyOf(backgroundColor(R.color.selected), backgroundColor(R.color.error))));

        // number should have been inserted
        selectedFieldNumber.check(matches(withText("" + (random + 1))));
    }


    @Test
    public void testInsertNote() {
        int random = new Random().nextInt(9);
        noteButton.perform(click());

        onView(ViewMatchers.withId(keys[random])).perform(click());

        // everything should stay the same
        for (int key : keys)
            onView(ViewMatchers.withId(key)).check(matches(isEnabled()));
        noteButton.check(matches(isEnabled()));
        deleteButton.check(matches(isEnabled()));
        pauseButton.check(matches(backgroundColor(R.color.transparent)));
        noteButton.check(matches(backgroundColor(R.color.yellow)));
        selectedField.check(matches(backgroundColor(R.color.selected)));

        // notes grid layout should have been inflated
        selectedFieldNotes.check(matches(isDisplayed()));
    }
}
