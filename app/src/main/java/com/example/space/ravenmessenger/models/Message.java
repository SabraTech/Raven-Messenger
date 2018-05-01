package com.example.space.ravenmessenger.models;


public class Message {
    public static final long TEXT = 0, IMAGE = 1;
    public String idSender;
    public String idReceiver;
    public String idReceiverRoom;
    public String text;
    public long type;
    public long timestamp;
}
