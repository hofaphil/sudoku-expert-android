package com.aol.philipphofer.gui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.aol.philipphofer.R;
import com.aol.philipphofer.helper.TestHelpers;
import com.aol.philipphofer.logic.ShareClass;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.logic.sudoku.Sudoku;
import com.aol.philipphofer.persistence.Data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A game shared by a friend waits on the home screen until it is played or dismissed.
 */
@RunWith(AndroidJUnit4.class)
public class SharedGameTests {

    private static final String link = "https://sudoku-expert.com/share?id=m6i9o8jk7la5qa58k63d4595293p6on491a23b6b4c4a9c2b6a";

    private Data data;
    private Sudoku shared;

    @Before
    public void setup() throws Exception {
        data = Data.Constants.instance(InstrumentationRegistry.getInstrumentation().getTargetContext());
        data.drop();

        shared = ShareClass.load(Uri.parse(link));
        data.saveSharedGame(shared, Difficulty.EXPERT);
    }

    @Test
    public void testPlaySharedGame() {
        try (ActivityScenario<Home> ignored = ActivityScenario.launch(Home.class)) {
            // waits on the home screen, the current game is not touched
            assertFalse(data.getLoadmode());
            onView(ViewMatchers.withId(R.id.sharedGame)).check(matches(isDisplayed()));
            onView(ViewMatchers.withId(R.id.sharedDifficulty)).check(matches(withText(R.string.expert)));

            onView(ViewMatchers.withId(R.id.sharedPlay)).perform(click());
            TestHelpers.wait(1000);

            // now it is the current game, the card is gone
            assertTrue(data.getLoadmode());
            assertEquals(shared, data.loadSudoku());
            assertEquals(Difficulty.EXPERT.getNumber(), data.loadInt(Data.GAME_DIFFICULTY));
            assertNull(data.sharedGameDifficulty());
        }
    }

    @Test
    public void testDismissSharedGame() {
        try (ActivityScenario<Home> ignored = ActivityScenario.launch(Home.class)) {
            onView(ViewMatchers.withId(R.id.sharedDismiss)).perform(click());

            onView(ViewMatchers.withId(R.id.sharedGame))
                    .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
            assertNull(data.sharedGameDifficulty());
            assertFalse(data.getLoadmode());
        }
    }
}
