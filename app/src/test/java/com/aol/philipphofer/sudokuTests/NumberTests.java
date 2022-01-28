package com.aol.philipphofer.sudokuTests;

import static org.junit.Assert.*;

import com.aol.philipphofer.logic.sudoku.Number;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NumberTests {

    public final int correctNumber = 2, falseNumber = 3;

    /**
     * Constructors
     */
    @Test
    public void testNumber() {
        Number number = new Number();

        assertEquals(0, number.getNumber());
        assertEquals(0, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertTrue(number.isChangeable());
        assertFalse(number.isNotes());
        assertFalse(number.isError());
    }

    @Test
    public void testNumberWithTwoParameter() {
        boolean isChangeable = true;
        Number number = new Number(correctNumber, correctNumber, isChangeable);

        assertEquals(correctNumber, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertFalse(number.isError());
    }

    @Test
    public void testNumberWithTwoParameterError() {
        boolean isChangeable = true;
        Number number = new Number(falseNumber, correctNumber, isChangeable);

        assertEquals(falseNumber, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertTrue(number.isError());
    }

    /**
     * int getNumber()
     */
    @Test
    public void testGetNumber() {
        boolean isChangeable = true;
        Number number = new Number(falseNumber, correctNumber, isChangeable);

        assertEquals(falseNumber, number.getNumber());
    }

    /**
     * boolean[] getNotes()
     */
    @Test
    public void testGetNotes() {
        boolean isChangeable = true;
        Number number = new Number(falseNumber, correctNumber, isChangeable);
        number.insert(falseNumber, true);
        boolean[] expected = new boolean[9];
        expected[falseNumber - 1] = true;

        assertArrayEquals(expected, number.getNotes());
    }

    /**
     * int getSolution()
     */
    @Test
    public void testGetSolution() {
        boolean isChangeable = true;
        Number number = new Number(falseNumber, correctNumber, isChangeable);

        assertEquals(correctNumber, number.getSolution());
    }

    /**
     * void setSolution(int solution)
     */
    @Test
    public void testSetSolution() {
        boolean isChangeable = true;
        Number number = new Number(falseNumber, falseNumber, isChangeable);

        number.setSolution(correctNumber);

        assertEquals(falseNumber, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertTrue(number.isError());
    }

    /**
     * boolean insert(int number, boolean note)
     */
    @Test
    public void testInsertNumber() {
        boolean isChangeable = true;
        Number number = new Number(0, correctNumber, isChangeable);
        // insert note to check if it gets deleted right
        number.insert(correctNumber, true);

        boolean success = number.insert(falseNumber, false);
        assertTrue(number.isError());
        assertFalse(success);

        success = number.insert(correctNumber, false);
        assertTrue(success);

        assertEquals(correctNumber, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertFalse(number.isError());
    }

    @Test
    public void testInsertNumberTwoTimes() {
        boolean isChangeable = true;
        Number number = new Number(0, correctNumber, isChangeable);

        boolean success = number.insert(falseNumber, false);
        assertTrue(number.isError());
        assertFalse(success);

        success = number.insert(falseNumber, false);
        assertTrue(success);

        assertEquals(0, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertFalse(number.isError());
    }

    @Test
    public void testInsertNote() {
        boolean isChangeable = true;
        Number number = new Number(falseNumber, correctNumber, isChangeable);
        boolean[] expected = new boolean[9];
        expected[falseNumber - 1] = true;

        boolean success = number.insert(falseNumber, true);

        assertTrue(success);
        assertEquals(0, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(expected, number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertTrue(number.isNotes());
        assertFalse(number.isError());
    }

    @Test
    public void testInsertNoteTwoTims() {
        boolean isChangeable = true;
        Number number = new Number(falseNumber, correctNumber, isChangeable);

        number.insert(falseNumber, true);
        boolean success = number.insert(falseNumber, true);

        assertTrue(success);
        assertEquals(0, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertTrue(number.isNotes());
        assertFalse(number.isError());
    }

    @Test
    public void testInsertNotChangeable() {
        Number number = new Number(correctNumber, correctNumber, false);

        number.insert(correctNumber, false);

        assertEquals(correctNumber, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertFalse(number.isChangeable());
        assertFalse(number.isNotes());
        assertFalse(number.isError());

        number.insert(correctNumber, true);

        assertEquals(correctNumber, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertFalse(number.isChangeable());
        assertFalse(number.isNotes());
        assertFalse(number.isError());
    }

    /**
     * void delete()
     */
    @Test
    public void testDeleteNumber() {
        boolean isChangeable = true;
        Number number = new Number(falseNumber, correctNumber, isChangeable);

        number.delete();

        assertEquals(0, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isError());
    }

    @Test
    public void testDeleteNote() {
        boolean isChangeable = true;
        Number number = new Number(falseNumber, correctNumber, isChangeable);
        number.insert(falseNumber, true);

        number.delete();

        assertEquals(0, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isError());
    }

    @Test
    public void testDeleteNotChangeable() {
        boolean isChangeable = false;
        Number number = new Number(correctNumber, correctNumber, isChangeable);

        number.delete();

        assertEquals(correctNumber, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertFalse(number.isError());
    }

    /**
     * void checkNote(int number)
     */
    @Test
    public void testCheckNote() {
        boolean isChangeable = true;
        Number number = new Number(falseNumber, correctNumber, isChangeable);
        number.insert(falseNumber, true);

        number.checkNote(falseNumber);

        assertEquals(0, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isError());
    }

    @Test
    public void testCheckNoteNotContained() {
        boolean isChangeable = true;
        Number number = new Number(correctNumber, correctNumber, isChangeable);
        number.insert(correctNumber, true);
        boolean[] expected = number.getNotes();

        number.checkNote(falseNumber);

        assertEquals(0, number.getNumber());
        assertEquals(correctNumber, number.getSolution());
        assertArrayEquals(expected, number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isError());
    }
}
