package com.aol.philipphofer.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import com.aol.philipphofer.logic.sudoku.Number;
import com.aol.philipphofer.logic.sudoku.Sudoku;
import com.aol.philipphofer.persistence.Data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class DataTests {

    @Mock
    private SharedPreferences dataMock;

    @Mock
    private SharedPreferences.Editor editorMock;

    @Mock
    private Context contextMock;

    private Data testData;
    private Map<String, Object> storageMock;

    private final String TEST_KEY = "test-key";

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        storageMock = new HashMap<>();

        when(contextMock.getSharedPreferences(anyString(), anyInt())).thenReturn(dataMock);
        when(dataMock.edit()).thenReturn(editorMock);

        when(dataMock.getBoolean(anyString(), anyBoolean())).then((Answer<Boolean>) invocation -> (Boolean) storageMock.getOrDefault((String) invocation.getArgument(0), (Boolean) invocation.getArgument(1)));
        when(dataMock.getInt(anyString(), anyInt())).then((Answer<Integer>) invocation -> (Integer) storageMock.getOrDefault((String) invocation.getArgument(0), (Integer) invocation.getArgument(1)));
        when(dataMock.getString(anyString(), anyString())).then((Answer<String>) invocation -> (String) storageMock.getOrDefault((String) invocation.getArgument(0), (String) invocation.getArgument(1)));

        when(editorMock.putBoolean(anyString(), anyBoolean())).then((Answer<SharedPreferences.Editor>) invocation -> {
            storageMock.put(invocation.getArgument(0), invocation.getArgument(1));
            return editorMock;
        });
        when(editorMock.putInt(anyString(), anyInt())).then((Answer<SharedPreferences.Editor>) invocation -> {
            storageMock.put(invocation.getArgument(0), invocation.getArgument(1));
            return editorMock;
        });
        when(editorMock.putString(anyString(), anyString())).then((Answer<SharedPreferences.Editor>) invocation -> {
            storageMock.put(invocation.getArgument(0), invocation.getArgument(1));
            return editorMock;
        });

        // reset singleton
        Field instance = Data.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        testData = Data.Constants.instance(contextMock);
    }

    /**
     * void saveBoolean(String key, boolean bool)
     * boolean loadBoolean(String key)
     * boolean loadBoolean(String key, boolean defValue)
     */
    @Test
    public void testSaveBoolean() {
        testData.saveBoolean(TEST_KEY, false);
        assertFalse(testData.loadBoolean(TEST_KEY));

        testData.saveBoolean(TEST_KEY, true);
        assertTrue(testData.loadBoolean(TEST_KEY));

        verify(editorMock, times(2)).apply();
    }

    @Test
    public void testLoadBoolean() {
        assertTrue(testData.loadBoolean(TEST_KEY));

        testData.saveBoolean(TEST_KEY, false);
        assertFalse(testData.loadBoolean(TEST_KEY));
    }

    @Test
    public void testLoadBooleanWithDefault() {
        assertFalse(testData.loadBoolean(TEST_KEY, false));

        testData.saveBoolean(TEST_KEY, true);
        assertTrue(testData.loadBoolean(TEST_KEY));
    }

    /**
     * void saveInt(String key, int value)
     * int loadInt(String key)
     */
    @Test
    public void testSaveInt() {
        testData.saveInt(TEST_KEY, 3);
        assertEquals(3, testData.loadInt(TEST_KEY));

        testData.saveInt(TEST_KEY, 42);
        assertEquals(42, testData.loadInt(TEST_KEY));

        verify(editorMock, times(2)).apply();
    }

    @Test
    public void testLoadInt() {
        assertEquals(0, testData.loadInt(TEST_KEY));

        testData.saveInt(TEST_KEY, 34);
        assertEquals(34, testData.loadInt(TEST_KEY));
    }

    /**
     * void saveGameNumber(Number number, Position position)
     */
    @Test
    public void testSaveGameNumber() {
        final Number number = new Number(2, 3, false);
        final Position position = new Position(4, 2, 2);

        testData.saveGameNumber(number, position);

        verify(editorMock, times(1)).apply();
        verify(editorMock, times(1)).putString(anyString(), anyString());
    }

    /**
     * void saveSudoku(Sudoku sudoku)
     */
    @Test
    public void testSaveSudoku() {
        Sudoku sudoku = new Sudoku();

        testData.saveSudoku(sudoku);

        verify(editorMock, times(1)).apply();
        verify(editorMock, times(81)).putString(anyString(), anyString());
    }

    /**
     * Sudoku loadSudoku()
     */
    @Test
    public void testLoadSudoku() {
        Sudoku sudoku = new Sudoku();
        sudoku.insert(3, new Position(1, 1, 1), false);
        sudoku.insert(3, new Position(1, 1, 1), true);
        testData.saveSudoku(sudoku);

        Sudoku loaded = testData.loadSudoku();

        verify(dataMock, times(81)).getString(anyString(), anyString());
        assertEquals(sudoku, loaded);
    }

    @Test
    public void testLoadSudokuEmpty() {
        Sudoku loaded = testData.loadSudoku();

        verify(dataMock, times(81)).getString(anyString(), anyString());
        assertEquals(new Sudoku(), loaded);
    }

    /**
     * boolean getLoadmode()
     * void setLoadmode(boolean loadmode)
     */
    @Test
    public void testSetGetLoadmode() {
        boolean loadmode = testData.getLoadmode();

        assertFalse(loadmode);

        testData.setLoadmode(true);
        assertTrue(testData.getLoadmode());
    }

    // TODO: test addTime but with new data structure
}
