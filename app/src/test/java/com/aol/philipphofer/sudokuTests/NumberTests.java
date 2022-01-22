package com.aol.philipphofer.sudokuTests;

import static org.junit.Assert.*;

import com.aol.philipphofer.logic.sudoku.Number;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NumberTests {

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
        int num = 2, sol = 2;
        boolean isChangeable = true;
        Number number = new Number(num, sol, isChangeable);

        assertEquals(num, number.getNumber());
        assertEquals(sol, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertFalse(number.isError());
    }

    @Test
    public void testNumberWithTwoParameterError() {
        int num = 3, sol = 2;
        boolean isChangeable = true;
        Number number = new Number(num, sol, isChangeable);

        assertEquals(num, number.getNumber());
        assertEquals(sol, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertTrue(number.isError());
    }

    /**
     * public int getNumber()
     */
    @Test
    public void testGetNumber() {
        int num = 3, sol = 2;
        boolean isChangeable = true;
        Number number = new Number(num, sol, isChangeable);

        assertEquals(num, number.getNumber());
    }

    /**
     * public boolean[] getNotes()
     */
    @Test
    public void testGetNotes() {
        int num = 3, sol = 2;
        boolean isChangeable = true;
        Number number = new Number(num, sol, isChangeable);
        number.insert(num, true);
        boolean[] expected = new boolean[9];
        expected[num - 1] = true;

        assertArrayEquals(expected, number.getNotes());
    }

    /**
     * public int getSolution()
     */
    @Test
    public void testGetSolution() {
        int num = 3, sol = 2;
        boolean isChangeable = true;
        Number number = new Number(num, sol, isChangeable);

        assertEquals(sol, number.getSolution());
    }

    /**
     * public void setSolution(int solution)
     */
    @Test
    public void testSetSolution() {
        int num = 3, sol = 2;
        boolean isChangeable = true;
        Number number = new Number(num, num, isChangeable);

        number.setSolution(sol);

        assertEquals(num, number.getNumber());
        assertEquals(sol, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertTrue(number.isError());
    }

    /**
     * public boolean insert(int number, boolean note)
     */
    @Test
    public void testInsertNumber() {
        int num = 2, sol = 2;
        boolean isChangeable = true;
        Number number = new Number(0, sol, isChangeable);
        // insert note to check if it gets deleted right
        number.insert(num, true);

        boolean error = number.insert(num - 1, false);
        assertTrue(number.isError());
        assertTrue(error);

        error = number.insert(num, false);
        assertFalse(error);

        assertEquals(num, number.getNumber());
        assertEquals(sol, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertFalse(number.isError());
    }

    @Test
    public void testInsertNote() {
        int num = 3, sol = 2;
        boolean isChangeable = true;
        Number number = new Number(num, sol, isChangeable);
        boolean[] expected = new boolean[9];
        expected[num - 1] = true;

        boolean error = number.insert(num, true);

        assertFalse(error);
        assertEquals(0, number.getNumber());
        assertEquals(sol, number.getSolution());
        assertArrayEquals(expected, number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertTrue(number.isNotes());
        assertFalse(number.isError());
    }

    @Test
    public void testInsertNotChangeable() {
        int num = 3, sol = 3;
        boolean isChangeable = false;
        Number number = new Number(num, sol, isChangeable);

        number.insert(num, false);

        assertEquals(num, number.getNumber());
        assertEquals(sol, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertFalse(number.isError());

        number.insert(num, true);

        assertEquals(num, number.getNumber());
        assertEquals(sol, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertFalse(number.isError());
    }

    /**
     * public void delete()
     */
    @Test
    public void testDeleteNumber() {
        int num = 3, sol = 2;
        boolean isChangeable = true;
        Number number = new Number(num, sol, isChangeable);

        number.delete();

        assertEquals(0, number.getNumber());
        assertEquals(sol, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isError());
    }

    @Test
    public void testDeleteNote() {
        int num = 3, sol = 2;
        boolean isChangeable = true;
        Number number = new Number(num, sol, isChangeable);
        number.insert(num, true);

        number.delete();

        assertEquals(0, number.getNumber());
        assertEquals(sol, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isError());
    }

    @Test
    public void testDeleteNotChangeable() {
        int num = 3, sol = 3;
        boolean isChangeable = false;
        Number number = new Number(num, sol, isChangeable);

        number.delete();

        assertEquals(num, number.getNumber());
        assertEquals(sol, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isNotes());
        assertFalse(number.isError());
    }

    /**
     * public void checkNote(int number)
     */
    @Test
    public void testCheckNote() {
        int num = 3, sol = 2;
        boolean isChangeable = true;
        Number number = new Number(num, sol, isChangeable);
        number.insert(num, true);

        number.checkNote(num);

        assertEquals(0, number.getNumber());
        assertEquals(sol, number.getSolution());
        assertArrayEquals(new boolean[9], number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isError());
    }

    @Test
    public void testCheckNoteNotContained() {
        int num = 3, sol = 2;
        boolean isChangeable = true;
        Number number = new Number(num, sol, isChangeable);
        number.insert(num, true);
        boolean[] expected = number.getNotes();

        number.checkNote(num + 1);

        assertEquals(0, number.getNumber());
        assertEquals(sol, number.getSolution());
        assertArrayEquals(expected, number.getNotes());
        assertEquals(isChangeable, number.isChangeable());
        assertFalse(number.isError());
    }
}
