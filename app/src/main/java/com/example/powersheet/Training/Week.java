package com.example.powersheet.Training;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Week implements Serializable {
    private String title;
    private Date beginDate;
    private Date endDate;
    private int numDays;
    private int numCols;
    private ArrayList<Day> days;

    private int idx;
    private Block parentBlock;

    public Week(String t, Date d, int nd, int idx, Block parentBlock) {
        title = t;
        beginDate = d;
        numDays = nd;
        days = new ArrayList<>();
        this.idx = idx;
        this.parentBlock = parentBlock;
    }

    public Week(String t) {
        title = t;
        days = new ArrayList<>();
    }

    public Week(int idx, Block parentBlock) {
        this.parentBlock = parentBlock;
        this.idx = idx;
        days = new ArrayList<>();
    }

    public Week() {
        days = new ArrayList<>();
    }

    public int getNumCols() {
        return numCols;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public Block getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(Block parentBlock) {
        this.parentBlock = parentBlock;
    }


    public void setTitle(String t) {
        title = t;
    }

    public String getTitle() {
        return title;
    }

    public void setBeginDate(Date d) {
        beginDate = d;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setEndDate(Date d) {
        endDate = d;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getNumDays() {
        return numDays;
    }

    public void addDay(Day d) {
        days.add(d);
    }

    public ArrayList<Day> getDays() {
        return days;
    }
}
