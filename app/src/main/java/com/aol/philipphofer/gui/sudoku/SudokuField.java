package com.aol.philipphofer.gui.sudoku;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.custom.CustomColor;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.persistence.Data;

public class SudokuField extends GridLayout implements View.OnClickListener {

    private int number;

    public Position position;

    private boolean error;

    private TextView numberView;

    private GridLayout notesLayout;
    private TextView[] notes;

    private MainActivity mainActivity;

    private boolean changeable;
    private boolean isNotes;

    public SudokuField(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mainActivity = (MainActivity) context;

        LayoutInflater.from(context).inflate(R.layout.sudoku_field, this);

        numberView = findViewById(R.id.numberView);
        numberView.setOnClickListener(this);

        notesLayout = new GridLayout(context);
        notesLayout = findViewById(R.id.notesGrid);
        notesLayout.setOnClickListener(this);

        notes = new TextView[9];
        for (int i = 0; i < 9; i++)
            notes[i] = findViewById(context.getResources().getIdentifier("tv" + i, "id", context.getPackageName()));

    }

    public void init(int number, Position position) {
        this.position = position;

        isNotes = false;
        setNumber(0);
        switchLayout(isNotes);
        error = false;

        for (int i = 0; i < 9; i++)
            notes[i].setVisibility(View.INVISIBLE);

        setNumber(number);
        setBackgroundColor(getResources().getColor(R.color.unselected));
        if (number > 0) {
            setChangeable(false);
            setNumberViewText("" + number);
        } else {
            setNumberViewText("");
            setChangeable(true);
        }
    }

    public void load(Position position) {
        this.position = position;
        setBackgroundColor(getResources().getColor(R.color.unselected));
        Data data = Data.instance(mainActivity);
        data.loadField(this);
        if (getError())
            if (Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS))
                this.setBackgroundColor(getResources().getColor(R.color.error));
    }

    public void save() {
        Data data = Data.instance(mainActivity);
        data.saveField(this);
    }

    public void insert(int number) {
        if (!isChangeable())
            return;
        if (mainActivity.isNotes() != isNotes) {
            delete();
            isNotes = !isNotes;
            switchLayout(isNotes);
        }
        if (isNotes) {
            if (notes[number - 1].getVisibility() == VISIBLE)
                deleteNote(number);
            else
                addNote(number);
        } else {
            if (this.number == number)
                delete();
            else {
                if (this.number != 0)
                    delete();
                if (!checkNumber(number))
                    error();
                setNumber(number);
                setNumberViewText(number);
            }
        }
    }

    public void delete() {
        if (getError())
            unerror();
        if (isChangeable()) {
            if (isNotes)
                for (int i = 1; i < 10; i++)
                    deleteNote(i);
            else if (number != 0) {
                mainActivity.subNumberCount(number);
                setNumber(0);
                setNumberViewText("");
                mainActivity.setFreeFields(mainActivity.getFreeFields() + 1);
            }
        }
    }

    public boolean checkNumber(int number) {
        return mainActivity.sudoku.getSolution()[position.parent].getNumbers()[position.row][position.column] == number;
    }

    public void switchLayout(boolean isNotes) {
        if (isNotes) {
            mainActivity.unselectPartner(this);
            this.numberView.setVisibility(INVISIBLE);
            this.notesLayout.setVisibility(VISIBLE);
        } else {
            this.numberView.setVisibility(VISIBLE);
            this.notesLayout.setVisibility(INVISIBLE);
        }
    }

    public void error() {
        error = true;
        mainActivity.addError();
        if (Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS))
            this.setBackgroundColor(getResources().getColor(R.color.error));
    }

    private void unerror() {
        error = false;
        this.setBackgroundColor(getResources().getColor(R.color.selected));
    }

    public void select() {
        if (!error || !Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS))
            this.setBackgroundColor(Color.parseColor(Data.instance(getContext()).loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));
    }

    public void unselect() {
        if (!error || !Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS))
            this.setBackgroundColor(getResources().getColor(R.color.unselected));
    }

    public void lightSelect() {
        if (!error || !Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS)) {
            this.setBackgroundColor(getResources().getColor(R.color.lineSelected));
        }
    }

    public void unlightSelect() {
        if (!error || !Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS))
            this.setBackgroundColor(getResources().getColor(R.color.lineUnselected));
    }

    public void checkNotes(int number) {
        if (number != 0)
            notes[number - 1].setVisibility(INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        mainActivity.select(this);
    }

    //get and set methods

    public void setNumber(int number) {
        this.number = number;
        if (number != 0) {
            mainActivity.addNumberCount(number);
            mainActivity.setFreeFields(mainActivity.getFreeFields() - 1);
        }
    }

    public int getNumber() {
        return this.number;
    }

    public void addNote(int number) {
        this.notes[number - 1].setVisibility(VISIBLE);
    }

    public void deleteNote(int number) {
        this.notes[number - 1].setVisibility(INVISIBLE);
    }

    public void setNumberViewText(String text) {
        this.numberView.setText(text);
    }

    public void setNumberViewText(int text) {
        this.numberView.setText("" + text);
    }

    public String getNumberViewText() {
        return this.numberView.getText().toString();
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
        if (!changeable)
            numberView.setTypeface(numberView.getTypeface(), Typeface.BOLD);
        else
            numberView.setTypeface(numberView.getTypeface(), Typeface.ITALIC);
    }

    public boolean isChangeable() {
        return this.changeable;
    }

    public boolean isNotes() {
        return this.isNotes;
    }

    public void setIsNotes(boolean isNotes) {
        this.isNotes = isNotes;
    }

    public boolean getError() {
        return this.error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean[] getNotes() {
        boolean[] returnBool = new boolean[9];
        for (int i = 0; i < 9; i++)
            if (notes[i].getVisibility() == VISIBLE)
                returnBool[i] = true;
        return returnBool;
    }

    public void setNotes(boolean[] bool) {
        for (int i = 0; i < 9; i++)
            if (bool[i])
                notes[i].setVisibility(VISIBLE);
    }
}
