package com.aol.philipphofer.gui.custom

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aol.philipphofer.R
import com.aol.philipphofer.persistence.Data

// ComponentActivity for the back handling of AndroidX, the views stay the platform ones
abstract class CustomActivity : ComponentActivity() {

    protected lateinit var data: Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = Data.instance(this)
    }

    override fun onContentChanged() {
        super.onContentChanged()

        val root = findViewById<ViewGroup>(android.R.id.content).getChildAt(0) ?: return
        val topBar = findViewById<View>(R.id.statusBar) ?: findViewById(R.id.title) ?: return
        applySystemBarInsets(root, topBar)
    }

    /**
     * Edge-to-edge: the top bar reaches behind the status bar,
     * the root view keeps its content clear of the navigation bar and display cutouts.
     */
    private fun applySystemBarInsets(root: View, topBar: View) {
        val topBarPaddingTop = topBar.paddingTop
        val rootPaddingLeft = root.paddingLeft
        val rootPaddingRight = root.paddingRight
        val rootPaddingBottom = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { _, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            topBar.setPadding(
                topBar.paddingLeft,
                topBarPaddingTop + insets.top,
                topBar.paddingRight,
                topBar.paddingBottom
            )
            root.setPadding(
                rootPaddingLeft + insets.left,
                root.paddingTop,
                rootPaddingRight + insets.right,
                rootPaddingBottom + insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }
}
