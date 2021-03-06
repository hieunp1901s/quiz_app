package com.example.quiz.models;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Test {
    private ArrayList<Question> listQuestion;
    private ArrayList<String> studentEmailList;
    String testName;
    String startTime;
    String duration;
    String date;
    String testID;
    String mix;
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

    public void setMix(String mix) {
        this.mix = mix;
    }

    public String getMix() {return this.mix;}

    public void setStudentEmailList(ArrayList<String> studentEmailList) {
        this.studentEmailList = studentEmailList;
    }

    public ArrayList<String> getStudentEmailList() {
        return studentEmailList;
    }

    public void setListQuestion(ArrayList<Question> listQuestion) {
        this.listQuestion = listQuestion;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        Test object = (Test) obj;
        if (this.getTestID().equals(object.getTestID()) && this.getTestName().equals(object.getTestName())
            && this.getStartTime().equals(object.getStartTime())
            && this.getMix().equals(object.getMix())
            && this.getDate().equals(object.getDate())
            && this.getDuration().equals(object.getDuration()))
            return true;
        return false;
    }


}
