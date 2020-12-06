package com.example.powersheet.Spreadsheet;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Sheet implements Serializable {
    //Title of this sheet.
    String sheetTitle;

    //Data stored within cells in sheet.
    protected ArrayList<ArrayList<Cell>> cells;

    protected HashMap<String, String> metadata;

    Sheet() {
        cells = new ArrayList<>();
    }

    protected Sheet(List<List<Object>> c, String title, HashMap<String, String> metadata) {
        sheetTitle = title;
        cells = new ArrayList<>();
        this.metadata = metadata;
        populateCells(c);
    }

    protected String getSheetRange(int leftBoundX, int leftBoundY, int rightBoundX, int rightBoundY) {
        String range = "";

        // Map alphabet to indices
        Map<Integer, Character> numToLetter = new HashMap<>();
        for (int i = 0; i < 26; ++i) {
            numToLetter.put(i, (char) ('a' + i));
        }

        String rangeLeft = "";
        while (leftBoundX >= 0) {
            rangeLeft += numToLetter.get(leftBoundX % 26);
            leftBoundX -= 26;
        }
        rangeLeft += leftBoundY;

        String rangeRight = "";
        while (rightBoundX >= 0) {
            rangeRight += numToLetter.get(rightBoundX % 26);
            rightBoundX -= 26;
        }
        rangeRight += rightBoundX;

        range = rangeLeft + ":" + rangeRight;

        return range;
    }

    private void populateCells(List<List<Object>> data) {
        if (data == null) return;
        //Populate the cells with the data from the sheet.
        //Iterate through the columns first within each row.
        for (int r = 0; r < data.size(); ++r) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int c = 0; c < data.get(r).size(); ++c) {
                row.add( new Cell(r, c, data.get(r).get(c).toString()));
            }
            cells.add(row);
        }
    }

    public void printCells() {
        if (cells == null) return;
        for (ArrayList<Cell> row : cells) {
            for (Cell c : row) System.out.print((c.getValue().isEmpty() ? "NULL" : c.getValue()) + " ");
            System.out.println();
        }
    }


    public abstract ArrayList<ArrayList<Cell>> getSheetData();
    //public abstract void serialize(String fileName);
    //public abstract void deserialize(String fileName);



    /*private final String[] columns = {"Week", "Exercises", "Sets", "Reps", "Intensity", "Load", "RPE",
                                        "lsRPE", "E1RM", "Client Notes", "Coach Notes" };
    private int numWeeks;
    private ArrayList<HashMap<String, ArrayList<String>>> table;

    //data of entire training sheet should be received in column major order
    Sheet(List<List<Object>> data) {
        //for (String s : columns) table.put(s, new ArrayList<String>());
        table = new ArrayList<HashMap<String, ArrayList<String>>>();
        numWeeks = data.size() / columns.length;
        for (int i = 0; i < numWeeks; ++i) table.add(new HashMap<String, ArrayList<String>>());

        //give each week its own table
        for (int w = 0; w < table.size(); ++w) {
            //iterate through the rows of each column
            for (int c = w * columns.length; c < w * columns.length + columns.length; ++c) {
                String col = (String) data.get(c).get(0);
                table.get(w).put(col, new ArrayList<String>());
                for (int r = 1; r < data.get(c).size(); ++r) {
                    table.get(w).get(col).add((String) data.get(c).get(r));
                }
            }
        }
    }*/
}
