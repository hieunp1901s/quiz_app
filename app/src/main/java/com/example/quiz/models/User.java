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

    public User() {
    }

    public User(String name, String myTests, String joinTests) {
        this.name = name;
        this.myTests = myTests;
        this.joinTests = joinTests;
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
}
