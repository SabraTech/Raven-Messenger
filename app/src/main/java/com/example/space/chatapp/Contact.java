package com.example.space.chatapp;


public class Contact {
    private String name;
    private String lastMessage;

    // next features
    // sent, sent & delivered, sent & delivered & seen, or a message for me.
    private String lastMessageStatus;
    private String lastActiveTime;
    private String image_url;


    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
