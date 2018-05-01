package com.example.space.ravenmessenger.models;

public class Request {
    private String name, status, avatar, uid;

    public Request() {

    }

    public Request(String name, String status, String avatar, String uid) {
        this.name = name;
        this.status = status;
        this.avatar = avatar;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}