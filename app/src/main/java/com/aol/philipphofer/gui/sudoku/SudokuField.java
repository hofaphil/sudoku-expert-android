package com.aol.philipphofer.gui.sudoku;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.persistence.Data;
import com.aol.philipphofer.sudoku.Number;

import java.util.Observable;
import java.util.Observer;

public class SudokuField extends GridLayout implements View.OnClickListener, Observer {

    public Position position;
    private Number number;

    private final TextView numberView;
    private GridLayout notesLayout = null;
    private TextView[] notes;

    private final MainActivity mainActivity;

    public SudokuField(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mainActivity = (MainActivity) context;

        LayoutInflater.from(context).inflate(R.layout.sudoku_field, this);

        numberView = findViewById(R.id.numberView);
        numberView.setOnClickListener(this);
    }

    /* public void init(Number number, Position position) {
        this.position = position;

        isNotes = false;
        // TODO: kann die nächste Zeile weg?
        setNumber(0);
        switchLayout(isNotes);
        error = false;

        setNumber(number);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unselected));
        if (number > 0) {
            setChangeable(false);
            setNumberViewText("" + number);
        } else {
            setNumberViewText("");
            setChangeable(true);
        }
    } */

    public void init(Number number, Position position) {
        this.position = position;
        this.number = number;
        this.number.addObserver(this);

        this.setNumberViewText(this.number.getNumber());
    }

    /* public void load(Position position) {
        this.position = position;
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unselected));
        Data data = Data.instance(mainActivity);
        data.loadField(this);
        if (getError())
            if (Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS))
                this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.error));
    }

    public void save() {
        Data data = Data.instance(mainActivity);
        data.saveField(this);
    }


    /* public void switchLayout(boolean isNotes) {
        if (isNotes) {
            mainActivity.unselectPartner(this);
            this.numberView.setVisibility(INVISIBLE);
            getNotesLayout().setVisibility(VISIBLE);
        } else {
            this.numberView.setVisibility(VISIBLE);
            if (notesLayout != null)
                getNotesLayout().setVisibility(INVISIBLE);
        }
    } */

    public void error(boolean error) {
        // TODO mainActivity.addError();
        if (error && Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS)) {
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.error));
        } else {
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected));
        }
    }

    public void select(boolean select, boolean error) {
        if (!error || !Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS)) {
            if (select)
                this.setBackgroundColor(MainActivity.getPrimaryColor(getContext()));
            else
                this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unselected));
        }
    }

    public void lightSelect(boolean select, boolean error) {
        if (!error || !Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS)) {
            if (select)
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lineSelected));
            else
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lineUnselected));
        }
    }

    public void checkNotes(int number) {
        if (number != 0 && notesLayout != null)
            notes[number - 1].setVisibility(INVISIBLE);
    }

    public void setNumberViewText(int number) {
        if (number != 0)
            this.numberView.setText(String.valueOf(number));
        else
            this.numberView.setText("");
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

    private GridLayout getNotesLayout() {
        if (notesLayout == null) {
            notesLayout = (GridLayout) ((ViewStub) findViewById(R.id.notesGridStub)).inflate();
            notesLayout.setOnClickListener(this);
            notes = new TextView[9];
            for (int i = 0; i < 9; i++)
                notes[i] = findViewById(getContext().getResources().getIdentifier("tv" + i, "id", getContext().getPackageName()));
        }
        return notesLayout;
    }

    @Override
    public void update(Observable observable, Object o) {
        Number n = (Number) observable;
        this.setNumberViewText(n.getNumber());
        this.error(n.isError());
        System.out.println(observable);
        System.out.println(observable.hasChanged());
        System.out.println(observable.countObservers());
        System.out.println(o);
    }

    @Override
    public void onClick(View view) {
        mainActivity.select(this.position);
    }
}
