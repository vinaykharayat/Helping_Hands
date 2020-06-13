package com.helpinghandsorg.helpinghands.repositories;

public class ChatModel {

    private String sender;
    private String message;
    private String receiver;

    public ChatModel(String sender, String message, String receiver) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
    }

    public ChatModel() {
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
