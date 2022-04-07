package com.aol.philipphofer.gui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.Timer;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.persistence.Data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StatisticsTests {

    public ActivityScenario<Statistics> statistics;

    @Before
    public void setup() {
        Data d = Data.instance(InstrumentationRegistry.getInstrumentation().getContext());
        d.drop();

        // Add some games
        d.addTime(10, Difficulty.BEGINNER);
        d.addTime(20, Difficulty.BEGINNER);
        d.addTime(100, Difficulty.ADVANCED);

        statistics = ActivityScenario.launch(Statistics.class);
    }

    @Test
    public void testCorrectlyDisplayed() {
        ViewInteraction beginnerAverageTime = onView(ViewMatchers.withId(R.id.Beginner_Average_Time));
        ViewInteraction beginnerBestTime = onView(ViewMatchers.withId(R.id.Beginner_Best_Time));
        ViewInteraction beginnerTimesPlayed = onView(ViewMatchers.withId(R.id.Beginner_TimesPlayed_Count));

        beginnerAverageTime.check(matches(withText(Timer.timeToString(15))));
        beginnerBestTime.check(matches(withText(Timer.timeToString(10))));
        beginnerTimesPlayed.check(matches(withText("2")));

        ViewInteraction advancedAverageTime = onView(ViewMatchers.withId(R.id.Advanced_Average_Time));
        ViewInteraction advancedBestTime = onView(ViewMatchers.withId(R.id.Advanced_Best_Time));
        ViewInteraction advancedTimesPlayed = onView(ViewMatchers.withId(R.id.Advanced_TimesPlayed_Count));

        advancedAverageTime.check(matches(withText(Timer.timeToString(100))));
        advancedBestTime.check(matches(withText(Timer.timeToString(100))));
        advancedTimesPlayed.check(matches(withText("1")));

        ViewInteraction expertAverageTime = onView(ViewMatchers.withId(R.id.Expert_Average_Time));
        ViewInteraction expertBestTime = onView(ViewMatchers.withId(R.id.Expert_Best_Time));
        ViewInteraction expertTimesPlayed = onView(ViewMatchers.withId(R.id.Expert_TimesPlayed_Count));

        expertAverageTime.check(matches(withText(Timer.timeToString(0))));
        expertBestTime.check(matches(withText(Timer.timeToString(0))));
        expertTimesPlayed.check(matches(withText("0")));
    }
}
