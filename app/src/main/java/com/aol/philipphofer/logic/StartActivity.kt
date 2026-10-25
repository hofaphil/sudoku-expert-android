package com.aol.philipphofer.logic

import android.app.Activity
import android.content.Intent
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.Home
import com.aol.philipphofer.gui.custom.CustomToast
import com.aol.philipphofer.logic.ShareClass.load
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.persistence.Data

class StartActivity : Activity() {

    override fun onStart() {
        super.onStart()

        val data = Data.instance(this)
        val sharedLink = intent.action == Intent.ACTION_VIEW

        // opened through a shared sudoku link: the game waits on the home screen until it is played or dismissed
        if (sharedLink) {
            try {
                val sudoku = load(intent.data)
                data.saveSharedGame(sudoku, Difficulty.getDifficulty(difficulty))
            } catch (e: Exception) {
                CustomToast(this, resources.getString(R.string.error_default)).show()
            }
        }

        // a fresh home screen shows the shared game, also when the app was already open
        val home = Intent(this, Home::class.java)
        if (sharedLink) home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(home)
        finish()
    }

    companion object {
        var difficulty = 0
    }
}