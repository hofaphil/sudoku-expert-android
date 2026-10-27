package com.aol.philipphofer.logic

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.aol.philipphofer.R
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.logic.help.LinkShortener.getId
import com.aol.philipphofer.logic.help.LinkShortener.getLink
import com.aol.philipphofer.logic.sudoku.Number
import com.aol.philipphofer.logic.sudoku.Sudoku
import java.util.zip.DataFormatException

object ShareClass {

    private const val HOST = "https://sudoku-expert.com"

    // difficulty + 81 numbers
    private const val ID_LENGTH = 82

    @JvmStatic
    fun share(sudoku: Sudoku, difficulty: Difficulty, context: Activity): String {
        val id = StringBuilder()
        id.append(difficulty.number)

        for (b in 0..8)
            for (i in 0..2)
                for (j in 0..2) {
                    // only share the given numbers, not the player's entries
                    val number = sudoku.getNumber(Position(b, i, j))
                    id.append(if (number.isChangeable) 0 else number.number)
                }

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sudoku")

        val shareMessage = context.resources.getString(R.string.share_description)
        val shareLink = HOST + "/share?id=" + getLink(id.toString())
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage + "\n\n" + shareLink + "\n");

        context.startActivity(
            Intent.createChooser(
                shareIntent,
                context.resources.getString(R.string.share_title)
            )
        )

        return shareLink
    }

    @Throws(Exception::class)
    @JvmStatic
    fun load(uri: Uri?): Sudoku {
        if (uri == null) throw DataFormatException()

        val link = uri.getQueryParameter("id") ?: throw DataFormatException("Missing id")
        val id = getId(link)
        if (id.length != ID_LENGTH || !id.all { it in '0'..'9' })
            throw DataFormatException("Invalid id")

        val difficulty = id[0].digitToInt()
        if (Difficulty.entries.none { it.number == difficulty })
            throw DataFormatException("Invalid difficulty")

        // TODO: not nice!
        StartActivity.difficulty = difficulty

        val sudoku = Sudoku()
        var k = 1
        for (b in 0..8)
            for (i in 0..2)
                for (j in 0..2) {
                    val position = Position(b, i, j)
                    val numberValue = id[k++].digitToInt()
                    val number = Number(numberValue, numberValue, numberValue == 0)

                    sudoku.setNumber(position, number)
                }

        if (!solveSudokuNative(sudoku)) throw DataFormatException("Sudoku has no solution")
        return sudoku
    }

    // JNI
    init {
        System.loadLibrary("generator-jni")
    }

    external fun solveSudokuNative(sudoku: Sudoku?): Boolean
}