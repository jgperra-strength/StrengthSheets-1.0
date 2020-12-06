package com.example.powersheet.CustomAdapters;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.powersheet.R;
import com.example.powersheet.Training.Exercise;
import com.example.powersheet.Training.ExerciseAttribute;

import java.util.ArrayList;

public class ExerciseAdapter extends ArrayAdapter {
    ArrayList<Exercise> exercises;
    Context context;
    public ExerciseAdapter(Context c, ArrayList<Exercise> e, int position) {
        super(c, position);
        context = c;
        exercises = e;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return exercises.size();
    }

    @Override
    public Object getItem(int i) {
        return exercises.get(i);
    }

    @Override
    public long getItemId(int i) {
        return exercises.size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, final ViewGroup viewGroup) {
        final Exercise exercise = exercises.get(i);
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.exercise_entry_view, null);
            final View viewFinal = view;

            TextView exerciseName = (TextView) view.findViewById(R.id.exerciseFillTextView);
            exerciseName.setText(exercise.getTitle());

            HorizontalScrollView attributesList = (HorizontalScrollView) view.findViewById(R.id.attributeScrollView);
            final LinearLayout layout = (LinearLayout) attributesList.getChildAt(0);
            for (final ExerciseAttribute ea : exercise.getAttributes()) {
                View v = layoutInflater.inflate(R.layout.attribute_view, null);

                TextView title = (TextView) v.findViewById(R.id.attTitleTextView);
                title.setText(ea.getAttributeTitle());
                final EditText value = (EditText) v.findViewById(R.id.attValueTextView);
                value.setText(ea.getValue());

                value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ConstraintLayout) viewFinal.findViewById(R.id.topConstraint)).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                        v.requestFocus();
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                    }
                });

                value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (!value.isFocused()) {
                            ((ConstraintLayout) viewFinal.findViewById(R.id.topConstraint)).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                        }
                        else {
                            ((ConstraintLayout) viewFinal.findViewById(R.id.topConstraint)).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                        }
                    }
                });

                value.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if (i == KeyEvent.KEYCODE_ENTER || i == KeyEvent.KEYCODE_BACK) {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            value.setFocusable(false);
                            value.setFocusableInTouchMode(true);
                            ((ConstraintLayout) viewFinal.findViewById(R.id.topConstraint)).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                value.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        ea.setValue(charSequence.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                if (title.getText().toString() == "NOTES") {
                    value.setVisibility(View.GONE);
                    ((Button) v.findViewById(R.id.showButton)).setVisibility(View.VISIBLE);
                    ((Button) v.findViewById(R.id.showButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View popupNotes = layoutInflater.inflate(R.layout.popup_notes, null);

                            ((TextView) popupNotes.findViewById(R.id.notesTitleTextView)).setText(exercise.getTitle() + " Notes");

                            final EditText notes = popupNotes.findViewById(R.id.notesEditText);
                            notes.setText(exercise.getNotes());

                            builder.setView(popupNotes);

                            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    exercise.setNotes(notes.getText().toString());
                                    if (notes.getText().toString().isEmpty()) {
                                        exercise.getAttributes().remove(exercise.getAttributes().size() - 1);
                                        layout.removeViewAt(layout.getChildCount() - 1);
                                    }
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                        }
                    });
                }

                layout.addView(v);
            }
        }
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return exercises.size();
    }

    @Override
    public boolean isEmpty() {
        return exercises.isEmpty();
    }
}
