package com.example.powersheet.CustomAdapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.powersheet.CustomAdapters.Helper.ItemTouchHelperAdapter;
import com.example.powersheet.CustomAdapters.Helper.ItemTouchHelperViewHolder;
import com.example.powersheet.CustomAdapters.Helper.OnStartDragListener;
import com.example.powersheet.R;
import com.example.powersheet.Training.Week;
import com.example.powersheet.ViewTrainingWeekActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class WeekRecyclerAdapter extends RecyclerView.Adapter<WeekRecyclerAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Week> weeks;

    private RecyclerView rv;

    private final OnStartDragListener mDragStartListener;
    public Context context;

    public WeekRecyclerAdapter(Context c, OnStartDragListener dragStartListener, ArrayList<Week> w) {
        context = c;
        mDragStartListener = dragStartListener;
        weeks = w;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_entry_view, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        rv = (RecyclerView) parent;
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.titleView.setText(weeks.get(position).getTitle());

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        holder.weekBeginDate.setText(weeks.get(position).getBeginDate() == null ? "" : sdf.format(weeks.get(position).getBeginDate()));
        holder.weekEndDate.setText(weeks.get(position).getEndDate() == null ? "" : sdf.format(weeks.get(position).getEndDate()));

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        Week deletedItem = weeks.get(position);
        weeks.remove(position);
        notifyItemRemoved(position);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        weeks.add(position, deletedItem);
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
        Collections.swap(weeks, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return weeks.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView titleView;
        public TextView weekBeginDate;
        public TextView weekEndDate;
        public final ImageView handleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.titleTextView);
            weekBeginDate = (TextView) itemView.findViewById(R.id.valueTextView);
            weekEndDate = (TextView) itemView.findViewById(R.id.valueTextView2);
            handleView = (ImageView) itemView.findViewById(R.id.handle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    weeks.get(ItemViewHolder.this.getAdapterPosition()).setIdx(ItemViewHolder.this.getAdapterPosition());
                    Intent intent = new Intent(context, ViewTrainingWeekActivity.class);
                    intent.putExtra("blockIdx", Integer.toString(weeks.get(ItemViewHolder.this.getAdapterPosition()).getParentBlock().getIdx()));
                    intent.putExtra("weekIdx", Integer.toString(ItemViewHolder.this.getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    editWeek(ItemViewHolder.this.getAdapterPosition());
                    return true;
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

    private void editWeek(final int idx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View popup = inflater.inflate(R.layout.popup_week_add, null);

        final TextView action = (TextView) popup.findViewById(R.id.actionTextView);
        action.setText("Edit Week");

        final EditText dayTitle = (EditText) popup.findViewById(R.id.editTitleView);
        dayTitle.setText(weeks.get(idx).getTitle());

        final EditText weekBeginDate = (EditText) popup.findViewById(R.id.editDateView);
        final EditText weekEndDate = (EditText) popup.findViewById(R.id.editDateEndView);

        String myFormat = "MM/dd/yy"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        weekBeginDate.setText(weeks.get(idx).getBeginDate() == null ? "" : sdf.format(weeks.get(idx).getBeginDate()));
        weekEndDate.setText(weeks.get(idx).getEndDate() == null ? "" : sdf.format(weeks.get(idx).getEndDate()));

        final Calendar myBeginCalendar = Calendar.getInstance();
        final Calendar myEndCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener beginDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myBeginCalendar.set(Calendar.YEAR, year);
                myBeginCalendar.set(Calendar.MONTH, monthOfYear);
                myBeginCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                myEndCalendar.set(Calendar.YEAR, year);
                myEndCalendar.set(Calendar.MONTH, monthOfYear);
                myEndCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                myEndCalendar.add(Calendar.DAY_OF_YEAR, 7);

                weekBeginDate.setText(sdf.format(myBeginCalendar.getTime()));
                weekEndDate.setText(sdf.format(myEndCalendar.getTime()));
            }
        };

        weekBeginDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, beginDate, myBeginCalendar
                        .get(Calendar.YEAR), myBeginCalendar.get(Calendar.MONTH),
                        myBeginCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myBeginCalendar.set(Calendar.YEAR, year);
                myBeginCalendar.set(Calendar.MONTH, monthOfYear);
                myBeginCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                weekEndDate.setText(sdf.format(myBeginCalendar.getTime()));
            }
        };

        weekEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, endDate, myBeginCalendar
                        .get(Calendar.YEAR), myBeginCalendar.get(Calendar.MONTH),
                        myBeginCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ((Button) popup.findViewById(R.id.clearDateButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weekBeginDate.setText("");
            }
        });

        ((Button) popup.findViewById(R.id.clearDateEndButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weekEndDate.setText("");
            }
        });

        builder.setView(popup);

        //Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!dayTitle.getText().toString().isEmpty()) {

                    weeks.get(idx).setTitle(dayTitle.getText().toString());
                    try {
                        if (weekBeginDate.getText().toString().isEmpty()) weeks.get(idx).setBeginDate(null);
                        else weeks.get(idx).setBeginDate(sdf.parse(weekBeginDate.getText().toString()));
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        if (weekEndDate.getText().toString().isEmpty()) weeks.get(idx).setEndDate(null);
                        else weeks.get(idx).setEndDate(sdf.parse(weekEndDate.getText().toString()));
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rv.setAdapter(WeekRecyclerAdapter.this);
                    }
                });
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
}