package com.example.teamsclone.models;

public class Comments
{



    public String comment,date,time,username,cid;

    public Comments()
    {

    }

    public Comments(String comment,String cid, String date, String time, String username) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.cid=cid;
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}