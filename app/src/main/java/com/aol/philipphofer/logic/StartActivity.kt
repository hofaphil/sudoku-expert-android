package com.aol.philipphofer.logic

import android.app.Activity
import android.content.Intent
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomToast
import com.aol.philipphofer.logic.ShareClass.load
import com.aol.philipphofer.logic.sudoku.Sudoku
import com.aol.philipphofer.persistence.Data

class StartActivity : Activity() {

    override fun onStart() {
        super.onStart()

        val sudoku: Sudoku
        val data = Data.instance(this)

        var intent = intent
        // opened through a shared sudoku link
        if (intent.action == Intent.ACTION_VIEW) {
            val uri = getIntent().data
            try {
                sudoku = load(uri)
                data.saveSudoku(sudoku)
                data.saveInt(Data.GAME_DIFFICULTY, difficulty)
                data.saveInt(Data.GAME_ERRORS, 0)
                data.saveInt(Data.GAME_TIME, 0)
                data.loadmode = true
            } catch (e: Exception) {
                CustomToast(this, resources.getString(R.string.error_default)).show()
            }
        }

        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        var difficulty = 0
    }
}