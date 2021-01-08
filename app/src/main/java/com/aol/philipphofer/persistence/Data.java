package com.aol.philipphofer.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.sudoku.SudokuField;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.sudoku.Block;
import com.aol.philipphofer.sudoku.Sudoku;

public class Data {

    private static Data unique = null;
    private final SharedPreferences data;
    private final SharedPreferences.Editor editor;

    private final static String NAME = "data";

    private final static String SOLUTION_FIELD_NAME = "solutionfield";
    private final static String SUDOKU_FIELD_NAME = "sudokufieldname";

    private final static String LOAD_MODE = "loadmode";

    // for fields
    private final static String FIELD_IS_CHANGEABLE = "changeable";
    private final static String FIELD_IS_NOTES = "isnotes";
    private final static String FIELD_NUMBER = "fieldnumber";
    private final static String FIELD_ERROR = "fielderror";
    private final static String FIELD_NOTES = "notes";

    // for settings
    public final static String SETTINGS_POWERMODE = "powermode";
    public final static String SETTINGS_MARK_LINES = "marklines";
    public final static String SETTINGS_MARK_NUMBERS = "marknumbers";
    public final static String SETTINGS_MARK_ERRORS = "markerrors";
    public final static String SETTINGS_CHECK_NOTES = "checknotes";
    public final static String SETTINGS_SHOW_TIME = "showtime";
    public final static String SETTINGS_COLOR = "color";
    public final static String SETTINGS_SUPPORTER = "supporter";

    // for actual game
    public final static String GAME_ERRORS = "errors";
    public final static String GAME_DIFFICULTY = "difficulty";
    public final static String GAME_TIME = "time";
    public final static String GAME_SHOW_ERRORS = "main_show_errors";
    public final static String GAME_SHOW_TIME = "main_show_time";

    // for statistics
    public static final String STATISTICS_BESTTIME = "besttime";
    public static final String STATISTICS_TIMEOVERALL = "timeoverall";
    public static final String STATISTICS_TIMESPLAYED = "timesplayed";

    private Data(Context context) {
        data = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = data.edit();
        editor.apply();
    }

    public static Data instance(Context context) {
        if (unique == null)
            unique = new Data(context);
        return unique;
    }

    public void saveField(SudokuField field) {
        editor.putBoolean(field.position + FIELD_IS_CHANGEABLE, field.isChangeable());
        editor.putBoolean(field.position + FIELD_IS_NOTES, field.isNotes());
        editor.putBoolean(field.position + FIELD_ERROR, field.getError());
        if (field.isNotes())
            for (int i = 0; i < 9; i++)
                editor.putBoolean(field.position + FIELD_NOTES + i, field.getNotes()[i]);
        else
            editor.putInt(field.position + FIELD_NUMBER, field.getNumber());
        editor.apply();
    }

    public void loadField(SudokuField field) {
        field.setIsNotes(data.getBoolean(field.position + FIELD_IS_NOTES, false));
        field.switchLayout(field.isNotes());
        field.setError(data.getBoolean(field.position + FIELD_ERROR, false));
        field.setChangeable(data.getBoolean(field.position + FIELD_IS_CHANGEABLE, false));
        if (field.isNotes()) {
            boolean[] bool = new boolean[9];
            for (int i = 0; i < 9; i++)
                bool[i] = data.getBoolean(field.position + FIELD_NOTES + i, false);
            field.setNotes(bool);
        } else {
            field.setNumber(data.getInt(field.position + FIELD_NUMBER, 0));
            if (field.getNumber() > 0)
                field.setNumberViewText(field.getNumber());
        }
    }

    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int loadInt(String key) {
        return data.getInt(key, 0);
    }

    public void saveBoolean(String key, boolean bool) {
        editor.putBoolean(key, bool);
        editor.apply();
    }

    public boolean loadBoolean(String key) {
        return data.getBoolean(key, true);
    }

    public boolean loadBoolean(String key, boolean defValue) {
        return data.getBoolean(key, defValue);
    }

