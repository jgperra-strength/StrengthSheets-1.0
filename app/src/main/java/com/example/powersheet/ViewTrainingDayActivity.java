package com.example.powersheet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.powersheet.APIControllers.PhotosAPIController;
import com.example.powersheet.APIDataStructures.PhotosMediaItem;
import com.example.powersheet.CustomAdapters.ExerciseAttributeAdapter;
import com.example.powersheet.CustomAdapters.ExerciseRecyclerAdapter;
import com.example.powersheet.CustomAdapters.Helper.OnStartDragListener;
import com.example.powersheet.CustomAdapters.Helper.SimpleItemTouchHelperCallback;
import com.example.powersheet.CustomAdapters.PhotosMediaAdapter;
import com.example.powersheet.Training.Day;
import com.example.powersheet.Training.Exercise;
import com.example.powersheet.Training.ExerciseAttribute;
import com.example.powersheet.UserData.User;

import java.util.ArrayList;

public class ViewTrainingDayActivity extends AppCompatActivity {
    private ArrayList<Exercise> exercises;
   // private ExerciseAdapter exerciseAdapter;
    //private ListView exercisesListView;

    private RecyclerView recyclerView;
    private ExerciseRecyclerAdapter exerciseAdapter;

    @Override
    public void onResume() {
        super.onResume();
        //if (!exercises.isEmpty()) exercisesListView.setAdapter(exerciseAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_training_day);

        //Allow screen to adjust correctly when keyboard appears
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);

        Intent i = getIntent();
        final int blockIdx = Integer.parseInt(i.getExtras().getString("blockIdx"));
        final int weekIdx = Integer.parseInt(i.getExtras().getString("weekIdx"));
        final int dayIdx = Integer.parseInt(i.getExtras().getString("dayIdx"));

        TextView dayTitle = (TextView) this.findViewById(R.id.dayTitle);
        Day day = ViewSheetFileActivity.sheetFile.getTrainingSheet().getBlocks().get(blockIdx).getWeeks().get(weekIdx).getDays().get(dayIdx);
        dayTitle.setText(day.getDayTitle());

       /* exercisesListView = (ListView) this.findViewById(R.id.exercisesListView);

        exercises = day.getExercises();
        exerciseAdapter = new ExerciseAdapter(this, exercises, R.layout.exercise_entry_view);
        if (!exercises.isEmpty()) exercisesListView.setAdapter(exerciseAdapter);*/

        // set up the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        OnStartDragListener dragListener = new OnStartDragListener();
        exercises = day.getExercises();
        exerciseAdapter = new ExerciseRecyclerAdapter(this, dragListener, exercises);

