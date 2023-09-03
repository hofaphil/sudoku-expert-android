package com.aol.philipphofer.logic;

import static com.aol.philipphofer.persistence.Data.GAME_SHOW_ERRORS;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.Keyboard;
import com.aol.philipphofer.gui.StatusBar;
import com.aol.philipphofer.gui.custom.CustomActivity;
import com.aol.philipphofer.gui.custom.CustomToast;
import com.aol.philipphofer.gui.dialog.EndCardDialog;
import com.aol.philipphofer.gui.sudoku.SudokuField;
import com.aol.philipphofer.gui.sudoku.SudokuGrid;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.logic.help.DifficultyKt;
import com.aol.philipphofer.logic.sudoku.Block;
import com.aol.philipphofer.logic.sudoku.Number;
import com.aol.philipphofer.logic.sudoku.Sudoku;
import com.aol.philipphofer.persistence.Data;

public class MainActivity extends CustomActivity implements Timer.TimerListener {

    public Sudoku game;

    public StatusBar statusBar;
    public SudokuGrid sudokuGrid;
    public Keyboard keyboard;
    public EndCardDialog endCardDialog;

    public Position selected;
    public boolean isNotes;
    public boolean pause;

    public Timer timer;

    // TODO: not static!
    public static int MAX_ERROR = 3;
    public static Difficulty DIFFICULTY = Difficulty.BEGINNER;

    public static boolean LOAD_MODE = false;
    public static boolean SHARED = false;

    private Thread t = new Thread();

