package com.example.quiz.models;

public class ChatRoom {
    String id;
    String name;
    Message lastMessage;

    public ChatRoom(Test test) {
        this.id = test.getTestID();
        this.name = test.getTestName();
    }

    public ChatRoom(String name, String ID) {
        this.name = name;
        this.id = id;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