        recyclerView.setAdapter(exerciseAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(exerciseAdapter, this);
        ((SimpleItemTouchHelperCallback) callback).setLongPressDrag(true);
        ((SimpleItemTouchHelperCallback) callback).setSwipeEnabled(false);
        dragListener.mItemTouchHelper = new ItemTouchHelper(callback);
        dragListener.mItemTouchHelper.attachToRecyclerView(recyclerView);

        /*exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final HorizontalScrollView attributesScroll = (HorizontalScrollView) view.findViewById(R.id.attributeScrollView);
                final Button optionsButton = (Button) view.findViewById(R.id.optionsButton);
                final Button deleteButton = (Button) view.findViewById(R.id.deleteButton);
                final Button unselectButton = (Button) view.findViewById(R.id.unselectButton);

                for (int c = 0; c < exercisesListView.getChildCount(); ++c) {
                    View v = exercisesListView.getChildAt(c);

                    HorizontalScrollView scroll = (HorizontalScrollView) v.findViewById(R.id.attributeScrollView);
                    Button edit = (Button) v.findViewById(R.id.optionsButton);
                    Button delete = (Button) v.findViewById(R.id.deleteButton);
                    Button unselect = (Button) v.findViewById(R.id.unselectButton);

                    v.setSelected(false);
                    v.setBackgroundColor(Color.WHITE);
                    scroll.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                    unselect.setVisibility(View.GONE);
                }

                view.setSelected(true);
                view.setBackgroundColor(Color.LTGRAY);
                attributesScroll.setVisibility(View.GONE);
                optionsButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                unselectButton.setVisibility(View.VISIBLE);

                final int idx = i;
                optionsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(ViewTrainingDayActivity.this, view);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.exercise_attribute_menu, popup.getMenu());

                        //SubMenu editSubMenu = popup.getMenu().addSubMenu(0, 0, Menu.NONE, "Edit");
                        popup.getMenu().add(0, 1, Menu.NONE, "Edit");
                        //editSubMenu.add(0, 2, Menu.NONE, "Slot");

                        SubMenu viewSubMenu = popup.getMenu().addSubMenu(1, 0, Menu.NONE, "View");
                        viewSubMenu.add(1, 1, Menu.NONE, "Notes");
                        viewSubMenu.add(1, 2, Menu.NONE, "Videos");

                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getGroupId()) {
                                    case 0:
                                        addOrEditExercise(idx);
                                        break;

                                    case 1:
                                        switch(menuItem.getItemId()) {
                                            case 1:
                                                displayNotes(idx, attributesScroll);
                                                break;

                                            case 2:

                                                break;
                                        }
                                        break;
                                }

                                return false;
                            }
                        });
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exercises.remove(idx);
                        if (!exercises.isEmpty()) exercisesListView.setAdapter(exerciseAdapter);
                        else exercisesListView.setAdapter(null);
                    }
                });

                final View v1 = view;
                unselectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        v1.setSelected(true);
                        v1.setBackgroundColor(Color.WHITE);
                        attributesScroll.setVisibility(View.VISIBLE);
                        optionsButton.setVisibility(View.GONE);
                        deleteButton.setVisibility(View.GONE);
                        unselectButton.setVisibility(View.GONE);
                    }
                });

            }
        });*/
    }

    public void addExercise(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        addOrEditExercise(-1);
    }

