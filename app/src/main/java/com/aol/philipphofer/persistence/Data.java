package com.aol.philipphofer.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.sudoku.SudokuField;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.sudoku.Block;
import com.aol.philipphofer.sudoku.Number;
import com.aol.philipphofer.sudoku.Sudoku;
import com.google.gson.Gson;

public class Data {

    private static Data unique = null;
    private final SharedPreferences data;
    private final SharedPreferences.Editor editor;

    private final static String NAME = "data";

    private final static String SUDOKU_GAME_NAME = "game-field";
    private final static String SUDOKU_FIELD_NAME = "sudoku-field";

    private final static String LOAD_MODE = "loadmode";

    // for fields
    private final static String FIELD_IS_CHANGEABLE = "changeable";
    private final static String FIELD_IS_NOTES = "isnotes";
    private final static String FIELD_NUMBER = "fieldnumber";
    private final static String FIELD_ERROR = "fielderror";
    private final static String FIELD_NOTES = "notes";

    // for settings
    public final static String SETTINGS_MARK_LINES = "marklines";
    public final static String SETTINGS_MARK_NUMBERS = "marknumbers";
    public final static String SETTINGS_MARK_ERRORS = "markerrors";
    public final static String SETTINGS_CHECK_NOTES = "checknotes";
    public final static String SETTINGS_SHOW_TIME = "showtime";
    public final static String SETTINGS_COLOR = "color";
    // this functionality does not exist anymore, but some people might have bought this
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

    /* public void saveField(SudokuField field) {
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
    } */

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

    public void saveGameNumber(Number number, Position position) {
        Gson gson = new Gson();
        System.out.println(gson.toJson(number));
        editor.putString(SUDOKU_GAME_NAME + position, gson.toJson(number));

        editor.commit();
    }

    public void saveSudoku(Sudoku sudoku) {
        Gson gson = new Gson();
        int k = 0;
        for (int h = 0; h < 9; h++)
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    try {
                        editor.putString(SUDOKU_FIELD_NAME + new Position(i, j, h), gson.toJson(sudoku.getSudoku()[h].getNumbers()[i][j]));
                        editor.putString(SUDOKU_GAME_NAME + new Position(i, j, h), gson.toJson(sudoku.getGame()[h].getNumbers()[i][j]));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
        editor.commit();
    }

    public Sudoku loadSudoku() {
        Gson gson = new Gson();
        Sudoku sudoku = new Sudoku();

        int k = 0;
        for (int h = 0; h < 9; h++)
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    String sudokuJson = data.getString(SUDOKU_FIELD_NAME + new Position(i, j, h), "");
                    if (sudokuJson != null && !sudokuJson.isEmpty())
                        sudoku.getSudoku()[h].getNumbers()[i][j] = gson.fromJson(sudokuJson, Number.class);
                    else
                        sudoku.getSudoku()[h].getNumbers()[i][j] = new Number();

                    String gameJson = data.getString(SUDOKU_GAME_NAME + new Position(i, j, h), "");
                    System.out.println(gameJson);
                    if (gameJson != null && !gameJson.isEmpty())
                        sudoku.getGame()[h].getNumbers()[i][j] = gson.fromJson(gameJson, Number.class);
                    else
                        sudoku.getGame()[h].getNumbers()[i][j] = new Number();
                }

        return sudoku;
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