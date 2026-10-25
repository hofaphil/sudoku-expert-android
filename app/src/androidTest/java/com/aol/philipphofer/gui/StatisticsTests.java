package com.aol.philipphofer.gui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.Timer;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.persistence.Data;
import com.aol.philipphofer.persistence.GameRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StatisticsTests {

    public ActivityScenario<Home> home;

    @Before
    public void setup() {
        Data d = Data.Constants.instance(InstrumentationRegistry.getInstrumentation().getTargetContext());
        d.drop();

        // Add some games
        d.addGame(new GameRecord(1L, Difficulty.BEGINNER, true, 10));
        d.addGame(new GameRecord(2L, Difficulty.BEGINNER, true, 20));
        // solved without a recorded time: played, but not part of the times
        d.addGame(new GameRecord(3L, Difficulty.BEGINNER, true, null));
        // lost: not counted as played
        d.addGame(new GameRecord(4L, Difficulty.BEGINNER, false, null));
        d.addGame(new GameRecord(5L, Difficulty.ADVANCED, true, 100));

        // the statistics are a tab of the home screen
        home = ActivityScenario.launch(Home.class);
        onView(ViewMatchers.withId(R.id.statisticsTab)).perform(click());
    }

    @Test
    public void testCorrectlyDisplayed() {
        // overall is selected first: 4 solved, 3 of them timed
        assertStatistics(Timer.timeToString(43), Timer.timeToString(10), Timer.timeToString(130), "4");

        onView(ViewMatchers.withId(R.id.statisticsBeginner)).perform(click());
        assertStatistics(Timer.timeToString(15), Timer.timeToString(10), Timer.timeToString(30), "3");

        onView(ViewMatchers.withId(R.id.statisticsAdvanced)).perform(click());
        assertStatistics(Timer.timeToString(100), Timer.timeToString(100), Timer.timeToString(100), "1");

        // no expert games: a hint instead of the statistics
        onView(ViewMatchers.withId(R.id.statisticsExpert)).perform(click());
        onView(ViewMatchers.withId(R.id.statisticsEmpty)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.statisticsBest)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.statisticsDetails)).check(matches(not(isDisplayed())));

        onView(ViewMatchers.withId(R.id.statisticsOverall)).perform(click());
        onView(ViewMatchers.withId(R.id.statisticsEmpty)).check(matches(not(isDisplayed())));
        assertStatistics(Timer.timeToString(43), Timer.timeToString(10), Timer.timeToString(130), "4");
    }

    private void assertStatistics(String average, String best, String total, String played) {
        onView(ViewMatchers.withId(R.id.statisticsAverageTime)).check(matches(withText(average)));
        onView(ViewMatchers.withId(R.id.statisticsBestTime)).check(matches(withText(best)));
        onView(ViewMatchers.withId(R.id.statisticsTotalTime)).check(matches(withText(total)));
        onView(ViewMatchers.withId(R.id.statisticsTimesPlayed)).check(matches(withText(played)));
    }
}