    public void saveString(String key, String string) {
        editor.putString(key, string);
        editor.apply();
    }

    public String loadString(String key, String defValue) {
        return data.getString(key, defValue);
    }

    public void saveSolution(Block[] blocks) {
        int k = 0;
        for (int i = 0; i < 9; i++)
            for (int a = 0; a < 3; a++)
                for (int b = 0; b < 3; b++)
                    editor.putInt(SOLUTION_FIELD_NAME + k++, blocks[i].getNumbers()[a][b]);
        editor.apply();
    }

    public void loadSolution(Sudoku sudoku) {
        int k = 0;
        Block[] block = new Block[9];
        int[][] numbers = new int[3][3];
        for (int i = 0; i < 9; i++) {
            for (int a = 0; a < 3; a++)
                for (int b = 0; b < 3; b++)
                    numbers[a][b] = data.getInt(SOLUTION_FIELD_NAME + k++, 0);
            block[i] = new Block();
            block[i].setNumbers(numbers);
        }
        sudoku.setSolution(block);
    }

    public void saveSudoku(Block[] blocks) {
        int k = 0;
        for (int i = 0; i < 9; i++)
            for (int a = 0; a < 3; a++)
                for (int b = 0; b < 3; b++)
                    editor.putInt(SUDOKU_FIELD_NAME + k++, blocks[i].getNumbers()[a][b]);
        editor.apply();
    }

    public void loadSudoku(Sudoku sudoku) {
        int k = 0;
        Block[] block = new Block[9];
        int[][] numbers = new int[3][3];
        for (int i = 0; i < 9; i++) {
            for (int a = 0; a < 3; a++)
                for (int b = 0; b < 3; b++)
                    numbers[a][b] = data.getInt(SUDOKU_FIELD_NAME + k++, 0);
            block[i] = new Block();
            block[i].setNumbers(numbers);
        }
        sudoku.setSudoku(block);
    }

    public boolean getLoadmode() {
        return data.getBoolean(LOAD_MODE, false);
    }

    public void setLoadmode(boolean loadmode) {
        editor.putBoolean(LOAD_MODE, loadmode);
        editor.commit();
    }

    public void addTime(int time, Difficulty difficulty) {
        int timesPlayed = loadInt(Data.STATISTICS_TIMESPLAYED + difficulty.getNumber()) + 1;
        int timeOverall = loadInt(Data.STATISTICS_TIMEOVERALL + difficulty.getNumber()) + time;
        saveInt(Data.STATISTICS_TIMESPLAYED + difficulty.getNumber(), timesPlayed);
        saveInt(Data.STATISTICS_TIMEOVERALL + difficulty.getNumber(), timeOverall);
        if (loadInt(Data.STATISTICS_BESTTIME + difficulty.getNumber()) > time ||
                loadInt(Data.STATISTICS_BESTTIME + difficulty.getNumber()) == 0)
            saveInt(Data.STATISTICS_BESTTIME + difficulty.getNumber(), time);
    }

    public int getTheme() {
        // Hacky, but before there was a string saved
        try {
            return data.getInt(SETTINGS_COLOR, R.style.AppTheme);
        } catch (Exception e) {
            return R.style.AppTheme;
        }
    }

    public void saveTheme(int color) {
        switch (color) {
            case 1:
                editor.putInt(SETTINGS_COLOR, R.style.AppTheme_Green);
                break;
            case 2:
                editor.putInt(SETTINGS_COLOR, R.style.AppTheme_Blue);
                break;
            case 3:
                editor.putInt(SETTINGS_COLOR, R.style.AppTheme_Orange);
                break;
            default:
                editor.putInt(SETTINGS_COLOR, R.style.AppTheme);
                break;
        }
        editor.commit();
    }

    public void drop() {
        boolean supporter = loadBoolean(SETTINGS_SUPPORTER, false);

        editor.clear();

        editor.putBoolean(SETTINGS_SUPPORTER, supporter);
        editor.commit();
    }

}