package com.example.quiz.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Answer {
    private String score;
    private String participantName;
    private ArrayList<String> listAnswer;
    private String timeFinish;
    private String duration;
    public Answer(){};

    public void setScore(String score) {
        this.score = score;
    }

    public void setParticipant(String participantName) {
        this.participantName = participantName;
    }

    public void setListAnswer(ArrayList<String> listAnswer) {
        this.listAnswer = listAnswer;
    }

    public void setTimeFinish(String timeFinish) {this.timeFinish = timeFinish;}

    public String getScore() {
        return score;
    }

    public String getParticipantName() {
        return participantName;
    }

    public ArrayList<String> getListAnswer() {
        return listAnswer;
    }

    public String getTimeFinish() {
        return timeFinish;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
