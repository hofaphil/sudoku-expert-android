package com.aol.philipphofer.sudokuTests;

import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.logic.sudoku.Block;
import com.aol.philipphofer.logic.sudoku.Number;
import com.aol.philipphofer.logic.sudoku.Sudoku;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class SudokuTests {

    @Mock
    Block blockMock;

    Sudoku testSudoku;

    Position position = new Position(3, 2, 1);
    private final int correctNumber = 3, falseNumber = 4;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        testSudoku = new Sudoku();

        Block[] blocks = new Block[9];
        for (int i = 0; i < 9; i++)
            blocks[i] = new Block();
        blocks[position.getBlock()] = blockMock;

        Field blocksField = testSudoku.getClass().getDeclaredField("blocks");
        blocksField.setAccessible(true);
        blocksField.set(testSudoku, blocks);

        when(blockMock.insert(eq(correctNumber), any(Position.class), anyBoolean())).thenReturn(true);
        when(blockMock.insert(eq(falseNumber), any(Position.class), eq(false))).thenReturn(false);
        when(blockMock.insert(anyInt(), any(Position.class), eq(true))).thenReturn(true);
    }

    /**
     * Constructor
     */
    @Test
    public void testSudoku() {
        Sudoku sudoku = new Sudoku();
        Arrays.stream(sudoku.getBlocks()).forEach(Assert::assertNotNull);
        assertEquals(0, sudoku.getOverallErrors());
    }

    /**
     * void insert(int Number, Position position, boolean note)
     */
    @Test
    public void testInsert() {
        testSudoku.insert(correctNumber, position, false);

        assertEquals(0, testSudoku.getOverallErrors());
        verify(blockMock, times(1)).insert(correctNumber, position, false);
        verifyNoMoreInteractions(blockMock);
    }

    @Test
    public void testInsertNote() {
        testSudoku.insert(falseNumber, position, true);

        assertEquals(0, testSudoku.getOverallErrors());
        verify(blockMock, times(1)).insert(falseNumber, position, true);
        verifyNoMoreInteractions(blockMock);
    }

    @Test
    public void testInsertError() {
        testSudoku.insert(falseNumber, position, false);

        assertEquals(1, testSudoku.getOverallErrors());

        testSudoku.insert(falseNumber, position, false);

        assertEquals(2, testSudoku.getOverallErrors());
        verify(blockMock, times(2)).insert(falseNumber, position, false);
        verifyNoMoreInteractions(blockMock);
    }

    /**
     * void delete(Position position)
     */
    @Test
    public void testDelete() {
        testSudoku.delete(position);

        assertEquals(0, testSudoku.getOverallErrors());
        verify(blockMock, times(1)).delete(position);
        verifyNoMoreInteractions(blockMock);
    }

    /**
     * int freeField()
     */
    @Test
    public void testFreeFields() {
        Sudoku sudoku = new Sudoku();
        assertEquals(81, sudoku.freeFields());

        sudoku.insert(correctNumber, position, false);
        assertEquals(80, sudoku.freeFields());

        sudoku.insert(correctNumber, position, false);
        assertEquals(81, sudoku.freeFields());

        sudoku.insert(correctNumber, position, false);
        assertEquals(80, sudoku.freeFields());

        sudoku.insert(falseNumber, position, false);
        assertEquals(80, sudoku.freeFields());

        sudoku.insert(correctNumber, position, true);
        assertEquals(81, sudoku.freeFields());

        sudoku.insert(correctNumber, position, true);
        assertEquals(81, sudoku.freeFields());
    }

    /**
     * int currentErrors()
     */
    @Test
    public void testCurrentErrors() {
        Sudoku sudoku = new Sudoku();
        Number number = new Number(0, correctNumber, true);
        sudoku.setNumber(position, number);
        assertEquals(0, sudoku.currentErrors());

        sudoku.insert(correctNumber, position, false);
        assertEquals(0, sudoku.currentErrors());

        sudoku.insert(falseNumber, position, false);
        assertEquals(1, sudoku.currentErrors());

        sudoku.insert(falseNumber, position, true);
        assertEquals(0, sudoku.currentErrors());
    }

    /**
     * Number getNumber(Position position)
     * Number setNumber(Position position, Number number)
     */
    @Test
    public void testGetSetNumber() {
        Sudoku sudoku = new Sudoku();
        Number number = new Number(0, correctNumber, true);
        sudoku.setNumber(position, number);

        assertEquals(number, sudoku.getNumber(position));
    }
}
