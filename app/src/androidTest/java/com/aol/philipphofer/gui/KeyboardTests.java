package com.aol.philipphofer.gui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static org.hamcrest.CoreMatchers.not;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
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

@RunWith(AndroidJUnit4.class)
public class KeyboardTests {

    private static final int[] keys = {
            R.id.num1, R.id.num2, R.id.num3, R.id.num4, R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9
    };

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivity = new ActivityScenarioRule<>(MainActivity.class);

    ViewInteraction pauseButton = onView(ViewMatchers.withId(R.id.pause));
    ViewInteraction noteButton = onView(ViewMatchers.withId(R.id.notes));
    ViewInteraction deleteButton = onView(ViewMatchers.withId(R.id.delete));

    @Test
    public void testPause() {
        pauseButton = onView(ViewMatchers.withId(R.id.pause));
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
        onView(ViewMatchers.withId(R.id.grid)).check(matches(not(isDisplayed())));

        pauseButton.perform(click());

        // everything should be enabled again
        for (int key : keys)
            onView(ViewMatchers.withId(key)).check(matches(isEnabled()));
        noteButton.check(matches(isEnabled()));
        deleteButton.check(matches(isEnabled()));
        pauseButton.check(matches(backgroundColor(R.color.transparent)));
        noteButton.check(matches(backgroundColor(R.color.yellow)));
        onView(ViewMatchers.withId(R.id.grid)).check(matches(isDisplayed()));
    }

    @Test
    public void testNote() {
        noteButton = onView(ViewMatchers.withId(R.id.notes));
        noteButton.perform(click());

        noteButton.check(matches(backgroundColor(R.color.yellow)));

        noteButton.perform(click());

        noteButton.check(matches(backgroundColor(R.color.transparent)));
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
