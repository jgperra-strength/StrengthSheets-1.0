package com.example.powersheet.APIDataStructures;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.example.powersheet.Util.BitmapDataObject;

import java.io.Serializable;
import java.util.Date;

public class PhotosMediaItem implements Serializable {
    private String mediaItemId;

    private BitmapDataObject bitmapDataObject;
    //private Bitmap thumbnail;

    private Date date;
    private String uri;

    public PhotosMediaItem() {}

    public PhotosMediaItem(String id, Date d, BitmapDataObject thumbnail, Uri u) {
        mediaItemId = id;
        bitmapDataObject = thumbnail;
        date = d;
        uri = u.toString();
    }

    public PhotosMediaItem(String id, Date d, BitmapDataObject thumbnail) {
        mediaItemId = id;
        bitmapDataObject = thumbnail;
        date = d;
    }

    public PhotosMediaItem(String id, Date d) {
        mediaItemId = id;
        date = d;
        bitmapDataObject = null;
    }

    public void setId(String id) {
        mediaItemId = id;
    }

    public String getMediaItemId() {
        return mediaItemId;
    }

    public Bitmap getThumbnail() {
        if (bitmapDataObject == null) return null;
        return bitmapDataObject.getBitmap();
    }

    public void setBitmapDataObject(BitmapDataObject bitmapDataObject) {
        this.bitmapDataObject = bitmapDataObject;
    }

    public Date getDate() {
        return date;
    }

    public Uri getUri() {
        return Uri.parse(uri);
    }

    public String getMimeType(Context context) {
        String mimeType = null;
        Uri realUri = Uri.parse(uri);
        if (realUri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(realUri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri);
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }
}
