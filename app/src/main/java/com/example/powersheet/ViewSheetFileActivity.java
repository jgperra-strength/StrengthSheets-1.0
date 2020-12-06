package com.example.powersheet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.powersheet.Spreadsheet.SheetFile;
import com.example.powersheet.Spreadsheet.TrainingSheet;

import java.util.ArrayList;

public class ViewSheetFileActivity extends AppCompatActivity {
    public static SheetFile sheetFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sheet_file);

        Intent i = getIntent();
        String sheetName = i.getExtras().getString("sheetFileTitle");

        sheetFile = new SheetFile(sheetName);
        sheetFile.setTrainingSheet(new TrainingSheet());
        sheetFile.deserializeSheetFile(sheetName);

        TextView sheetFileTitle = (TextView) this.findViewById(R.id.sheetFileTitle);
        sheetFileTitle.setText(sheetFile.getSheetFileTitle());

        ListView sheetsListView = (ListView) this.findViewById(R.id.sheetsListView);

        //list sheets within sheetfile in list view
        ArrayList<String> sheets = new ArrayList<>();
        sheets.add("Training");
        sheets.add("Nutrition");
        final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sheets);
        sheetsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        sheetsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) adapter.getItem(i);
                switch (item) {
                    case "Training":
                        Intent intent = new Intent(ViewSheetFileActivity.this, ViewTrainingSheetActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }
}