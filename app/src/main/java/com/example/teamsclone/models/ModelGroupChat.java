package com.example.teamsclone.models;

public class ModelGroupChat {
    String message,sender,type,time,date;

    public ModelGroupChat(){

    }

    public ModelGroupChat(String message, String sender,String time,String date, String type) {
        this.message = message;
        this.sender = sender;
        this.time=time;
        this.date=date;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
