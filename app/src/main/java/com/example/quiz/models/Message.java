package com.example.quiz.models;

public class Message {
    private String user;
    private String time;
    private String message;
    private String userId;

    public Message() {
    }

    public Message(String user, String time, String message, String userId) {
        this.user = user;
        this.time = time;
        this.message = message;
        this.userId = userId;
    }

    public String getUser() {
        if (user == null)
            return "";
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        if (message == null)
            return "";
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }
}
