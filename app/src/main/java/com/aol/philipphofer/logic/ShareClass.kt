package com.aol.philipphofer.logic

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.aol.philipphofer.R
import com.aol.philipphofer.logic.help.LinkShortener.getId
import com.aol.philipphofer.logic.help.LinkShortener.getLink
import com.aol.philipphofer.logic.sudoku.Number
import com.aol.philipphofer.logic.sudoku.Sudoku
import java.util.zip.DataFormatException

internal object ShareClass {

    private const val URL = "https://philipphofer.de/"

    @JvmStatic
    fun share(sudoku: Sudoku, context: Activity): String {
        val id = StringBuilder()
        id.append(MainActivity.DIFFICULTY.number)

        for (b in 0..8)
            for (i in 0..2)
                for (j in 0..2)
                    id.append(sudoku.getNumber(Position(b, i, j)).number)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sudoku")

        val shareMessage = context.resources.getString(R.string.share_description)
        val shareLink = URL + "share?id=" + getLink(id.toString())
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

        val link = uri.getQueryParameter("id")!!
        val id = getId(link)
        val difficulty = (id[0].toString() + "").toInt()
        if (difficulty < 0 || difficulty > 3) throw Exception("Difficulty is wrong!")

        // TODO: not nice!
        StartActivity.difficulty = difficulty

        val sudoku = Sudoku()
        var k = 1
        for (b in 0..8)
            for (i in 0..2)
                for (j in 0..2) {
                    val position = Position(b, i, j)
                    val numberValue = (id[k++].toString() + "").toInt()
                    val number = Number(numberValue, numberValue, numberValue == 0)

                    sudoku.setNumber(position, number)
                }

        solveSudokuNative(sudoku)
        return sudoku
    }

    // JNI
    init {
        System.loadLibrary("generator-jni")
    }

    external fun solveSudokuNative(sudoku: Sudoku?)
}