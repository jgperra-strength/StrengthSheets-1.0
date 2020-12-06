package com.example.powersheet.UserData;

import com.example.powersheet.APIDataStructures.PhotosAlbum;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private ArrayList<PhotosAlbum> photosAlbums;

    public User() {
        photosAlbums = new ArrayList<>();
    }

    public ArrayList<PhotosAlbum> getPhotosAlbums() {
        return photosAlbums;
    }
}
