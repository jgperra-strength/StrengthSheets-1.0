package com.example.powersheet;

import android.app.Application;

import com.example.powersheet.UserData.User;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class MyApp extends Application {
    public User user;
    public GoogleAccountCredential credentials;
}
