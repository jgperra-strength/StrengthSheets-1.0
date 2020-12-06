package com.example.powersheet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.powersheet.CustomAdapters.Helper.OnStartDragListener;
import com.example.powersheet.CustomAdapters.Helper.SimpleItemTouchHelperCallback;
import com.example.powersheet.CustomAdapters.WeekRecyclerAdapter;
import com.example.powersheet.Training.Block;
import com.example.powersheet.Training.Week;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ViewTrainingBlockActivity extends AppCompatActivity {
    private Block block;
    private ArrayList<Week> weeks;
    private RecyclerView recyclerView;
    private WeekRecyclerAdapter weekAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_training_block);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);

        Intent i = getIntent();
        int blockIdx = Integer.parseInt(i.getExtras().getString("blockIdx"));

        TextView blockTitle = (TextView) this.findViewById(R.id.blockTitle);
        block = ViewSheetFileActivity.sheetFile.getTrainingSheet().getBlocks().get(blockIdx);
        blockTitle.setText(block.getBlockTitle());

        // set up the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        OnStartDragListener dragListener = new OnStartDragListener();
        weeks = block.getWeeks();
        weekAdapter = new WeekRecyclerAdapter(this, dragListener, weeks);

        recyclerView.setAdapter(weekAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(weekAdapter, this);
        dragListener.mItemTouchHelper = new ItemTouchHelper(callback);
        dragListener.mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void addWeek(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View popup = inflater.inflate(R.layout.popup_week_add, null);

        final TextView action = (TextView) popup.findViewById(R.id.actionTextView);
        action.setText("Add Week");

        final EditText weekTitle = (EditText) popup.findViewById(R.id.editTitleView);
        weekTitle.setText("Week " + (weeks.size() + 1));

        final EditText weekBeginDate = (EditText) popup.findViewById(R.id.editDateView);
        final EditText weekEndDate = (EditText) popup.findViewById(R.id.editDateEndView);

        final Calendar myBeginCalendar = Calendar.getInstance();
        final Calendar myEndCalendar = Calendar.getInstance();
        String myFormat = "MM/dd/yy";
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

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
                new DatePickerDialog(ViewTrainingBlockActivity.this, beginDate, myBeginCalendar
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
                new DatePickerDialog(ViewTrainingBlockActivity.this, endDate, myBeginCalendar
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
                if (!weekTitle.getText().toString().isEmpty()) {
                    Week w = new Week(weekTitle.getText().toString());
                    try {
                        w.setBeginDate(sdf.parse(weekBeginDate.getText().toString()));
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        w.setEndDate(sdf.parse(weekEndDate.getText().toString()));
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }

                    w.setIdx(weeks.size());
                    w.setParentBlock(block);

                    //days.add(d);
                    weeks.add(w);
                    if (!weeks.isEmpty()) recyclerView.setAdapter(weekAdapter);
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