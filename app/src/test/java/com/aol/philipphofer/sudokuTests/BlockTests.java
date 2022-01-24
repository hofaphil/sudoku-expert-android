package com.aol.philipphofer.sudokuTests;

import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.logic.sudoku.Block;
import com.aol.philipphofer.logic.sudoku.Number;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BlockTests {

    @Mock
    private Number numberMock;

    private Block testBlock;

    Position position = new Position(1, 2, 3);
    private final int correctNumber = 3, falseNumber = 4;

    @Before
    public void setup() {
        testBlock = new Block();
        testBlock.setNumber(1, 2, numberMock);

        when(numberMock.insert(eq(correctNumber), anyBoolean())).thenReturn(true);
        when(numberMock.insert(eq(falseNumber), eq(false))).thenReturn(false);
    }

    /**
     * Constructor
     */
    @Test
    public void testBlock() {
        Block block = new Block();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                assertNotNull(block.getNumber(i, j));
    }

    /**
     * public boolean insert(int number, Position position, boolean note)
     */
    @Test
    public void testInsert() {
        boolean returnVal = testBlock.insert(correctNumber, position, false);

        assertTrue(returnVal);
        verify(numberMock, times(1)).insert(correctNumber, false);
        verifyNoMoreInteractions(numberMock);
    }

    @Test
    public void testInsertNote() {
        boolean returnVal = testBlock.insert(correctNumber, position, true);

        assertTrue(returnVal);
        verify(numberMock, times(1)).insert(correctNumber, true);
        verifyNoMoreInteractions(numberMock);
    }

    @Test
    public void testInsertError() {
        boolean returnVal = testBlock.insert(falseNumber, position, false);

        assertFalse(returnVal);
        verify(numberMock, times(1)).insert(falseNumber, false);
        verifyNoMoreInteractions(numberMock);
    }

    /**
     * public void delete(Position position)
     */
    @Test
    public void testDelete() {
        testBlock.delete(position);

        verify(numberMock, times(1)).delete();
        verifyNoMoreInteractions(numberMock);
    }

    /**
     * public Number getNumber(int row, int column)
     */
    @Test
    public void testGetNumbers() {
        Number number = testBlock.getNumber(position.row, position.column);

        assertEquals(numberMock, number);
    }

    /**
     * public void setNumber(int row, int col, Number number)
     */
    @Test
    public void testSetNumber() {
        Number n = new Number();
        testBlock.setNumber(position.row, position.column, n);

        assertEquals(n, testBlock.getNumber(position.row, position.column));
    }
}
