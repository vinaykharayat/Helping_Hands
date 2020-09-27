package com.helpinghandsorg.helpinghands.repositories;

public class ChatModel {

    private String sender;
    private String message;
    private String receiver;
    private String time;

    public ChatModel(String sender, String message, String receiver, String time) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.time = time;
    }

    public ChatModel() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReciever(String reciever) {
        this.receiver = reciever;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
