package com.aol.philipphofer.logic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.Keyboard;
import com.aol.philipphofer.gui.StatusBar;
import com.aol.philipphofer.gui.custom.CustomActivity;
import com.aol.philipphofer.gui.custom.CustomAdLoader;
import com.aol.philipphofer.gui.custom.CustomToast;
import com.aol.philipphofer.gui.dialog.EndCardDialog;
import com.aol.philipphofer.gui.sudoku.SudokuField;
import com.aol.philipphofer.gui.sudoku.SudokuGrid;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.persistence.Data;
import com.aol.philipphofer.logic.sudoku.Block;
import com.aol.philipphofer.logic.sudoku.Number;
import com.aol.philipphofer.logic.sudoku.Sudoku;
import com.google.android.gms.ads.AdView;

public class MainActivity extends CustomActivity {

    public Sudoku game; // the current game

    public StatusBar statusBar;
    public SudokuGrid sudokuGrid;
    private Keyboard keyboard;
    private EndCardDialog endCardDialog;

    private Position selected;
    private boolean isNotes;
    public static boolean pause;

    private Timer timer;

    public static int MAX_ERROR = 3;
    public static Difficulty DIFFICULTY = Difficulty.ADVANCED;
    public static boolean LOAD_MODE = false;
    // TODO load a shared game
    public static boolean SHARED = false;

    private Thread t = new Thread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sudokuGrid = findViewById(R.id.sudokuGrid);
        statusBar = findViewById(R.id.statusBar);
        keyboard = findViewById(R.id.keyboard);
        endCardDialog = new EndCardDialog(this);

