package com.example.quiz.models;

import java.io.Serializable;
import java.util.ArrayList;

public class CacheData implements Serializable {
    ArrayList<String> answerOrder;
    ArrayList<Integer> questionOrder;
    ArrayList<String> listAnswer;
    String testID;
    public CacheData() {

        this.testID = "";
    }

    public ArrayList<String> getAnswerOrder() {
        return answerOrder;
    }

    public void setAnswerOrder(ArrayList<String> answerOrder) {
        this.answerOrder = answerOrder;
    }

    public ArrayList<Integer> getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(ArrayList<Integer> questionOrder) {
        this.questionOrder = questionOrder;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public ArrayList<String> getListAnswer() {
        return listAnswer;
    }

    public void setListAnswer(ArrayList<String> listAnswer) {
        this.listAnswer = listAnswer;
    }
}
