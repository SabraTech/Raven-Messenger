package com.example.space.chatapp.models;


import java.util.ArrayList;

public class Notifications {

    private ArrayList<Request> requests;

    public Notifications() {
        requests = new ArrayList<>();
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }
}
