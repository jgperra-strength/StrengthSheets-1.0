package com.example.powersheet.Util;

import android.content.Context;
import android.net.Uri;

import com.example.powersheet.Spreadsheet.Sheet;
import com.example.powersheet.UserData.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class FileSystemUtil {
    private static String baseDirectory;

    public static void setBaseDirectory(String dir) {
        baseDirectory = dir;
    }

    public static void createSpreadsheetsDirectory() {
        File myDir = new File(baseDirectory, "spreadsheets");
        myDir.mkdir();
        System.out.println(myDir.exists());
    }

    public static void createSheetFileDirectory(String sheetFileName) {
        File myDir = new File(baseDirectory + File.separator + "spreadsheets", sheetFileName.toLowerCase());
        System.out.println("CREATING " + myDir.getAbsolutePath());
        myDir.mkdir();

        File trainingFile = new File(myDir, "training.txt");
        System.out.println(trainingFile.getAbsolutePath());
        try {
            trainingFile.createNewFile();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getSpreadsheetFileDirectory(String spreadsheetFileName) {
        return baseDirectory + File.separator + "spreadsheets" + File.separator + spreadsheetFileName.toLowerCase();
    }

    public static String getSheetFileMetadataDirectory(String spreadsheetFileName) {
        return baseDirectory + File.separator + "spreadsheets" + File.separator + spreadsheetFileName.toLowerCase() + File.separator + "fileMetadata.txt";
    }

    public static String getTrainingSheetDirectory(String spreadsheetFileName) {
        return baseDirectory + File.separator + "spreadsheets" + File.separator + spreadsheetFileName.toLowerCase() + File.separator + "training.txt";
    }

    public static void serializeSheet(Sheet s, String dir) {
        try {
            FileOutputStream fos = new FileOutputStream(dir);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(s);
            out.close();
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Sheet deserializeSheet(String dir) {
        Sheet result = null;

        try {
            FileInputStream fis = new FileInputStream(dir);
            ObjectInputStream in = new ObjectInputStream(fis);
            result = (Sheet) in.readObject();
            in.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void serializeUserObject(User u) {
        try {
            FileOutputStream fos = new FileOutputStream(baseDirectory + File.separator + "user.txt");
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(u);
            out.close();
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User deserializeUser() {
        User result = null;

        try {
            FileInputStream fis = new FileInputStream(baseDirectory + File.separator + "user.txt");
            ObjectInputStream in = new ObjectInputStream(fis);
            result = (User) in.readObject();
            in.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String setupMedia(Context c, Uri uri) {
        File temp = new File(baseDirectory, "tempVideo");

        try {
            temp.createNewFile();
            InputStream in =  c.getContentResolver().openInputStream(uri);
            OutputStream out = new FileOutputStream(temp);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return temp.getAbsolutePath();
        //System.out.println(temp.length());
        //if (temp.delete()) System.out.println("DELETE SUCCESSFUL");
    }
}
