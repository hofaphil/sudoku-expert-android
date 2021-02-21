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
import com.aol.philipphofer.sudoku.Sudoku;
import com.google.android.gms.ads.AdView;

public class MainActivity extends CustomActivity {

    private int errors;
    private int freeFields; //loaded indirect by fields
    private int[] numberCount;

    public StatusBar statusBar;
    public SudokuGrid sudokuGrid;
    private Keyboard keyboard;
    public Sudoku sudoku;
    private EndCardDialog endCardDialog;
    private SudokuField selected;
    private boolean notes;
    public static boolean pause;

    private Timer timer;

    public static int MAXERROR = 3;
    public static Difficulty DIFFICULTY = Difficulty.ADVANCED;
    public static boolean LOADMODE = false;
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

        if (endCardDialog.isShowing())
            return;

        if (LOADMODE = data.getLoadmode()) {
            setFreeFields(81);
            DIFFICULTY = Difficulty.getDifficulty(data.loadInt(Data.GAME_DIFFICULTY));
            numberCount = new int[9];
            sudoku = new Sudoku(4);

            data.loadSolution(sudoku);
            data.loadSudoku(sudoku);

            if (SHARED) {
                sudokuGrid.init(sudoku.getSudoku());
                SHARED = false;
            } else
                for (int i = 0; i < 9; i++)
                    for (int a = 0; a < 3; a++)
                        for (int b = 0; b < 3; b++)
                            sudokuGrid.blocks[i].field[a][b].load(new Position(a, b, i));

            setErrors(data.loadInt(Data.GAME_ERRORS));

            statusBar.initDifficultyView();
            statusBar.activate();
            sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        pause = true;
        pauseGame();

        if (endCardDialog.isShowing())
            return;

        // TODO selected sometimes still shown, sometimes not (after showing dialog)
        // TODO when new game menu open and you open app again, not in pause mode but dialog still shown!

        if (!(LOADMODE = data.getLoadmode())) {
            sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.LOADING);

            keyboard.deactivate();
            statusBar.deactivate();

            if (!t.isAlive()) {
                setFreeFields(81);
                DIFFICULTY = Difficulty.getDifficulty(data.loadInt(Data.GAME_DIFFICULTY));
                numberCount = new int[9];
                sudoku = new Sudoku(8);
                setErrors(0);
                t = new Thread(this::heavyLoading);
                t.start();
            }
        } else {
            getWindow().getDecorView().post(() -> timer.startTimer(data.loadInt(Data.GAME_TIME)));
        }
    }

    public void heavyLoading() {
        timer.stopTimer();
        sudoku.create(DIFFICULTY.getNumber());

        LOADMODE = !LOADMODE;
        data.setLoadmode(LOADMODE);
        data.saveSolution(sudoku.getSolution());
        data.saveSudoku(sudoku.getSudoku());
        data.saveBoolean(Data.GAME_SHOW_ERRORS, data.loadBoolean(Data.SETTINGS_MARK_ERRORS));
        data.saveBoolean(Data.GAME_SHOW_TIME, data.loadBoolean(Data.SETTINGS_SHOW_TIME));

        runOnUiThread(() -> {
            sudokuGrid.init(sudoku.getSudoku());
            for (int i = 0; i < 9; i++)
                for (int a = 0; a < 3; a++)
                    for (int b = 0; b < 3; b++)
                        sudokuGrid.blocks[i].field[a][b].save();
            data.saveInt(Data.GAME_ERRORS, 0);
            data.saveInt(Data.GAME_TIME, 0);

            statusBar.initDifficultyView();
            sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.VISIBLE);

            statusBar.activate();
            keyboard.activate();

            getWindow().getDecorView().post(() -> timer.startTimer(0));

            statusBar.initError();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        for (int i = 0; i < 9; i++)
            for (int a = 0; a < 3; a++)
                for (int b = 0; b < 3; b++)
                    sudokuGrid.blocks[i].field[a][b].save();
        data.saveInt(Data.GAME_ERRORS, errors);
        data.saveInt(Data.GAME_DIFFICULTY, DIFFICULTY.getNumber());
        timer.stopTimer();
        data.saveInt(Data.GAME_TIME, timer.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.killTimer();
    }

    public void select(SudokuField sudokuField) {
        if (this.selected != null) {
            this.selected.unselect();
            unselectPartner(this.selected);
        }
        this.selected = sudokuField;
        selectPartner(sudokuField);
        this.selected.select();
    }

    public void unselectPartner(SudokuField sudokuField) {
        if (data.loadBoolean(Data.SETTINGS_MARK_NUMBERS) && sudokuField.getNumber() != 0)
            for (int i = 0; i < 9; i++)
                for (int a = 0; a < 3; a++)
                    for (int b = 0; b < 3; b++)
                        if (sudokuGrid.blocks[i].field[a][b].getNumber() == sudokuField.getNumber())
                            sudokuGrid.blocks[i].field[a][b].unlightSelect();

        if (data.loadBoolean(Data.SETTINGS_MARK_LINES)) {
            for (int i = 0; i < 3; i++) {
                if (i != sudokuField.position.column)
                    sudokuGrid.blocks[sudokuField.position.parent].field[sudokuField.position.row][i].unlightSelect();
                if (i != sudokuField.position.row)
                    sudokuGrid.blocks[sudokuField.position.parent].field[i][sudokuField.position.column].unlightSelect();
            }
            switch (sudokuField.position.parent) {
                case 0:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[1].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[2].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[3].field[i][sudokuField.position.column].unlightSelect();
                        sudokuGrid.blocks[6].field[i][sudokuField.position.column].unlightSelect();
                    }
                    break;
                case 1:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[0].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[2].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[4].field[i][sudokuField.position.column].unlightSelect();
                        sudokuGrid.blocks[7].field[i][sudokuField.position.column].unlightSelect();
                    }
                    break;
                case 2:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[0].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[1].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[5].field[i][sudokuField.position.column].unlightSelect();
                        sudokuGrid.blocks[8].field[i][sudokuField.position.column].unlightSelect();
                    }
                    break;
                case 3:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[4].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[5].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[0].field[i][sudokuField.position.column].unlightSelect();
                        sudokuGrid.blocks[6].field[i][sudokuField.position.column].unlightSelect();
                    }
                    break;
                case 4:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[3].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[5].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[1].field[i][sudokuField.position.column].unlightSelect();
                        sudokuGrid.blocks[7].field[i][sudokuField.position.column].unlightSelect();
                    }
                    break;
                case 5:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[3].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[4].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[2].field[i][sudokuField.position.column].unlightSelect();
                        sudokuGrid.blocks[8].field[i][sudokuField.position.column].unlightSelect();
                    }
                    break;
                case 6:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[7].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[8].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[0].field[i][sudokuField.position.column].unlightSelect();
                        sudokuGrid.blocks[3].field[i][sudokuField.position.column].unlightSelect();
                    }
                    break;
                case 7:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[6].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[8].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[1].field[i][sudokuField.position.column].unlightSelect();
                        sudokuGrid.blocks[4].field[i][sudokuField.position.column].unlightSelect();
                    }
                    break;
                case 8:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[6].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[7].field[sudokuField.position.row][i].unlightSelect();
                        sudokuGrid.blocks[2].field[i][sudokuField.position.column].unlightSelect();
                        sudokuGrid.blocks[5].field[i][sudokuField.position.column].unlightSelect();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void selectPartner(SudokuField sudokuField) {
        if (data.loadBoolean(Data.SETTINGS_MARK_NUMBERS) && sudokuField.getNumber() != 0)
            for (int i = 0; i < 9; i++)
                for (int a = 0; a < 3; a++)
                    for (int b = 0; b < 3; b++)
                        if (sudokuGrid.blocks[i].field[a][b].getNumber() == sudokuField.getNumber())
                            sudokuGrid.blocks[i].field[a][b].lightSelect();
        if (data.loadBoolean(Data.SETTINGS_MARK_LINES)) {
            for (int i = 0; i < 3; i++) {
                if (i != sudokuField.position.column)
                    sudokuGrid.blocks[sudokuField.position.parent].field[sudokuField.position.row][i].lightSelect();
                if (i != sudokuField.position.row)
                    sudokuGrid.blocks[sudokuField.position.parent].field[i][sudokuField.position.column].lightSelect();
            }
            switch (sudokuField.position.parent) {
                case 0:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[1].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[2].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[3].field[i][sudokuField.position.column].lightSelect();
                        sudokuGrid.blocks[6].field[i][sudokuField.position.column].lightSelect();
                    }
                    break;
                case 1:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[0].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[2].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[4].field[i][sudokuField.position.column].lightSelect();
                        sudokuGrid.blocks[7].field[i][sudokuField.position.column].lightSelect();
                    }
                    break;
                case 2:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[0].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[1].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[5].field[i][sudokuField.position.column].lightSelect();
                        sudokuGrid.blocks[8].field[i][sudokuField.position.column].lightSelect();
                    }
                    break;
                case 3:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[4].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[5].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[0].field[i][sudokuField.position.column].lightSelect();
                        sudokuGrid.blocks[6].field[i][sudokuField.position.column].lightSelect();
                    }
                    break;
                case 4:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[3].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[5].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[1].field[i][sudokuField.position.column].lightSelect();
                        sudokuGrid.blocks[7].field[i][sudokuField.position.column].lightSelect();
                    }
                    break;
                case 5:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[3].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[4].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[2].field[i][sudokuField.position.column].lightSelect();
                        sudokuGrid.blocks[8].field[i][sudokuField.position.column].lightSelect();
                    }
                    break;
                case 6:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[7].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[8].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[0].field[i][sudokuField.position.column].lightSelect();
                        sudokuGrid.blocks[3].field[i][sudokuField.position.column].lightSelect();
                    }
                    break;
                case 7:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[6].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[8].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[1].field[i][sudokuField.position.column].lightSelect();
                        sudokuGrid.blocks[4].field[i][sudokuField.position.column].lightSelect();
                    }
                    break;
                case 8:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[6].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[7].field[sudokuField.position.row][i].lightSelect();
                        sudokuGrid.blocks[2].field[i][sudokuField.position.column].lightSelect();
                        sudokuGrid.blocks[5].field[i][sudokuField.position.column].lightSelect();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void checkNotes(SudokuField sudokuField, int number) {
        if (data.loadBoolean(Data.SETTINGS_CHECK_NOTES) && !isNotes()) {
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    sudokuGrid.blocks[sudokuField.position.parent].field[i][j].checkNotes(number);

            switch (sudokuField.position.parent) {
                case 0:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[1].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[2].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[3].field[i][sudokuField.position.column].checkNotes(number);
                        sudokuGrid.blocks[6].field[i][sudokuField.position.column].checkNotes(number);
                    }
                    break;
                case 1:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[0].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[2].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[4].field[i][sudokuField.position.column].checkNotes(number);
                        sudokuGrid.blocks[7].field[i][sudokuField.position.column].checkNotes(number);
                    }
                    break;
                case 2:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[0].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[1].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[5].field[i][sudokuField.position.column].checkNotes(number);
                        sudokuGrid.blocks[8].field[i][sudokuField.position.column].checkNotes(number);
                    }
                    break;
                case 3:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[4].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[5].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[0].field[i][sudokuField.position.column].checkNotes(number);
                        sudokuGrid.blocks[6].field[i][sudokuField.position.column].checkNotes(number);
                    }
                    break;
                case 4:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[3].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[5].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[1].field[i][sudokuField.position.column].checkNotes(number);
                        sudokuGrid.blocks[7].field[i][sudokuField.position.column].checkNotes(number);
                    }
                    break;
                case 5:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[3].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[4].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[2].field[i][sudokuField.position.column].checkNotes(number);
                        sudokuGrid.blocks[8].field[i][sudokuField.position.column].checkNotes(number);
                    }
                    break;
                case 6:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[7].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[8].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[0].field[i][sudokuField.position.column].checkNotes(number);
                        sudokuGrid.blocks[3].field[i][sudokuField.position.column].checkNotes(number);
                    }
                    break;
                case 7:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[6].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[8].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[1].field[i][sudokuField.position.column].checkNotes(number);
                        sudokuGrid.blocks[4].field[i][sudokuField.position.column].checkNotes(number);
                    }
                    break;
                case 8:
                    for (int i = 0; i < 3; i++) {
                        sudokuGrid.blocks[6].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[7].field[sudokuField.position.row][i].checkNotes(number);
                        sudokuGrid.blocks[2].field[i][sudokuField.position.column].checkNotes(number);
                        sudokuGrid.blocks[5].field[i][sudokuField.position.column].checkNotes(number);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void insert(int number) {
        if (selected != null) {
            if (selected.getNumber() != 0)
                unselectPartner(selected);
            selected.insert(number);
            selectPartner(selected);
            checkNotes(selected, number);
            this.selected.select();
        }
    }

    public void delete() {
        if (selected != null) {
            unselectPartner(selected);
            selected.delete();
            selectPartner(selected);
            this.selected.select();
        }
    }

    public boolean notesMode() {
        notes = !notes;
        return notes;
    }

    public boolean isNotes() {
        return this.notes;
    }

    public void addError() {
        this.errors++;
        if (errors >= MAXERROR && data.loadBoolean(Data.GAME_SHOW_ERRORS))
            abortSudoku();
        else {
            statusBar.setError();
        }
    }

    public int getErrors() {
        return this.errors;
    }

    private void setErrors(int errors) {
        this.errors = errors;
        statusBar.setError();
    }

    public void finishSudoku() {
        for (int i = 0; i < 9; i++)
            for (int x = 0; x < 3; x++)
                for (int y = 0; y < 3; y++)
                    if (sudokuGrid.blocks[i].field[x][y].getError())
                        return;


        timer.stopTimer();
        data.setLoadmode(false);

        int t = data.loadBoolean(Data.GAME_SHOW_TIME) ? timer.getTime() : 0;

        endCardDialog.show(true, t, DIFFICULTY);

        data.addTime(timer.getTime(), DIFFICULTY);
    }

    public void abortSudoku() {
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

    public void subNumberCount(int number) {
        if (number != 0) {
            if (--numberCount[number - 1] < 9)
                keyboard.activateNumber(number);
        }
    }

    public void addNumberCount(int number) {
        if (number != 0) {
            if (++numberCount[number - 1] == 9)
                keyboard.deactivateNumber(number);
            else
                keyboard.activateNumber(number);
        }
    }

    public void setFreeFields(int number) {
        this.freeFields = number;
        if (freeFields == 0)
            finishSudoku();
    }

    public int getFreeFields() {
        return this.freeFields;
    }

    public void share() {
        try {
            ShareClass.share(sudoku, this);
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
        if (requestCode == 0)
            if (resultCode == 1)
                recreate();
    }
}