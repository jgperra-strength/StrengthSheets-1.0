package com.example.powersheet.Spreadsheet;

public class Cell implements java.io.Serializable {
    private int row;
    private int column;
    private String value;

    Cell(int r, int c, String val) {
        row = r;
        column = c;
        value = val;
    }

    public void setValue(String v) {
        value = v;
    }

    public String getValue() {
        return value;
    }

    public void setLocation(int r, int c) {
        row = r;
        column = c;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
