package com.example.powersheet.CustomAdapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.powersheet.CustomAdapters.Helper.ItemTouchHelperAdapter;
import com.example.powersheet.CustomAdapters.Helper.ItemTouchHelperViewHolder;
import com.example.powersheet.CustomAdapters.Helper.OnStartDragListener;
import com.example.powersheet.R;
import com.example.powersheet.Training.Day;
import com.example.powersheet.ViewTrainingDayActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DayRecyclerAdapter extends RecyclerView.Adapter<DayRecyclerAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Day> days;

    private RecyclerView rv;

    private final OnStartDragListener mDragStartListener;
    public Context context;

    public DayRecyclerAdapter(Context c, OnStartDragListener dragStartListener, ArrayList<Day> w) {
        context = c;
        mDragStartListener = dragStartListener;
        days = w;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_entry_view, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        rv = (RecyclerView) parent;
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.titleView.setText(days.get(position).getDayTitle());

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        holder.dayDate.setText(days.get(position).getDate() == null ? "" : sdf.format(days.get(position).getDate()));

        String myFormatTime = "hh:mm aa"; //In which you need put here
        SimpleDateFormat stf = new SimpleDateFormat(myFormatTime, Locale.US);
        holder.dayTime.setText(days.get(position).getTime() == null ? "" : stf.format(days.get(position).getTime()));

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
        Day deletedItem = days.get(position);
        days.remove(position);
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
                        days.add(position, deletedItem);
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
        Collections.swap(days, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView titleView;
        public TextView dayDate;
        public TextView dayTime;
        public final ImageView handleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.titleTextView);
            dayDate = (TextView) itemView.findViewById(R.id.valueTextView);
            dayTime = (TextView) itemView.findViewById(R.id.valueTextView2);
            handleView = (ImageView) itemView.findViewById(R.id.handle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    days.get(DayRecyclerAdapter.ItemViewHolder.this.getAdapterPosition()).setIdx(DayRecyclerAdapter.ItemViewHolder.this.getAdapterPosition());
                    Intent intent = new Intent(context, ViewTrainingDayActivity.class);
                    intent.putExtra("blockIdx", Integer.toString(days.get(ItemViewHolder.this.getAdapterPosition()).getParentWeek().getParentBlock().getIdx()));
                    intent.putExtra("weekIdx", Integer.toString(days.get(ItemViewHolder.this.getAdapterPosition()).getParentWeek().getIdx()));
                    intent.putExtra("dayIdx", Integer.toString(ItemViewHolder.this.getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    editDay(ItemViewHolder.this.getAdapterPosition());
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

        private void editDay(final int idx) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View popup = inflater.inflate(R.layout.popup_day_add, null);

            final TextView action = (TextView) popup.findViewById(R.id.actionTextView);
            action.setText("Edit Day");

            final EditText dayTitle = (EditText) popup.findViewById(R.id.editTitleView);
            dayTitle.setText(days.get(idx).getDayTitle());

            final EditText dayDate = (EditText) popup.findViewById(R.id.editDateView);
            final EditText dayTime = (EditText) popup.findViewById(R.id.editTimeView);

            String myFormat = "MM/dd/yy"; //In which you need put here
            final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            dayDate.setText(days.get(idx).getDate() == null ? "" : sdf.format(days.get(idx).getDate()));

            String myFormatTime = "hh:mm aa";
            final SimpleDateFormat stf = new SimpleDateFormat(myFormatTime, Locale.US);
            dayTime.setText(days.get(idx).getTime() == null ? "" : stf.format(days.get(idx).getTime()));

            final Calendar myCalendar = Calendar.getInstance();
            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    dayDate.setText(sdf.format(myCalendar.getTime()));
                }
            };

            dayDate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(context, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            dayTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    final Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String am_pm = "";
                            mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                            mcurrentTime.set(Calendar.MINUTE, selectedMinute);
                            if (mcurrentTime.get(Calendar.AM_PM) == Calendar.AM) am_pm = "AM";
                            else if (mcurrentTime.get(Calendar.AM_PM) == Calendar.PM) am_pm = "PM";
                            String strHrsToShow = (mcurrentTime.get(Calendar.HOUR) == 0) ? "12" : mcurrentTime.get(Calendar.HOUR) + "";
                            dayTime.setText(strHrsToShow + ":" + String.format("%02d", mcurrentTime.get(Calendar.MINUTE)) + " " + am_pm);
                        }
                    }, hour, minute, false);
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });

            ((Button) popup.findViewById(R.id.clearDateButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dayDate.setText("");
                }
            });

            ((Button) popup.findViewById(R.id.clearTimeButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dayTime.setText("");
                }
            });

            String[] arraySpinner = new String[days.size()];
            for (int i = 0; i < days.size(); ++i) {
                arraySpinner[i] = Integer.toString(i + 1);
            }

            final Spinner indexSpinner = (Spinner) popup.findViewById(R.id.indexSpinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_item, arraySpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            indexSpinner.setAdapter(adapter);
            indexSpinner.setSelection(idx);

            builder.setView(popup);

            //Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!dayTitle.getText().toString().isEmpty()) {

                        days.get(idx).setDayTitle(dayTitle.getText().toString());
                        try {
                            if (dayDate.getText().toString().isEmpty()) days.get(idx).setDate(null);
                            else days.get(idx).setDate(sdf.parse(dayDate.getText().toString()));
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }

                        try {
                            if (dayTime.getText().toString().isEmpty()) days.get(idx).setTime(null);
                            else days.get(idx).setTime(stf.parse(dayTime.getText().toString()));
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }

                        int spinnerVal = Integer.parseInt(indexSpinner.getSelectedItem().toString()) -1;
                        if (idx != spinnerVal) {
                            Day temp = days.get(idx);
                            days.remove(idx);
                            days.add(spinnerVal, temp);
                        }
                    }

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rv.setAdapter(DayRecyclerAdapter.this);
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
}