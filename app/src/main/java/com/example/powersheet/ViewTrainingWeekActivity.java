package com.example.powersheet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.powersheet.CustomAdapters.DayRecyclerAdapter;
import com.example.powersheet.CustomAdapters.Helper.OnStartDragListener;
import com.example.powersheet.CustomAdapters.Helper.SimpleItemTouchHelperCallback;
import com.example.powersheet.Training.Day;
import com.example.powersheet.Training.Week;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ViewTrainingWeekActivity extends AppCompatActivity {
    private Week week;
    private ArrayList<Day> days;
    private RecyclerView recyclerView;
    private DayRecyclerAdapter dayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_training_week);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);

        Intent i = getIntent();
        int blockIdx = Integer.parseInt(i.getExtras().getString("blockIdx"));
        int weekIdx = Integer.parseInt(i.getExtras().getString("weekIdx"));

        TextView weekTitle = (TextView) this.findViewById(R.id.weekTitle);
        week = ViewSheetFileActivity.sheetFile.getTrainingSheet().getBlocks().get(blockIdx).getWeeks().get(weekIdx);
        weekTitle.setText(week.getTitle());

        // set up the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        OnStartDragListener dragListener = new OnStartDragListener();
        days = week.getDays();
        dayAdapter = new DayRecyclerAdapter(this, dragListener, days);

        recyclerView.setAdapter(dayAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(dayAdapter, this);
        dragListener.mItemTouchHelper = new ItemTouchHelper(callback);
        dragListener.mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void addDay(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View popup = inflater.inflate(R.layout.popup_day_add, null);

        final TextView action = (TextView) popup.findViewById(R.id.actionTextView);
        action.setText("Add Day");

        final EditText dayTitle = (EditText) popup.findViewById(R.id.editTitleView);
        dayTitle.setText("Day " + (days.size() + 1));

        final EditText dayDate = (EditText) popup.findViewById(R.id.editDateView);
        final EditText dayTime = (EditText) popup.findViewById(R.id.editTimeView);

        final Calendar myCalendar = Calendar.getInstance();
        String myFormatDate = "MM/dd/yy"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormatDate, Locale.US);

        String myFormatTime = "hh:mm aa";
        final SimpleDateFormat stf = new SimpleDateFormat(myFormatTime, Locale.US);

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
                new DatePickerDialog(ViewTrainingWeekActivity.this, date, myCalendar
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
                mTimePicker = new TimePickerDialog(ViewTrainingWeekActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

        String[] arraySpinner = new String[days.size() + 1];
        for (int i = 0; i < days.size(); ++i) {
            arraySpinner[i] = Integer.toString(i + 1);
        }
        arraySpinner[arraySpinner.length - 1] = Integer.toString(arraySpinner.length);
        final Spinner indexSpinner = (Spinner) popup.findViewById(R.id.indexSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        indexSpinner.setAdapter(adapter);
        indexSpinner.setSelection(arraySpinner.length - 1);

        builder.setView(popup);

        //Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!dayTitle.getText().toString().isEmpty()) {
                    Day d = new Day(dayTitle.getText().toString());
                    try {
                        d.setDate(sdf.parse(dayDate.getText().toString()));
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        d.setTime(stf.parse(dayTime.getText().toString()));
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }

                    d.setIdx(days.size());
                    d.setParentWeek(week);

                    days.add(Integer.parseInt(indexSpinner.getSelectedItem().toString()) - 1, d);
                    if (!days.isEmpty()) recyclerView.setAdapter(dayAdapter);
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
}