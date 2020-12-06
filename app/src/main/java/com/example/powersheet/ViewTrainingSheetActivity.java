package com.example.powersheet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.powersheet.CustomAdapters.BlockAdapter;
import com.example.powersheet.Training.Block;

import java.util.ArrayList;

public class ViewTrainingSheetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_training_sheet);

        ListView blocksListView = (ListView) this.findViewById(R.id.blocksListView);

        /*//list sheets within sheetfile in list view
        ArrayList<String> blocks = new ArrayList<>();
        for (Block b : ViewSheetFileActivity.sheetFile.getTrainingSheet().getBlocks()) {
            blocks.add(b.getBlockTitle());
        }*/

        BlockAdapter adapter = new BlockAdapter(this, ViewSheetFileActivity.sheetFile.getTrainingSheet().getBlocks(), R.layout.genric_entry_view);
        blocksListView.setAdapter(adapter);

        blocksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ViewTrainingSheetActivity.this, ViewTrainingBlockActivity.class);
                intent.putExtra("blockIdx", Integer.toString(i));
                startActivity(intent);
            }
        });
    }
}