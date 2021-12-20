package com.example.quiz.models;

public class Notification {
    String message;
    String time;

    public Notification() {};

    public Notification(String message, String time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
}
