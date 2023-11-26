package com.aol.philipphofer.gui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Timer;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.persistence.Data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StatusBarTests {

    ViewInteraction moreButton = onView(ViewMatchers.withId(R.id.moreButton));
    ViewInteraction newButton = onView(ViewMatchers.withId(R.id.newButton));
    ViewInteraction timeView = onView(ViewMatchers.withId(R.id.timeView));
    ViewInteraction difficultyView = onView(ViewMatchers.withId(R.id.difficultyView));
    ViewInteraction errorView = onView(ViewMatchers.withId(R.id.errorView));

    public ActivityScenario<MainActivity> mainActivity;

    @Before
    public void setup() {
        Data.instance(InstrumentationRegistry.getInstrumentation().getContext()).drop();
        mainActivity = ActivityScenario.launch(MainActivity.class);

        testVisibility();
    }

    @After
    public void teardown() {
        testVisibility();
    }

    // TODO: add tests for more button and new button

    @Test
    public void testInitialState() {
        difficultyView.check(matches(withText(Difficulty.BEGINNER.getText(InstrumentationRegistry.getInstrumentation().getTargetContext()))));
        errorView.check(matches(withText(InstrumentationRegistry
                .getInstrumentation().getTargetContext().getResources()
                .getString(R.string.statusbar_errors, 0, MainActivity.MAX_ERROR))));
        timeView.check(matches(withText(Timer.timeToString(0))));
        moreButton.check(matches(isEnabled()));
        newButton.check(matches(isEnabled()));
    }

    @Test
    public void testSetDifficulty() {
        mainActivity.onActivity(mainActivity -> mainActivity.statusBar.setDifficulty(Difficulty.EXPERT));
        difficultyView.check(matches(withText(Difficulty.EXPERT.getText(InstrumentationRegistry.getInstrumentation().getTargetContext()))));

        mainActivity.onActivity(mainActivity -> mainActivity.statusBar.setDifficulty(Difficulty.BEGINNER));
        difficultyView.check(matches(withText(Difficulty.BEGINNER.getText(InstrumentationRegistry.getInstrumentation().getTargetContext()))));
    }

    @Test
    public void testSetError() {
        mainActivity.onActivity(mainActivity -> mainActivity.statusBar.setError(2));
        errorView.check(matches(withText(InstrumentationRegistry
                .getInstrumentation().getTargetContext().getResources()
                .getString(R.string.statusbar_errors, 2, MainActivity.MAX_ERROR))));

        mainActivity.onActivity(mainActivity -> mainActivity.statusBar.setError(1));
        errorView.check(matches(withText(InstrumentationRegistry
                .getInstrumentation().getTargetContext().getResources()
                .getString(R.string.statusbar_errors, 1, MainActivity.MAX_ERROR))));
    }

    @Test
    public void testSetTime() {
        mainActivity.onActivity(mainActivity -> mainActivity.statusBar.setTime(61));
        timeView.check(matches(withText(Timer.timeToString(61))));

        mainActivity.onActivity(mainActivity -> mainActivity.statusBar.setTime(2222));
        timeView.check(matches(withText(Timer.timeToString(2222))));

        mainActivity.onActivity(mainActivity -> mainActivity.statusBar.setTime(4322));
        timeView.check(matches(withText(Timer.timeToString(4322))));
    }

    @Test
    public void testActivate() {
        mainActivity.onActivity(mainActivity -> mainActivity.statusBar.activate(false));
        moreButton.check(matches(isNotEnabled()));
        newButton.check(matches(isNotEnabled()));

        mainActivity.onActivity(mainActivity -> mainActivity.statusBar.activate(true));
        moreButton.check(matches(isEnabled()));
        newButton.check(matches(isEnabled()));
    }

    /**
     * helper
     */
    private void testVisibility() {
        timeView.check(matches(isDisplayed()));
        newButton.check(matches(isDisplayed()));
        moreButton.check(matches(isDisplayed()));
        difficultyView.check(matches(isDisplayed()));
        errorView.check(matches(isDisplayed()));
    }
}