        timer = new Timer(this);
        timer.start();

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
        AdView mAdView = findViewById(R.id.adView);

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
            ((ViewManager) mAdView.getParent()).removeView(mAdView);
        } else {
            RelativeLayout.LayoutParams paramsKeyboard = (RelativeLayout.LayoutParams) keyboard.getLayoutParams();
            paramsKeyboard.bottomMargin = (int) (Math.abs(width - height) / 1.5);
            keyboard.setLayoutParams(paramsKeyboard);
            if (paramsKeyboard.bottomMargin >= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics())) {
                CustomAdLoader.loadAd(this, mAdView);
            } else {
                ((ViewManager) mAdView.getParent()).removeView(mAdView);
            }
        }
        sudokuGrid.setLayoutParams(params);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart");

        // Load a game from data
        if (LOAD_MODE = data.getLoadmode()) {
            game = data.loadSudoku();
            sudokuGrid.init(game.getSudoku());

            statusBar.activate();
            keyboard.activate();

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

            keyboard.deactivate();
            statusBar.deactivate();

            if (!t.isAlive()) {
                t = new Thread(this::heavyLoading);
                t.start();
            }
        } else {
            getWindow().getDecorView().post(() -> timer.startTimer(data.loadInt(Data.GAME_TIME)));
        }

        DIFFICULTY = Difficulty.getDifficulty(data.loadInt(Data.GAME_DIFFICULTY));
        statusBar.setDifficulty(DIFFICULTY);
        statusBar.setError(game.overallErrors);
    }

    public void heavyLoading() {
        timer.stopTimer();
        game = createSudokuNative(7 * DIFFICULTY.getNumber() + 42);

        LOAD_MODE = !LOAD_MODE;
        data.setLoadmode(LOAD_MODE);
        data.saveSudoku(game);

        // save the setting that apply at first next game
        data.saveBoolean(Data.GAME_SHOW_ERRORS, data.loadBoolean(Data.SETTINGS_MARK_ERRORS));
        data.saveBoolean(Data.GAME_SHOW_TIME, data.loadBoolean(Data.SETTINGS_SHOW_TIME));

        // reset errors and time
        data.saveInt(Data.GAME_ERRORS, 0);
        data.saveInt(Data.GAME_TIME, 0);

        runOnUiThread(() -> {
            sudokuGrid.init(game.getSudoku());

            sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.VISIBLE);

            statusBar.activate();
            keyboard.activate();

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
            for (int i = 0; i < 3; i++) {
                if (i != sudokuField.position.column)
                    sudokuGrid.blocks[sudokuField.position.block].field[sudokuField.position.row][i].lightSelect(select);
                if (i != sudokuField.position.row)
                    sudokuGrid.blocks[sudokuField.position.block].field[i][sudokuField.position.column].lightSelect(select);
            }
            switch (sudokuField.position.block) {
                case 0:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[1].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[2].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[3].field[i][sudokuField.position.column].lightSelect(select);
                        sudokuGrid.blocks[6].field[i][sudokuField.position.column].lightSelect(select);
                    }
                    break;
                case 1:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[0].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[2].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[4].field[i][sudokuField.position.column].lightSelect(select);
                        sudokuGrid.blocks[7].field[i][sudokuField.position.column].lightSelect(select);
                    }
                    break;
                case 2:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[0].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[1].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[5].field[i][sudokuField.position.column].lightSelect(select);
                        sudokuGrid.blocks[8].field[i][sudokuField.position.column].lightSelect(select);
                    }
                    break;
                case 3:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[4].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[5].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[0].field[i][sudokuField.position.column].lightSelect(select);
                        sudokuGrid.blocks[6].field[i][sudokuField.position.column].lightSelect(select);
                    }
                    break;
                case 4:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[3].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[5].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[1].field[i][sudokuField.position.column].lightSelect(select);
                        sudokuGrid.blocks[7].field[i][sudokuField.position.column].lightSelect(select);
                    }
                    break;
                case 5:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[3].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[4].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[2].field[i][sudokuField.position.column].lightSelect(select);
                        sudokuGrid.blocks[8].field[i][sudokuField.position.column].lightSelect(select);
                    }
                    break;
                case 6:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[7].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[8].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[0].field[i][sudokuField.position.column].lightSelect(select);
                        sudokuGrid.blocks[3].field[i][sudokuField.position.column].lightSelect(select);
                    }
                    break;
                case 7:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[6].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[8].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[1].field[i][sudokuField.position.column].lightSelect(select);
                        sudokuGrid.blocks[4].field[i][sudokuField.position.column].lightSelect(select);
                    }
                    break;
                case 8:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[6].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[7].field[sudokuField.position.row][i].lightSelect(select);
                        sudokuGrid.blocks[2].field[i][sudokuField.position.column].lightSelect(select);
                        sudokuGrid.blocks[5].field[i][sudokuField.position.column].lightSelect(select);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void checkNotes(Position position, int number) {
        if (data.loadBoolean(Data.SETTINGS_CHECK_NOTES) && !isNotes) {
            Block[] gameBlock = game.getSudoku();
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    gameBlock[position.block].getNumbers()[i][j].checkNote(number);

            switch (position.block) {
                case 0:
                    for (int i = 0; i < 3; i++) {
                        gameBlock[1].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[2].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[3].getNumbers()[i][position.column].checkNote(number);
                        gameBlock[6].getNumbers()[i][position.column].checkNote(number);
                    }
                    break;
                case 1:
                    for (int i = 0; i < 3; i++) {
                        gameBlock[0].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[2].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[4].getNumbers()[i][position.column].checkNote(number);
                        gameBlock[7].getNumbers()[i][position.column].checkNote(number);
                    }
                    break;
                case 2:
                    for (int i = 0; i < 3; i++) {
                        gameBlock[0].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[1].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[5].getNumbers()[i][position.column].checkNote(number);
                        gameBlock[8].getNumbers()[i][position.column].checkNote(number);
                    }
                    break;
                case 3:
                    for (int i = 0; i < 3; i++) {
                        gameBlock[4].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[5].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[0].getNumbers()[i][position.column].checkNote(number);
                        gameBlock[6].getNumbers()[i][position.column].checkNote(number);
                    }
                    break;
                case 4:
                    for (int i = 0; i < 3; i++) {
                        gameBlock[3].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[5].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[1].getNumbers()[i][position.column].checkNote(number);
                        gameBlock[7].getNumbers()[i][position.column].checkNote(number);
                    }
                    break;
                case 5:
                    for (int i = 0; i < 3; i++) {
                        gameBlock[3].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[4].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[2].getNumbers()[i][position.column].checkNote(number);
                        gameBlock[8].getNumbers()[i][position.column].checkNote(number);
                    }
                    break;
                case 6:
                    for (int i = 0; i < 3; i++) {
                        gameBlock[7].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[8].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[0].getNumbers()[i][position.column].checkNote(number);
                        gameBlock[3].getNumbers()[i][position.column].checkNote(number);
                    }
                    break;
                case 7:
                    for (int i = 0; i < 3; i++) {
                        gameBlock[6].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[8].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[1].getNumbers()[i][position.column].checkNote(number);
                        gameBlock[4].getNumbers()[i][position.column].checkNote(number);
                    }
                    break;
                case 8:
                    for (int i = 0; i < 3; i++) {
                        gameBlock[6].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[7].getNumbers()[position.row][i].checkNote(number);
                        gameBlock[2].getNumbers()[i][position.column].checkNote(number);
                        gameBlock[5].getNumbers()[i][position.column].checkNote(number);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void insert(int number) {
        if (selected != null) {
            selectPartner(sudokuGrid.blocks[selected.block].field[selected.row][selected.column], false);
            checkNotes(selected, number);
            game.insert(number, selected, isNotes);
            // TODO two times this.selected, maybe this can be done nicer
            data.saveGameNumber(game.getNumber(selected), selected);
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
            data.saveGameNumber(game.getNumber(selected), selected);
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
        else {
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
            keyboard.activatePauseMode();
        } else {
            sudokuGrid.setVisibility(View.VISIBLE);
            keyboard.deactivatePauseMode();
        }
        pause = !pause;
        return pause;
    }

    public void share() {
        try {
            // TODO: share only non-changeable fields
            ShareClass.share(game, this);
        } catch (Exception e) {
            new CustomToast(this, getResources().getString(R.string.error_default)).show();
        }
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

    // JNI
    static {
        System.loadLibrary("generator-jni");
    }

    public native Sudoku createSudokuNative(int freeFields);

}