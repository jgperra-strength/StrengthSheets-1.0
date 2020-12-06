package com.example.powersheet.CustomAdapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.powersheet.R;
import com.example.powersheet.Training.ExerciseAttribute;

import java.util.ArrayList;
import java.util.TreeMap;

public class ExerciseAttributeAdapter extends ArrayAdapter {
    ArrayList<ExerciseAttribute> attributes;
    Context context;

    TreeMap<Integer, String> hashMap;

    public ExerciseAttributeAdapter(Context c, ArrayList<ExerciseAttribute> e, int position) {
        super(c, position);
        context = c;
        attributes = e;

        hashMap = new TreeMap<>();
        for (int i = 0; i < e.size(); ++i) {
            hashMap.put(i, e.get(i).getValue());
        }
    }

    private class ViewHolder {
        private TextView attributeTitle;
        private EditText attributeValue;
        private int position;

        public ViewHolder(View view) {
            attributeTitle = (TextView) view.findViewById(R.id.attributeTitleTextView);
            attributeValue = (EditText) view.findViewById(R.id.attributeValueTextView);
        }

        public void setTitle(String t) {
            attributeTitle.setText(t);
        }

        public void setValue(String v) {
            attributeValue.setText(v);
        }
    }

    @Override
    public int getCount() {
        return attributes.size();
    }

    @Override
    public Object getItem(int i) {
        return attributes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ExerciseAttribute attribute = attributes.get(i);
        final ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.exercise_attribute_view, null);

            viewHolder = new ViewHolder(view);

            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.position = i;

        viewHolder.setTitle(attribute.getAttributeTitle());
        viewHolder.setValue(hashMap.get(i));

        viewHolder.attributeValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //attributes.get(viewHolder.position).setValue(charSequence.toString());
                hashMap.put(viewHolder.position, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                hashMap.put(viewHolder.position, editable.toString());
            }
        });

        final int idx = i;
        Button removeButton = (Button) view.findViewById(R.id.removeButton);
        removeButton.setTag(i);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Integer index = (Integer) view.getTag();
                attributes.remove(idx);
                notifyDataSetChanged();
                hashMap.remove(idx);

                TreeMap<Integer, String> tempMap = new TreeMap<>();
                for (Integer key : hashMap.keySet()) {
                    int k = key > idx ? key - 1 : key;
                    tempMap.put(k, hashMap.get(key));
                }
                hashMap = tempMap;
            }
        });

            /*TextView exerciseName = (TextView) view.findViewById(R.id.exerciseFillTextView);
            exerciseName.setText(exercise.getTitle());

            TextView sets = (TextView) view.findViewById(R.id.setsFillTextView);
            sets.setText(exercise.getNumSets());

            TextView reps = (TextView) view.findViewById(R.id.repsFillTextView);
            reps.setText(exercise.getNumReps());

            TextView load = (TextView) view.findViewById(R.id.loadFillTextView);
            load.setText(exercise.getLoad());

            TextView intensity = (TextView) view.findViewById(R.id.intensityFillTextView);
            intensity.setText(exercise.getIntensityValue());*/

            //ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            //if (layoutParams != null) layoutParams.height = 60;
        //}
        return view;
    }
}
