package com.example.space.ravenmessenger.models;


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
