package com.aol.philipphofer.sudokuTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.logic.sudoku.Block;
import com.aol.philipphofer.logic.sudoku.Number;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BlockTests {

    @Mock
    private Number numberMock;

    private Block testBlock;

    Position position = new Position(3, 1, 2);
    private final int correctNumber = 3, falseNumber = 4;

    @Before
    public void setup() {
        testBlock = new Block();
        testBlock.setNumber(1, 2, numberMock);

        when(numberMock.insert(eq(correctNumber), anyBoolean())).thenReturn(true);
        when(numberMock.insert(eq(falseNumber), eq(false))).thenReturn(false);
        when(numberMock.insert(anyInt(), eq(true))).thenReturn(true);
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
     * boolean insert(int number, Position position, boolean note)
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
        boolean returnVal = testBlock.insert(falseNumber, position, true);

        assertTrue(returnVal);
        verify(numberMock, times(1)).insert(falseNumber, true);
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
     * void delete(Position position)
     */
    @Test
    public void testDelete() {
        testBlock.delete(position);

        verify(numberMock, times(1)).delete();
        verifyNoMoreInteractions(numberMock);
    }

    /**
     * Number getNumber(int row, int column)
     */
    @Test
    public void testGetNumbers() {
        Number number = testBlock.getNumber(position.getRow(), position.getColumn());

        assertEquals(numberMock, number);
    }

    /**
     * void setNumber(int row, int col, Number number)
     */
    @Test
    public void testSetNumber() {
        Number n = new Number();
        testBlock.setNumber(position.getRow(), position.getColumn(), n);

        assertEquals(n, testBlock.getNumber(position.getRow(), position.getColumn()));
    }
}
