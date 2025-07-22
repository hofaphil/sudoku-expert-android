package com.aol.philipphofer.helper;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.test.espresso.matcher.BoundedMatcher;

import com.aol.philipphofer.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class CustomMatchers {

    public static final int[] keys = {
            R.id.num1, R.id.num2, R.id.num3, R.id.num4, R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9
    };

    public static final int[][] btn = {
            {R.id.btn00, R.id.btn01, R.id.btn02},
            {R.id.btn10, R.id.btn11, R.id.btn12},
            {R.id.btn20, R.id.btn21, R.id.btn22}
    };

    public static final int[] blocks = {
            R.id.blk0, R.id.blk1, R.id.blk2, R.id.blk3, R.id.blk4, R.id.blk5, R.id.blk6, R.id.blk7, R.id.blk8
    };

    public static Matcher<View> backgroundColor(final int backgroundColorId) {
        return new BoundedMatcher<>(View.class) {
            @Override
            public boolean matchesSafely(final View view) {
                Drawable background = view.getBackground();
                if (background instanceof ColorDrawable) {
                    int expected = ((ColorDrawable) background).getColor();
                    int actual = view.getContext().getColor(backgroundColorId);
                    return expected == actual;
                }
                return false;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("The background color of the resource " + backgroundColorId);
            }
        };
    }
}
