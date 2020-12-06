package com.example.powersheet.Training;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Block implements Serializable {
    private String blockTitle;
    private int numWeeks;
    private ArrayList<Week> weeks;
    private Date beginDate;
    private int idx;

    public Block(String bt, int nw, int idx) {
        blockTitle = bt;
        numWeeks = nw;
        weeks = new ArrayList<>();
        this.idx = idx;
    }

    public Block(int blockIdx) {
        this.idx = blockIdx;
        weeks = new ArrayList<>();
    }

    public Block() {
        weeks = new ArrayList<>();
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getNumWeeks() {
        return numWeeks;
    }

    public void addWeek(Week w) {
        weeks.add(w);
    }

    public String getBlockTitle() {
        return blockTitle;
    }

    public void setBlockTitle(String blockTitle) {
        this.blockTitle = blockTitle;
    }

    public ArrayList<Week> getWeeks() {
        return weeks;
    }
}