    private final static int[][] partnerBlockLookup = {
            {1, 2, 3, 6},
            {0, 2, 4, 7},
            {0, 1, 5, 8},
            {4, 5, 0, 6},
            {3, 5, 1, 7},
            {3, 4, 2, 8},
            {7, 8, 0, 3},
            {6, 8, 1, 4},
            {6, 7, 2, 5},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sudokuGrid = findViewById(R.id.sudokuGrid);
        statusBar = findViewById(R.id.statusBar);
        keyboard = findViewById(R.id.keyboard);
        endCardDialog = new EndCardDialog(this);

        timer = new Timer(this);
        timer.startTimer(0);

        ViewTreeObserver observer = sudokuGrid.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                init();
                sudokuGrid.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    protected void init() {
        ViewGroup.LayoutParams params = sudokuGrid.getLayoutParams();

        RelativeLayout mainLayout = findViewById(R.id.mainLayout);

        int width = mainLayout.getWidth();
        int height = mainLayout.getHeight() -
                statusBar.getHeight() -
                ((RelativeLayout.LayoutParams) statusBar.getLayoutParams()).bottomMargin -
                ((RelativeLayout.LayoutParams) statusBar.getLayoutParams()).topMargin -
                keyboard.getHeight() -
                ((RelativeLayout.LayoutParams) keyboard.getLayoutParams()).bottomMargin -
                ((RelativeLayout.LayoutParams) keyboard.getLayoutParams()).topMargin;

        if (height < width) {
            params.width = height;
        } else {
            RelativeLayout.LayoutParams paramsKeyboard = (RelativeLayout.LayoutParams) keyboard.getLayoutParams();
            paramsKeyboard.bottomMargin = (int) (Math.abs(width - height) / 1.5);
            keyboard.setLayoutParams(paramsKeyboard);
        }
        sudokuGrid.setLayoutParams(params);
    }

    @Override
    protected void onStart() {
        super.onStart();

        game = new Sudoku();

        // Load a game from data
        if (LOAD_MODE = data.getLoadmode()) {
            game = data.loadSudoku();
            sudokuGrid.init(game.getSudoku());

            statusBar.activate(true);
            keyboard.activate(true);

            sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        pause = true;
        pauseGame();

        // create a new game
        if (!(LOAD_MODE = data.getLoadmode())) {
            sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.LOADING);

            keyboard.activate(false);
            statusBar.activate(false);

            if (!t.isAlive()) {
                t = new Thread(this::heavyLoading);
                t.start();
            }
        } else {
            getWindow().getDecorView().post(() -> timer.startTimer(data.loadInt(Data.GAME_TIME)));
        }

        DIFFICULTY = DifficultyKt.getDifficulty(data.loadInt(Data.GAME_DIFFICULTY));
        statusBar.setDifficulty(DIFFICULTY);
        statusBar.setError(game.overallErrors);
    }

    public void heavyLoading() {
        timer.stopTimer();
        game = createSudokuNative(DIFFICULTY.getFreeFields());

        // game created -> save and set load-mode to load sudoku next time
        data.saveSudoku(game);
        data.setLoadmode(LOAD_MODE = !LOAD_MODE);

        // save the setting that apply at first next game
        data.saveBoolean(GAME_SHOW_ERRORS, data.loadBoolean(Data.SETTINGS_MARK_ERRORS));
        data.saveBoolean(Data.GAME_SHOW_TIME, data.loadBoolean(Data.SETTINGS_SHOW_TIME));

        // reset game errors and time
        data.saveInt(Data.GAME_ERRORS, 0);
        data.saveInt(Data.GAME_TIME, 0);

        runOnUiThread(() -> {
            sudokuGrid.init(game.getSudoku());

            sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.VISIBLE);

            statusBar.activate(true);
            keyboard.activate(true);

            statusBar.setError(game.overallErrors);

            getWindow().getDecorView().post(() -> timer.startTimer(0));
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        timer.stopTimer();
        data.saveInt(Data.GAME_TIME, timer.getTime());
        data.saveInt(Data.GAME_ERRORS, game.overallErrors);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.killTimer();
    }

    public void select(Position selectedPosition) {
        SudokuField sudokuField;

        // unselect old field first
        if (this.selected != null) {
            sudokuField = sudokuGrid.blocks[this.selected.block].field[this.selected.row][this.selected.column];
            sudokuField.select(false);
            selectPartner(sudokuField, false);
        }
        // select new one now
        this.selected = selectedPosition;

        sudokuField = sudokuGrid.blocks[selectedPosition.block].field[selectedPosition.row][selectedPosition.column];
        selectPartner(sudokuField, true);
        sudokuField.select(true);
    }

    public void selectPartner(SudokuField sudokuField, boolean select) {
        if (data.loadBoolean(Data.SETTINGS_MARK_NUMBERS) && sudokuField.number.getNumber() != 0)
            for (int i = 0; i < 9; i++) {
                Number[][] numbers = game.getSudoku()[i].getNumbers();
                for (int a = 0; a < 3; a++)
                    for (int b = 0; b < 3; b++)
                        if (numbers[a][b].getNumber() == sudokuField.number.getNumber())
                            sudokuGrid.blocks[i].field[a][b].lightSelect(select);
            }
        if (data.loadBoolean(Data.SETTINGS_MARK_LINES)) {
            // own block
            for (int i = 0; i < 3; i++) {
                if (i != sudokuField.position.column)
                    sudokuGrid.blocks[sudokuField.position.block].field[sudokuField.position.row][i].lightSelect(select);
                if (i != sudokuField.position.row)
                    sudokuGrid.blocks[sudokuField.position.block].field[i][sudokuField.position.column].lightSelect(select);
            }
            // other blocks
            for (int i = 0; i < 3; i++) {
                sudokuGrid.blocks[partnerBlockLookup[sudokuField.position.block][0]].field[sudokuField.position.row][i].lightSelect(select);
                sudokuGrid.blocks[partnerBlockLookup[sudokuField.position.block][1]].field[sudokuField.position.row][i].lightSelect(select);
                sudokuGrid.blocks[partnerBlockLookup[sudokuField.position.block][2]].field[i][sudokuField.position.column].lightSelect(select);
                sudokuGrid.blocks[partnerBlockLookup[sudokuField.position.block][3]].field[i][sudokuField.position.column].lightSelect(select);
            }
        }
    }

    private void checkNotes(Position position, int number) {
        if (data.loadBoolean(Data.SETTINGS_CHECK_NOTES) && !isNotes) {
            Block[] gameBlock = game.getSudoku();
            // own block
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    gameBlock[position.block].getNumbers()[i][j].checkNote(number);
            // other blocks
            for (int i = 0; i < 3; i++) {
                gameBlock[partnerBlockLookup[position.block][0]].getNumbers()[position.row][i].checkNote(number);
                gameBlock[partnerBlockLookup[position.block][1]].getNumbers()[position.row][i].checkNote(number);
                gameBlock[partnerBlockLookup[position.block][2]].getNumbers()[i][position.column].checkNote(number);
                gameBlock[partnerBlockLookup[position.block][3]].getNumbers()[i][position.column].checkNote(number);
            }
        }
    }

    public void insert(int number) {
        if (selected != null) {
            selectPartner(sudokuGrid.blocks[selected.block].field[selected.row][selected.column], false);
            checkNotes(selected, number);
            game.insert(number, selected, isNotes);
            // TODO two times this.selected, maybe this can be done nicer
            select(selected);

            // check if sudoku-game is finished
            checkSudoku();
        }
    }

    public void delete() {
        if (selected != null) {
            selectPartner(sudokuGrid.blocks[selected.block].field[selected.row][selected.column], false);
            game.delete(selected);
            // TODO two times this.selected, maybe this can be done nicer
            select(selected);

            // check new current errors
            checkErrors();
        }
    }

    public boolean notesMode() {
        isNotes = !isNotes;
        return isNotes;
    }

    public void checkSudoku() {
        if (game.currentErrors() == 0 && game.freeFields() == 0)
            finishSudoku();
        else if (data.loadBoolean(GAME_SHOW_ERRORS)) {
            this.checkErrors();
            if (game.overallErrors >= MAX_ERROR)
                abortSudoku();
        }
    }

    public void checkErrors() {
        this.statusBar.setError(game.overallErrors);
    }

    private void finishSudoku() {
        // TODO are these the right savings?

        timer.stopTimer();
        data.setLoadmode(false);

        int t = data.loadBoolean(Data.GAME_SHOW_TIME) ? timer.getTime() : 0;

        endCardDialog.show(true, t, DIFFICULTY);

        data.addTime(timer.getTime(), DIFFICULTY);
    }

    private void abortSudoku() {
        // TODO are these the right savings?

        data.setLoadmode(false);
        timer.stopTimer();

        endCardDialog.show(false, 0, DIFFICULTY);
    }

    public boolean pauseGame() {
        if (!pause) {
            sudokuGrid.setVisibility(View.INVISIBLE);
            timer.stopTimer();
        } else {
            sudokuGrid.setVisibility(View.VISIBLE);
            timer.startTimer();
        }
        keyboard.pauseMode(!pause);

        pause = !pause;
        return pause;
    }

    public String share() {
        try {
            return ShareClass.share(game, this);
        } catch (Exception e) {
            new CustomToast(this, getResources().getString(R.string.error_default)).show();
        }
        return "";
    }

    public static int getPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1)
            recreate();
    }

    @Override
    public void timeUpdate(int time) {
        runOnUiThread(() -> this.statusBar.setTime(time));
    }

    // JNI
    static {
        System.loadLibrary("generator-jni");
    }

    public native Sudoku createSudokuNative(int freeFields);
}