    public void addOrEditExercise(final int exerciseIdx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View popup = inflater.inflate(R.layout.popup_exercise_add, null);

        ((TextView) popup.findViewById(R.id.actionTextView)).setText(exerciseIdx == -1 ? "Add Exercise" : "Edit Exercise");

        final EditText name = (EditText) popup.findViewById(R.id.editNameView);
        if (exerciseIdx != -1) name.setText(exercises.get(exerciseIdx).getTitle());

        final ListView attributeListView = (ListView) popup.findViewById(R.id.attributesListView);

        final ArrayList<ExerciseAttribute> attributes;
        if (exerciseIdx == -1) attributes = new ArrayList<>();
        else {
            attributes = exercises.get(exerciseIdx).getAttributes();
            /*if (!exercises.get(exerciseIdx).getNotes().isEmpty()) {
                attributes.remove(attributes.size() - 1);
            }*/
        }

        final ExerciseAttributeAdapter eaAdapter = new ExerciseAttributeAdapter(this, attributes, R.layout.exercise_attribute_view);
        attributeListView.setAdapter(eaAdapter);

        Button attributesButton = (Button) popup.findViewById(R.id.attributesButton);
        //setupAttributesButton(attributesButton, attributes, eaAdapter, 0);
        setupAttributesButton(attributesButton, popup);

        final EditText notes = popup.findViewById(R.id.notesEditText);
        if (exerciseIdx != -1) notes.setText(exercises.get(exerciseIdx).getNotes());

        Button notesButton = (Button) popup.findViewById(R.id.notesButton);
        setupNotesButton(notesButton, popup);

        ArrayList<PhotosMediaItem> mediaItems = exerciseIdx == -1 ? (new ArrayList<>()) : (ArrayList<PhotosMediaItem>) exercises.get(exerciseIdx).getMedia().clone();
        ListView mediaListView = (ListView) popup.findViewById(R.id.mediaListView);
        PhotosMediaAdapter mediaAdapter = new PhotosMediaAdapter(ViewTrainingDayActivity.this,
                mediaItems, R.layout.photos_media_entry, false, true);
        if (!mediaItems.isEmpty()) mediaListView.setAdapter(mediaAdapter);

        mediaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PhotosAPIController photosAPIController = new PhotosAPIController(ViewTrainingDayActivity.this);
                photosAPIController.loadAndDisplayMedia(mediaItems.get(i).getMediaItemId());
            }
        });

        Button mediaButton = (Button) popup.findViewById(R.id.mediaButton);
        setupMediaButton(mediaButton, popup, mediaItems, mediaAdapter, mediaListView);

        final EditText feedback = popup.findViewById(R.id.feedbackEditText);
        if (exerciseIdx != -1) feedback.setText(exercises.get(exerciseIdx).getFeedback());

        Button feedbackButton = (Button) popup.findViewById(R.id.feedbackButton);
        setupFeedbackButton(feedbackButton, popup);

        Button addButton = (Button) popup.findViewById(R.id.addButton);
        setupAddButton(addButton, popup, attributes, mediaAdapter, mediaListView);


        /*Button countsButton = (Button) popup.findViewById(R.id.countsButton);
        setupButton(countsButton, attributes, eaAdapter, 1);

        Button intensityButton = (Button) popup.findViewById(R.id.intensitiesButton);
        setupButton(intensityButton, attributes, eaAdapter, 2);

        Button restButton = (Button) popup.findViewById(R.id.restButton);
        setupButton(restButton, attributes, eaAdapter, 3);*/

        /*int sizeSpinner = exerciseIdx == -1 ? exercises.size() + 1 : exercises.size();
        String[] arraySpinner = new String[sizeSpinner];
        for (int i = 0; i < exercises.size(); ++i) {
            arraySpinner[i] = Integer.toString(i + 1);
        }
        if (exerciseIdx == -1) arraySpinner[arraySpinner.length - 1] = Integer.toString(arraySpinner.length);
        final Spinner indexSpinner = (Spinner) popup.findViewById(R.id.indexSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        indexSpinner.setAdapter(adapter);
        if (exerciseIdx == -1) indexSpinner.setSelection(arraySpinner.length - 1);
        else indexSpinner.setSelection(exerciseIdx);*/

        //builder.setView(popup);

        //Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!name.getText().toString().isEmpty()) {
                    for (int i = 0; i < attributeListView.getChildCount(); ++i) {
                        attributes.get(i).setValue(((EditText) attributeListView.getChildAt(i).findViewById(R.id.attributeValueTextView)).getText().toString());
                    }

                    Exercise e = exerciseIdx == -1 ? (new Exercise(name.getText().toString())) : exercises.get(exerciseIdx);
                    /*if (!e.getNotes().isEmpty() && notes.getText().toString().isEmpty()) {
                        e.getAttributes().remove(exercises.get(exerciseIdx).getAttributes().size() - 1);
                    }
                    else if (e.getNotes().isEmpty() && !notes.getText().toString().isEmpty()) {
                        e.getAttributes().add(new ExerciseAttribute("NOTES",""));
                        recyclerView.setAdapter(exerciseAdapter);
                    }*/
                    e.setNotes(notes.getText().toString());
                    e.setFeedback(feedback.getText().toString());
                    e.setMedia(mediaItems);

                    if (exerciseIdx == -1) {
                        //Exercise e = new Exercise(name.getText().toString());
                        e.setAttributes(attributes);
                        exercises.add(e);
                    }
                    else {
                        //if (!exercises.get(exerciseIdx).getNotes().isEmpty()) attributes.add(new ExerciseAttribute("NOTES", ""));
                        exercises.get(exerciseIdx).setTitle(name.getText().toString());
                        /*int spinnerIdx = Integer.parseInt(indexSpinner.getSelectedItem().toString()) - 1;
                        if (spinnerIdx != exerciseIdx) {
                            Exercise temp = exercises.get(exerciseIdx);
                            exercises.remove(exerciseIdx);
                            exercises.add(spinnerIdx, temp);
                        }*/
                    }

                    if (!exercises.isEmpty()) {
                        recyclerView.setAdapter(exerciseAdapter);
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });

        builder.setView(popup);
        final AlertDialog d = builder.show();

        Button deleteButton = (Button) popup.findViewById(R.id.deleteButton);
        if (exerciseIdx == -1) deleteButton.setVisibility(View.GONE);
        else {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    exercises.remove(exerciseIdx);
                                    recyclerView.setAdapter(exerciseAdapter);
                                    d.dismiss();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewTrainingDayActivity.this);
                    builder.setMessage("Delete this exercise?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });
        }
    }

    private void setupAddButton(Button addButton, View popup, ArrayList<ExerciseAttribute> attributes, PhotosMediaAdapter photosMediaAdapter, ListView mediaListView) {
        TextView header = (TextView) popup.findViewById(R.id.headerText);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(header.getText().toString()) {
                    case "Attributes":
                        final ListView attributeListView = (ListView) popup.findViewById(R.id.attributesListView);

                        final ExerciseAttributeAdapter eaAdapter = new ExerciseAttributeAdapter(ViewTrainingDayActivity.this, attributes, R.layout.exercise_attribute_view);
                        attributeListView.setAdapter(eaAdapter);

                        chooseAttributes(addButton, attributes, eaAdapter);
                        break;
                    case "Media":
                        chooseMedia(addButton, mediaListView, photosMediaAdapter);
                        break;
                }
            }
        });
    }

    private void hideKeyboard(Activity activity, final View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setupNotesButton(Button notesButton, View popup) {
        TextView header = (TextView) popup.findViewById(R.id.headerText);
        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                header.setText("Notes");
                ((Button) popup.findViewById(R.id.addButton)).setVisibility(View.GONE);
                ((ListView) popup.findViewById(R.id.mediaListView)).setVisibility(View.GONE);
                ((ListView) popup.findViewById(R.id.attributesListView)).setVisibility(View.GONE);
                ((EditText) popup.findViewById(R.id.notesEditText)).setVisibility(View.VISIBLE);
                ((EditText) popup.findViewById(R.id.feedbackEditText)).setVisibility(View.GONE);
                hideKeyboard(ViewTrainingDayActivity.this, ((EditText) popup.findViewById(R.id.notesEditText)));
            }
        });
    }

    private void setupMediaButton(Button mediaButton, View popup, ArrayList<PhotosMediaItem> mediaItems, PhotosMediaAdapter mediaAdapter, ListView mediaListView) {
        TextView header = (TextView) popup.findViewById(R.id.headerText);
        mediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                header.setText("Media");
                ((ListView) popup.findViewById(R.id.mediaListView)).setVisibility(View.VISIBLE);
                ((Button) popup.findViewById(R.id.addButton)).setVisibility(View.VISIBLE);
                ((ListView) popup.findViewById(R.id.attributesListView)).setVisibility(View.GONE);
                ((EditText) popup.findViewById(R.id.notesEditText)).setVisibility(View.GONE);
                ((EditText) popup.findViewById(R.id.feedbackEditText)).setVisibility(View.GONE);
                hideKeyboard(ViewTrainingDayActivity.this, ((EditText) popup.findViewById(R.id.notesEditText)));

                //Retrieve thumbnails for media that need them.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<PhotosMediaItem> mediaItemsThumbnails = new ArrayList<>();
                        for (PhotosMediaItem item : mediaItems) {
                            if (item.getThumbnail() == null) {
                                mediaItemsThumbnails.add(item);
                            }
                        }
                        if (!mediaItemsThumbnails.isEmpty()) {
                            ProgressBar pbar = (ProgressBar) popup.findViewById(R.id.progressBar);
                            PhotosAPIController photosAPIController = new PhotosAPIController(ViewTrainingDayActivity.this);
                            photosAPIController.getItemThumbnails(mediaItemsThumbnails, mediaAdapter, mediaListView, pbar);
                        }
                    }
                });
            }
        });
    }

    private void setupFeedbackButton(Button feedbackButton, View popup) {
        TextView header = (TextView) popup.findViewById(R.id.headerText);
        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                header.setText("Feedback");
                ((Button) popup.findViewById(R.id.addButton)).setVisibility(View.GONE);
                ((ListView) popup.findViewById(R.id.attributesListView)).setVisibility(View.GONE);
                ((ListView) popup.findViewById(R.id.mediaListView)).setVisibility(View.GONE);
                ((EditText) popup.findViewById(R.id.feedbackEditText)).setVisibility(View.VISIBLE);
                ((EditText) popup.findViewById(R.id.notesEditText)).setVisibility(View.GONE);
                hideKeyboard(ViewTrainingDayActivity.this, ((EditText) popup.findViewById(R.id.notesEditText)));
            }
        });
    }

    private void setupAttributesButton(Button attributesButton, View popup) {
        TextView header = (TextView) popup.findViewById(R.id.headerText);
        attributesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                header.setText("Attributes");
                ((Button) popup.findViewById(R.id.addButton)).setVisibility(View.VISIBLE);
                ((ListView) popup.findViewById(R.id.attributesListView)).setVisibility(View.VISIBLE);
                ((ListView) popup.findViewById(R.id.mediaListView)).setVisibility(View.GONE);
                ((EditText) popup.findViewById(R.id.notesEditText)).setVisibility(View.GONE);
                ((EditText) popup.findViewById(R.id.feedbackEditText)).setVisibility(View.GONE);
                hideKeyboard(ViewTrainingDayActivity.this, ((EditText) popup.findViewById(R.id.notesEditText)));
            }
        });
    }

    private void chooseAttributes(Button b, final ArrayList<ExerciseAttribute> attributes, final ExerciseAttributeAdapter eaAdapter) {
       // b.setOnClickListener(new View.OnClickListener() {
         //   @Override
           // public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(ViewTrainingDayActivity.this, b);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.exercise_attribute_menu, popup.getMenu());

                ArrayList<String> attributeStrings = new ArrayList<>();
                //switch (which) {
                  //  case 0:
                        for (ExerciseAttribute.strengthMeasuresAttribute ea : ExerciseAttribute.strengthMeasuresAttribute.values()) attributeStrings.add(ea.toString());
                    //    break;

                    //case 1:
                        for (ExerciseAttribute.strengthCountsAttribute ea : ExerciseAttribute.strengthCountsAttribute.values()) attributeStrings.add(ea.toString());
                      //  break;

                    //case 2:
                        for (ExerciseAttribute.strengthIntensityAttribute ea : ExerciseAttribute.strengthIntensityAttribute.values()) attributeStrings.add(ea.toString());
                      //  break;

                    //case 3:
                        for (ExerciseAttribute.restAttributes ea : ExerciseAttribute.restAttributes.values()) attributeStrings.add(ea.toString());
                      //  break;
                //}

                for (int i = 0; i < attributeStrings.size(); ++i) {
                    popup.getMenu().add(0,i, Menu.NONE, attributeStrings.get(i)).setCheckable(true);
                    for (ExerciseAttribute att : attributes) {
                        if (att.getAttributeTitle() == attributeStrings.get(i)) popup.getMenu().getItem(i).setChecked(true);
                    }
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            default:
                                menuItem.setChecked(!menuItem.isChecked());
                                if (menuItem.isChecked()) {
                                    attributes.add(new ExerciseAttribute(menuItem.getTitle().toString(), ""));
                                    eaAdapter.notifyDataSetChanged();
                                }
                                else {
                                    int rmIdx = 0;
                                    for (int idx = 0; idx < attributes.size(); ++idx) {
                                        if (attributes.get(idx).getAttributeTitle() == menuItem.getTitle().toString()) {
                                            rmIdx = idx;
                                            break;
                                        }
                                    }
                                    attributes.remove(rmIdx);
                                    eaAdapter.notifyDataSetChanged();
                                }
                                break;
                        }

                        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                        menuItem.setActionView(new View(ViewTrainingDayActivity.this));
                        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                            @Override
                            public boolean onMenuItemActionExpand(MenuItem item) {
                                return false;
                            }

                            @Override
                            public boolean onMenuItemActionCollapse(MenuItem item) {
                                return false;
                            }
                        });

                        return false;
                    }
                });
                popup.show();
    }

    private void chooseMedia(Button b, ListView mediaListView, PhotosMediaAdapter photosMediaAdapter) {
        final PopupMenu popup = new PopupMenu(ViewTrainingDayActivity.this, b);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.exercise_attribute_menu, popup.getMenu());

        User user = ((MyApp) getApplicationContext()).user;
        for (int i = 0; i < user.getPhotosAlbums().size(); ++i) popup.getMenu().add(0, i, Menu.NONE, user.getPhotosAlbums().get(i).getTitle());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int idx = menuItem.getItemId();
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewTrainingDayActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View popup = inflater.inflate(R.layout.popup_media_album, null);

                ((TextView) popup.findViewById(R.id.titleTextView)).setText(menuItem.getTitle().toString() + " Media");

                ListView mediaListView2 = (ListView) popup.findViewById(R.id.mediaListView);
                PhotosMediaAdapter mediaAdapter = new PhotosMediaAdapter(ViewTrainingDayActivity.this,
                        user.getPhotosAlbums().get(idx).getMediaItems(), R.layout.photos_media_entry, true, true);
                if (!user.getPhotosAlbums().get(idx).getMediaItems().isEmpty()) mediaListView2.setAdapter(mediaAdapter);

                ((Button) popup.findViewById(R.id.uploadButton)).setVisibility(View.GONE);

                //Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < mediaListView2.getChildCount(); ++i) {
                            if (((CheckBox) mediaListView2.getChildAt(i).findViewById(R.id.checkBox)).isChecked()) {
                                photosMediaAdapter.getItems().add(user.getPhotosAlbums().get(idx).getMediaItems().get(i));
                                //PhotosMediaItem temp = new PhotosMediaItem(user.getPhotosAlbums().get(idx).getMediaItems().get(i).getMediaItemId(), user.getPhotosAlbums().get(idx).getMediaItems().get(i).getDate());
                                //photosMediaAdapter.getItems().add(temp);
                                mediaListView.setAdapter(photosMediaAdapter);
                            }
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });

                builder.setView(popup);
                builder.show();
                return false;
            }
        });

        popup.show();
    }


        //});
    //}

    /*private void displayNotes(int idx, HorizontalScrollView attributesScroll) {
        final Exercise exercise = exercises.get(idx);
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewTrainingDayActivity.this);
        View popupNotes = getLayoutInflater().inflate(R.layout.popup_notes, null);

        ((TextView) popupNotes.findViewById(R.id.notesTitleTextView)).setText(exercise.getTitle() + " Notes");

        final EditText notes = popupNotes.findViewById(R.id.notesEditText);
        notes.setText(exercise.getNotes());

        builder.setView(popupNotes);

        final LinearLayout layout = (LinearLayout) attributesScroll.getChildAt(0);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!exercise.getNotes().isEmpty() && notes.getText().toString().isEmpty()) {
                    exercise.getAttributes().remove(exercise.getAttributes().size() - 1);
                    layout.removeViewAt(layout.getChildCount() - 1);
                }
                else if (exercise.getNotes().isEmpty() && !notes.getText().toString().isEmpty()) {
                    exercise.getAttributes().add(new ExerciseAttribute("NOTES",""));
                    recyclerView.setAdapter(exerciseAdapter);
                }
                exercise.setNotes(notes.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }*/
}