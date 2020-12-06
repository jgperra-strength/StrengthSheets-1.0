package com.example.powersheet.Training;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Day implements Serializable {
    private String dayTitle;
    private int numExercises;
    private ArrayList<Exercise> exercises;
    private Date date;
    private Date time;

    private int idx;
    private Week parentWeek;

    public Day(String dt, int numEx, int idx, Week parentWeek) {
        dayTitle = dt;
        numExercises = numEx;
        exercises = new ArrayList<>();
        this.idx = idx;
        this.parentWeek = parentWeek;
    }

    public Day(String dt) {
        dayTitle = dt;
        numExercises = 0;
        exercises = new ArrayList<>();
    }

    public Day(int numExercises, int idx, Week parentWeek) {
        this.parentWeek = parentWeek;
        this.numExercises = numExercises;
        this.idx = idx;
        exercises = new ArrayList<>();
    }

    public Day(){
        exercises = new ArrayList<>();
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public Week getParentWeek() {
        return parentWeek;
    }

    public void setParentWeek(Week parentWeek) {
        this.parentWeek = parentWeek;
    }

    public String getDayTitle() {
        return dayTitle;
    }

    public void setDate(Date d) {
        date = d;
    }

    public Date getDate() {
        return date;
    }

    public void setTime(Date t) { time = t; }

    public Date getTime() {
        return time;
    }

    public void setDayTitle(String dt) {
        dayTitle = dt;
    }

    public int getNumExercises() {
        return numExercises;
    }

    public void addExercise(Exercise e) {
        exercises.add(e);
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    @Override
    public String toString() {
        return "Day{" +
                "dayTitle='" + dayTitle + '\'' +
                ", numExercises=" + numExercises +
                ", exercises=" + exercises +
                ", date=" + date +
                '}';
    }
}
