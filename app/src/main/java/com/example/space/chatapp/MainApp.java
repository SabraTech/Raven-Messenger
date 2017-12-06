package com.example.space.chatapp;


import android.app.Application;

public class MainApp extends Application {

    private static boolean chatActivityOpen = false;

    public static boolean isChatActivityOpen() {
        return chatActivityOpen;
    }

    public static void setChatActivityOpen(boolean chatActivityOpen) {
        MainApp.chatActivityOpen = chatActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
