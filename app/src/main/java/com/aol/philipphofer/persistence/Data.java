package com.aol.philipphofer.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.logic.sudoku.Number;
import com.aol.philipphofer.logic.sudoku.Sudoku;
import com.google.gson.Gson;

public class Data {

    private static Data unique = null;
    private final SharedPreferences data;
    private final SharedPreferences.Editor editor;

    private final static String NAME = "data";

    // for game loading
    private final static String SUDOKU_FIELD_NAME = "sudokufield";
    private final static String LOAD_MODE = "loadmode";

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
    public final static String GAME_DIFFICULTY = "difficulty";
    public final static String GAME_ERRORS = "errors";
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
    }

    public static Data instance(Context context) {
        if (unique == null)
            unique = new Data(context);
        return unique;
    }

    // datatype save and load methods
    public void saveInt(String key, int value) {
        editor.putInt(key, value).apply();
    }

    public int loadInt(String key) {
        return data.getInt(key, 0);
    }

    public void saveBoolean(String key, boolean bool) {
        editor.putBoolean(key, bool).apply();
    }

    public boolean loadBoolean(String key) {
        return data.getBoolean(key, true);
    }

    public boolean loadBoolean(String key, boolean defValue) {
        return data.getBoolean(key, defValue);
    }

    // sudoku save and load methods
    public void saveGameNumber(Number number, Position position) {
        editor.putString(SUDOKU_FIELD_NAME + position, new Gson().toJson(number)).apply();
    }

    public void saveSudoku(Sudoku sudoku) {
        Gson gson = new Gson();
        for (int h = 0; h < 9; h++)
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    try {
                        Position position = new Position(h, i, j);
                        editor.putString(SUDOKU_FIELD_NAME + position, gson.toJson(sudoku.getNumber(position)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        editor.putInt(GAME_ERRORS, sudoku.overallErrors).apply();
    }

    public Sudoku loadSudoku() {
        Gson gson = new Gson();
        Sudoku sudoku = new Sudoku();

        for (int h = 0; h < 9; h++)
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    Position position = new Position(h, i, j);
                    String sudokuJson = data.getString(SUDOKU_FIELD_NAME + position, "");
                    if (sudokuJson != null && !sudokuJson.isEmpty())
                        sudoku.getSudoku()[h].getNumbers()[i][j] = gson.fromJson(sudokuJson, Number.class);
                    else
                        sudoku.getSudoku()[h].getNumbers()[i][j] = new Number();
                }
        sudoku.overallErrors = data.getInt(GAME_ERRORS, 0);
        return sudoku;
    }

    // game save and load methods
    public boolean getLoadmode() {
        return data.getBoolean(LOAD_MODE, false);
    }

    public void setLoadmode(boolean loadmode) {
        editor.putBoolean(LOAD_MODE, loadmode);
        editor.apply();
    }

    // TODO: change to statistics model
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
        editor.apply();
    }

    // other
    public void drop() {
        boolean supporter = loadBoolean(SETTINGS_SUPPORTER, false);

        editor.clear();

        editor.putBoolean(SETTINGS_SUPPORTER, supporter);
        editor.commit();
    }

}