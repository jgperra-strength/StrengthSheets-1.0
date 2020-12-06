package com.example.powersheet.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class BitmapDataObject implements Serializable {
    private byte[] imageByteArray;

    public BitmapDataObject(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.imageByteArray = stream.toByteArray();
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray,
                0, imageByteArray.length);
        return bitmap;
    }
}
