package com.example.space.ravenmessenger.models;

import java.util.ArrayList;


public class Conversation {
    private ArrayList<Message> messages;

    public Conversation() {
        messages = new ArrayList<>();
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
}
