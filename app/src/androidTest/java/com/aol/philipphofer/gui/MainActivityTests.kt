package com.aol.philipphofer.gui

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aol.philipphofer.logic.MainActivity
import com.aol.philipphofer.logic.Position
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.logic.sudoku.Sudoku
import com.aol.philipphofer.persistence.Data
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTests {

    private lateinit var mainActivity: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        Data.instance(InstrumentationRegistry.getInstrumentation().context).drop()
        mainActivity = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        mainActivity.close()
    }

    /**
     * first start of the app
     */
    @Test
    fun testStartup() {
        mainActivity.onActivity { mainActivity: MainActivity ->
            // check modes
            Assert.assertTrue(MainActivity.LOAD_MODE)
            Assert.assertFalse(MainActivity.SHARED)

            // difficulty
            Assert.assertEquals(Difficulty.BEGINNER, MainActivity.DIFFICULTY)

            // timer
            Assert.assertTrue(mainActivity.timer.isRunning)
            Assert.assertEquals(0, mainActivity.timer.time.toLong())

            // pause
            Assert.assertFalse(mainActivity.pause)

            // sudoku
            Assert.assertNotNull(mainActivity.game)
            Assert.assertEquals(
                MainActivity.DIFFICULTY.freeFields,
                mainActivity.game.freeFields()
            )

            // selected
            Assert.assertNull(mainActivity.selected)
        }
    }

    /**
     * restart of the app
     */
    @Test
    fun testRestart() {
        val sudoku = Sudoku()
        sudoku.overallErrors = 1
        val gameTime = 300
        mainActivity.onActivity { mainActivity: MainActivity ->
            var inserted = false
            for (b in 0..8)
                for (i in 0..2)
                    for (j in 0..2) {
                        val pos = Position(b, i, j)

                        if (!inserted && mainActivity.game.getNumber(pos).isChangeable) {
                            val solution = mainActivity.game.getNumber(pos).solution
                            mainActivity.select(pos)

                            // insert false number
                            mainActivity.insert(if (solution == 3) 4 else 3)
                            inserted = true
                        }

                        // save game state
                        sudoku.setNumber(pos, mainActivity.game.getNumber(pos))
                    }

            // set new timer
            mainActivity.timer.startTimer(gameTime)
            Assert.assertEquals(Difficulty.BEGINNER, MainActivity.DIFFICULTY)
        }

        // restart activity
        mainActivity.recreate()
        mainActivity.onActivity { mainActivity: MainActivity ->
            // check for equal sudoku
            Assert.assertEquals(sudoku, mainActivity.game)

            // check for correct game-time
            Assert.assertEquals(gameTime.toLong(), mainActivity.timer.time.toLong())

            // check for same errors
            Assert.assertEquals(1, mainActivity.game.currentErrors().toLong())
            Assert.assertEquals(1, mainActivity.game.overallErrors.toLong())
            Assert.assertEquals(Difficulty.BEGINNER, MainActivity.DIFFICULTY)
        }
    }
}