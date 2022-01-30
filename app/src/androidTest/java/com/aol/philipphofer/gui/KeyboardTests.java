package com.aol.philipphofer.gui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.not;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.persistence.Data;

import java.util.Random;


@RunWith(AndroidJUnit4.class)
public class KeyboardTests {

    private static final int[] keys = {
            R.id.num1, R.id.num2, R.id.num3, R.id.num4, R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9
    };

    private static final int[][] btn = {
            {R.id.btn00, R.id.btn01, R.id.btn02},
            {R.id.btn10, R.id.btn11, R.id.btn12},
            {R.id.btn20, R.id.btn21, R.id.btn22}
    };

    private static final int[] blocks = {
            R.id.blk0, R.id.blk1, R.id.blk2, R.id.blk3, R.id.blk4, R.id.blk5, R.id.blk6, R.id.blk7, R.id.blk8
    };

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
            Data.instance(main).drop();
            for (int b = 0; b < 9; b++)
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++) {
                        position = new Position(b, i, j);
                        if (main.game.getNumber(position).isChangeable()) {
                            main.select(position);
                            selectedField = onView(allOf(
                                    ViewMatchers.withId(btn[position.row][position.column]),
                                    ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.block]))
                            ));
                            selectedFieldNumber = onView(allOf(
                                    ViewMatchers.isDescendantOfA(ViewMatchers.withId(btn[position.row][position.column])),
                                    ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.block])),
                                    ViewMatchers.withId(R.id.numberView)
                            ));
                            selectedFieldNotes = onView(allOf(
                                    ViewMatchers.isDescendantOfA(ViewMatchers.withId(btn[position.row][position.column])),
                                    ViewMatchers.isDescendantOfA(ViewMatchers.withId(blocks[position.block])),
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

        // everything should be disabled
        for (int key : keys)
            onView(ViewMatchers.withId(key)).check(matches(isNotEnabled()));
        noteButton.check(matches(isNotEnabled()));
        deleteButton.check(matches(isNotEnabled()));
        pauseButton.check(matches(backgroundColor(R.color.yellow)));
        noteButton.check(matches(backgroundColor(R.color.yellow)));
        selectedField.check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.grid)).check(matches(not(isDisplayed())));

        pauseButton.perform(click());

        // everything should be enabled again
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

    private static Matcher<View> backgroundColor(final int backgroundColorId) {
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public boolean matchesSafely(final View view) {
                Drawable background = view.getBackground();
                if (background instanceof ColorDrawable) {
                    int expected = ((ColorDrawable) background).getColor();
                    int actual = view.getContext().getColor(backgroundColorId);
                    return expected == actual;
                }
                return false;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("The background color of the resource " + backgroundColorId);
            }
        };
    }
}
