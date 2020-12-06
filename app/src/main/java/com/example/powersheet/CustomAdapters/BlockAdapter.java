package com.example.powersheet.CustomAdapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.powersheet.R;
import com.example.powersheet.Training.Block;
import com.example.powersheet.Training.Week;

import java.util.ArrayList;

public class BlockAdapter extends ArrayAdapter {
    ArrayList<Block> blocks;
    Context context;
    public BlockAdapter(Context c, ArrayList<Block> e, int position) {
        super(c, position);
        context = c;
        blocks = e;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
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
        return blocks.size();
    }

    @Override
    public Object getItem(int i) {
        return blocks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return blocks.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Block block = blocks.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.genric_entry_view, null);

            TextView blockTitle = (TextView) view.findViewById(R.id.titleTextView);
            blockTitle.setText(block.getBlockTitle());

            TextView blockValue = (TextView) view.findViewById(R.id.valueTextView);
            //dayValue.setText();
        }
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return blocks.size();
    }

    @Override
    public boolean isEmpty() {
        return blocks.isEmpty();
    }
}
