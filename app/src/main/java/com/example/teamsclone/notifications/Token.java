package com.example.teamsclone.notifications;

public class  Token {

    //registration token - app ID which allows it to receive Push-Notifications

    String token;

    public Token(String token) {
        this.token = token;
    }

    public Token(){

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
