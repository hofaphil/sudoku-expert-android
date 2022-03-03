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
import com.aol.philipphofer.logic.sudoku.Number;

import java.util.Observable;
import java.util.Observer;

public class SudokuField extends GridLayout implements View.OnClickListener, Observer {

    public Position position;
    public Number number;

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

        if (!number.isChangeable())
            numberView.setTypeface(numberView.getTypeface(), Typeface.BOLD);
        else
            numberView.setTypeface(numberView.getTypeface(), Typeface.ITALIC);

        this.update(this.number, null);
    }

    /* public void load(Position position) {
        this.position = position;
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unselected));
        Data data = Data.instance(mainActivity);
        data.loadField(this);
        if (getError())
            if (Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS))
                this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.error));
    } */

    public void switchLayout(boolean isNotes) {
        if (isNotes) {
            this.numberView.setVisibility(INVISIBLE);
            getNotesLayout().setVisibility(VISIBLE);
        } else {
            this.numberView.setVisibility(VISIBLE);
            if (notesLayout != null)
                getNotesLayout().setVisibility(INVISIBLE);
        }
    }

    public void error(boolean error) {
        // TODO mainActivity.addError();
        if (error && Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS))
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.error));
        else
            this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unselected));
    }

    public void select(boolean select) {
        if (!this.number.isError() || !Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS)) {
            if (select)
                this.setBackgroundColor(MainActivity.getPrimaryColor(getContext()));
            else
                this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unselected));
        }
    }

    public void lightSelect(boolean select) {
        if (!this.number.isError() || !Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS)) {
            if (select)
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightSelected));
            else
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unselected));
        }
    }

    public void setNumberViewText(int number) {
        if (number != 0)
            this.numberView.setText(String.valueOf(number));
        else
            this.numberView.setText("");
    }

    public void setNotes(boolean[] bool) {
        for (int i = 0; i < 9; i++)
            if (bool[i])
                notes[i].setVisibility(VISIBLE);
            else
                notes[i].setVisibility(INVISIBLE);
    }

    @Override
    public void update(Observable observable, Object o) {
        Number n = (Number) observable;
        this.error(n.isError());

        // handle number
        this.setNumberViewText(n.getNumber());

        // handle notes
        this.switchLayout(n.isNotes());
        if (this.notesLayout != null)
            this.setNotes(n.getNotes());
    }

    @Override
    public void onClick(View view) {
        mainActivity.select(this.position);
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
}
