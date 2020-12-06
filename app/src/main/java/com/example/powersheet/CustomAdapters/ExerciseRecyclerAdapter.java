package com.example.powersheet.CustomAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.powersheet.CustomAdapters.Helper.ItemTouchHelperAdapter;
import com.example.powersheet.CustomAdapters.Helper.ItemTouchHelperViewHolder;
import com.example.powersheet.CustomAdapters.Helper.OnStartDragListener;
import com.example.powersheet.R;
import com.example.powersheet.Training.Day;
import com.example.powersheet.Training.Exercise;
import com.example.powersheet.Training.ExerciseAttribute;
import com.example.powersheet.ViewTrainingDayActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ExerciseRecyclerAdapter extends RecyclerView.Adapter<ExerciseRecyclerAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Exercise> exercises;

    private RecyclerView rv;

    private final OnStartDragListener mDragStartListener;
    public Context context;

    public ExerciseRecyclerAdapter(Context c, OnStartDragListener dragStartListener, ArrayList<Exercise> w) {
        context = c;
        mDragStartListener = dragStartListener;
        exercises = w;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_entry_view, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        rv = (RecyclerView) parent;
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);

        holder.titleView.setText(exercise.getTitle());
        if (!exercise.getNotes().isEmpty()) {
            holder.notesIndicator.setVisibility(View.VISIBLE);
        }
        else {
            holder.notesIndicator.setVisibility(View.INVISIBLE);
        }

        if (!exercise.getFeedback().isEmpty()) {
            holder.feedbackIndicator.setVisibility(View.VISIBLE);
        }
        else {
            holder.feedbackIndicator.setVisibility(View.INVISIBLE);
        }

        if (!exercise.getMedia().isEmpty()) {
            holder.mediaIndicator.setVisibility(View.VISIBLE);
        }
        else {
            holder.mediaIndicator.setVisibility(View.INVISIBLE);
        }


        final View viewFinal = holder.view;
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        final LinearLayout layout = (LinearLayout) holder.attributesList.getChildAt(0);
        for (final ExerciseAttribute ea : exercise.getAttributes()) {
            View v = layoutInflater.inflate(R.layout.attribute_view, null);

            TextView title = (TextView) v.findViewById(R.id.attTitleTextView);
            title.setText(ea.getAttributeTitle());
            System.out.println("ATTRIBUTE = " + ea.getAttributeTitle());
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
                    } else {
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
            layout.addView(v);
        }

            // Start a drag whenever the handle view it touched
        /*holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });*/

    }

    @Override
    public void onItemDismiss(int position) {
        Exercise deletedItem = exercises.get(position);
        exercises.remove(position);
        notifyItemRemoved(position);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        exercises.add(position, deletedItem);
                        notifyItemInserted(position);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Delete this item?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(exercises, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final View view;
        public final TextView titleView;
        public final TextView notesIndicator;
        public final TextView mediaIndicator;
        public final TextView feedbackIndicator;
        public final HorizontalScrollView attributesList;


        //public final ImageView handleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            titleView = (TextView) itemView.findViewById(R.id.exerciseFillTextView);
            notesIndicator = (TextView) itemView.findViewById(R.id.notesIndicator);
            mediaIndicator = (TextView) itemView.findViewById(R.id.mediaIndicator);
            feedbackIndicator = (TextView) itemView.findViewById(R.id.feedbackIndicator);
            attributesList = (HorizontalScrollView) itemView.findViewById(R.id.attributeScrollView);

            //handleView = (ImageView) itemView.findViewById(R.id.handle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ViewTrainingDayActivity) context).addOrEditExercise(ExerciseRecyclerAdapter.ItemViewHolder.this.getAdapterPosition());
                }
            });
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}