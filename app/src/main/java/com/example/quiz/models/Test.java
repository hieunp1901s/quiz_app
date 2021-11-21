package com.example.quiz.models;

import android.util.Log;

import com.example.quiz.models.Question;

import java.util.ArrayList;

public class Test {
    private ArrayList<Question> listQuestion;
    String testName;
    String startTime;
    String duration;
    String date;
    String testID;
    int count = 0;

    public Test(){
        listQuestion = new ArrayList<>();
    }

    public void addQuestion(Question question) {
        listQuestion.add(question);
        count ++;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public ArrayList<Question> getListQuestion() {
        return listQuestion;
    }
}
