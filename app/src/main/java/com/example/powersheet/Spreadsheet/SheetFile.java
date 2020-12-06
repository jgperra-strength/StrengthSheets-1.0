package com.example.powersheet.Spreadsheet;

import com.example.powersheet.Util.FileSystemUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SheetFile {
    private String title;
    private Configuration config;
    private TrainingSheet trainingSheet;
    private ArrayList<Sheet> sheets;

    public SheetFile(String t) {
        title = t;
    }

    public SheetFile() {}

    public String getSheetFileTitle() {
        return title;
    }

    public void setTrainingSheet(TrainingSheet ts) {
        trainingSheet = ts;
    }

    public TrainingSheet getTrainingSheet() {
        return trainingSheet;
    }

    public void serializeTrainingSheet() {
        FileSystemUtil.serializeSheet(trainingSheet, FileSystemUtil.getTrainingSheetDirectory(title));
    }

    public void serializeSheetFile() {
        //serialize sheets within file
        //trainingSheet.serialize(FileSystemUtil.getTrainingSheetDirectory(title));
        //FileSystemUtil.serializeSheet(trainingSheet, FileSystemUtil.getTrainingSheetDirectory(title));
        serializeTrainingSheet();

        try {
            FileOutputStream fos = new FileOutputStream(FileSystemUtil.getSheetFileMetadataDirectory(title));
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(title);
            out.close();
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserializeSheetFile(String sheetFileTitle) {

        try {
            FileInputStream fis = new FileInputStream(FileSystemUtil.getSheetFileMetadataDirectory(sheetFileTitle));
            ObjectInputStream in = new ObjectInputStream(fis);
            title = (String) in.readObject();
            in.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (title != null) {
            //deserialize sheets within file
            //trainingSheet.deserialize(FileSystemUtil.getTrainingSheetDirectory(title));
            trainingSheet = (TrainingSheet) FileSystemUtil.deserializeSheet(FileSystemUtil.getTrainingSheetDirectory(title));
        }
    }
}
