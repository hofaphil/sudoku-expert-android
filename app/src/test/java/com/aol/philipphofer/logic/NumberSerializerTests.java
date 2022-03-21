package com.aol.philipphofer.logic;

import static org.junit.Assert.assertEquals;

import com.aol.philipphofer.logic.help.NumberSerializer;
import com.aol.philipphofer.logic.sudoku.Number;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NumberSerializerTests {

    @Test
    public void test() {
        Number n = new Number(1, 3, true);
        String s = NumberSerializer.numberToString(n);
        assertEquals(n, NumberSerializer.numberFromString(s));

        n = new Number( 2, 2, false);
        s = NumberSerializer.numberToString(n);
        assertEquals(n, NumberSerializer.numberFromString(s));

        n = new Number(3, 3, true);
        n.insert(4, true);
        s = NumberSerializer.numberToString(n);
        assertEquals(n, NumberSerializer.numberFromString(s));
    }
}
