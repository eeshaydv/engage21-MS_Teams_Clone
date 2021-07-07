package com.example.teamsclone.models;

public class Friends {
    public String name, uid;

    public Friends() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Friends(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
