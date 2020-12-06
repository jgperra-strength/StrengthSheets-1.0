package com.example.powersheet.Training;

import com.example.powersheet.APIDataStructures.PhotosMediaItem;

import java.io.Serializable;
import java.util.ArrayList;

public class Exercise implements Serializable {
    public enum intensity {
        RPE,
        lsRPE,
        RIR,
        PCTG
    }

    private String title;
    private int numSets;
    private int numReps;
    private float load;

    private intensity intensityType;
    String intensityValue;

    private ArrayList<ExerciseAttribute> attributes;
    private ArrayList<PhotosMediaItem> media;
    private String notes;
    private String feedback;

    public Exercise(String t, int ns, int nr, int l, String i) {
        title = t;
        numSets = ns;
        numReps = nr;
        load = l;
        intensityValue = i;
        attributes = new ArrayList<>();
        media = new ArrayList<>();
        notes = "";
        feedback="";
    }

    public Exercise(String name) {
        title = name;
        attributes = new ArrayList<>();
        media = new ArrayList<>();
        notes = "";
        feedback = "";
    }

    public Exercise() {};

    public ArrayList<ExerciseAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<ExerciseAttribute> attributes) {
        this.attributes = attributes;
    }

    public ArrayList<PhotosMediaItem> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<PhotosMediaItem> media) {
        this.media = media;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setNotes(String n) {
        notes = n;
    }

    public String getNotes() {
        return notes;
    }

    public intensity getIntensityType() {
        return intensityType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
        title = t;
    }

    public String getNumSets() {
        return Integer.toString(numSets);
    }

    public String getNumReps() {
        return Integer.toString(numReps);
    }

    public String getLoad() {
        return Integer.toString((int) load);
    }

    public String getIntensityValue() {
        return intensityValue;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "title='" + title + '\'' +
                ", numSets=" + numSets +
                ", numReps=" + numReps +
                ", load=" + load +
                ", intensityType=" + intensityType +
                ", intensityValue='" + intensityValue + '\'' +
                '}';
    }
}
