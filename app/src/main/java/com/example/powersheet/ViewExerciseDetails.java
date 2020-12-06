package com.example.powersheet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.powersheet.CustomAdapters.ExerciseAttributeAdapter;
import com.example.powersheet.Training.Exercise;
import com.example.powersheet.Training.ExerciseAttribute;

import java.util.ArrayList;

public class ViewExerciseDetails extends AppCompatActivity {
    private ListView attributeListView;
    private ExerciseAttributeAdapter eaAdapter;
    private ArrayList<ExerciseAttribute> attributes;
    private Exercise exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_exercise_details);

        //Allow screen to adjust correctly when keyboard appears
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);

        Intent intent = getIntent();
        int blockIdx = Integer.parseInt(intent.getExtras().getString("blockIdx"));
        int weekIdx = Integer.parseInt(intent.getExtras().getString("weekIdx"));
        int dayIdx = Integer.parseInt(intent.getExtras().getString("dayIdx"));
        int exerciseIdx = Integer.parseInt(intent.getExtras().getString("exerciseIdx"));

        System.out.println("BLOCK = " + blockIdx);
        System.out.println("WEEK = " + weekIdx);
        System.out.println("DAY = " + dayIdx);
        System.out.println("EXERCISE = " + exerciseIdx);


        exercise = ViewSheetFileActivity.sheetFile.getTrainingSheet().getBlocks().get(blockIdx).getWeeks().get(weekIdx).getDays().get(dayIdx).getExercises().get(exerciseIdx);

        TextView exerciseTitle = (TextView) this.findViewById(R.id.exerciseTitle);
        exerciseTitle.setText(exercise.getTitle());

        attributeListView = (ListView) this.findViewById(R.id.attributesListView);
        attributeListView.setItemsCanFocus(true);

        if (!exercise.getAttributes().isEmpty()) {
            if (exercise.getAttributes().get(exercise.getAttributes().size() - 1).getAttributeTitle() == "NOTES") {
                exercise.getAttributes().remove(exercise.getAttributes().size() - 1);
            }
        }

        attributes = (ArrayList<ExerciseAttribute>) exercise.getAttributes().clone();

        eaAdapter = new ExerciseAttributeAdapter(this, attributes, R.layout.exercise_attribute_view);
        attributeListView.setAdapter(eaAdapter);

        //((EditText) this.findViewById(R.id.notesEditText)).setText(exercise.getNotes());
    }

    public void addAttribute(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.exercise_attribute_menu, popup.getMenu());

        SubMenu strengthMeasuresSubMenu = popup.getMenu().addSubMenu(0, 0, Menu.NONE, "STRENGTH MEASURES");
        int count = 1;
        for (ExerciseAttribute.strengthMeasuresAttribute aType : ExerciseAttribute.strengthMeasuresAttribute.values()) {
            strengthMeasuresSubMenu.add(0, count++, Menu.NONE, aType.toString());
        }

        SubMenu strengthCountsSubMenu = popup.getMenu().addSubMenu(1, 0, Menu.NONE, "STRENGTH COUNTS");
        count = 1;
        for (ExerciseAttribute.strengthCountsAttribute aType : ExerciseAttribute.strengthCountsAttribute.values()) {
            strengthCountsSubMenu.add(1, count++, Menu.NONE, aType.toString());
        }

        SubMenu strengthIntensitiesSubMenu = popup.getMenu().addSubMenu(2, 0, Menu.NONE, "STRENGTH INTENSITIES");
        count = 1;
        for (ExerciseAttribute.strengthIntensityAttribute aType : ExerciseAttribute.strengthIntensityAttribute.values()) {
            strengthIntensitiesSubMenu.add(2, count++, Menu.NONE, aType.toString());
        }



        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case 0:
                        break;
                    default:
                        attributes.add(new ExerciseAttribute(menuItem.getTitle().toString(), ""));
                        eaAdapter.notifyDataSetChanged();
                        break;
                    /*case 0 :
                        switch (menuItem.getItemId()) {
                            case 0:
                                break;
                            default:
                                attributes.add(new ExerciseAttribute(menuItem.getTitle().toString(), ""));
                                eaAdapter.notifyDataSetChanged();
                        }
                        break;*/
                }

                return false;
            }
        });
    }

    public void saveExercise(View view) {
        String title = ((EditText) this.findViewById(R.id.exerciseTitle)).getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, "Exercise not saved. Empty exercise title.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Button b = (Button) view;
        b.setEnabled(false);
        for (int i = 0; i < attributes.size(); ++i) {
            EditText attributeValue = (EditText) eaAdapter.getView(i, null, attributeListView).findViewById(R.id.attributeValueTextView);
            attributes.get(i).setValue(attributeValue.getText().toString());
            /*if (attributeListView.getChildAt(i) != null) {
                System.out.println("CHILD " + i);
                EditText attributeValue = (EditText) attributeListView.getChildAt(i).findViewById(R.id.attributeValueTextView);
                attributes.get(i).setValue(attributeValue.getText().toString());
            }*/
        }

        exercise.setTitle(title);
        exercise.setAttributes(attributes);
//        exercise.setNotes(((EditText) this.findViewById(R.id.notesEditText)).getText().toString());

        /*if (!exercise.getNotes().isEmpty()) {
            attributes.add(new ExerciseAttribute("NOTES", ""));
        }*/

        ViewSheetFileActivity.sheetFile.serializeTrainingSheet();

        b.setEnabled(true);
        Toast.makeText(this, "Exercise saved!",
                Toast.LENGTH_SHORT).show();
    }
}