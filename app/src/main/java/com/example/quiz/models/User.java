package com.example.quiz.models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

@IgnoreExtraProperties
public class User {
    @SerializedName("name")
    public String name;
    @SerializedName("myTests")
    public String myTests;
    @SerializedName("joinTests")
    public String joinTests;
    @SerializedName("email")
    public String email;

    public User() {
    }

    public User(String name, String myTests, String joinTests, String email) {
        this.name = name;
        this.myTests = myTests;
        this.joinTests = joinTests;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getMyTests() {
        return myTests;
    }

    public String getJoinTests() {
        return joinTests;
    }

    public String getEmail() {
        return email;
    }
}
