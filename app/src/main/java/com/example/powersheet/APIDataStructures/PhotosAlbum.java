package com.example.powersheet.APIDataStructures;

import java.io.Serializable;
import java.util.ArrayList;

public class PhotosAlbum implements Serializable {
    private String title;
    private String albumId;
    private ArrayList<PhotosMediaItem> mediaItems;

    public PhotosAlbum() {}

    public PhotosAlbum(String t, String id) {
        title = t;
        albumId = id;
        mediaItems = new ArrayList<>();
    }

    public PhotosAlbum(String t) {
        title = t;
        mediaItems = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public ArrayList<PhotosMediaItem> getMediaItems() {
        return mediaItems;
    }

    public void setMediaItems(ArrayList<PhotosMediaItem> mediaItems) {
        this.mediaItems = mediaItems;
    }
}
