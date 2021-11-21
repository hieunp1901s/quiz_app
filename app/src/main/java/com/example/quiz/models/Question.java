package com.example.quiz.models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;


@IgnoreExtraProperties
public class Question {
    @SerializedName("question")
    String question;
    @SerializedName("answer_1")
    String answer_1;
    @SerializedName("answer_2")
    String answer_2;
    @SerializedName("answer_3")
    String answer_3;
    @SerializedName("answer_4")
    String answer_4;
    @SerializedName("correctAnswer")
    String correctAnswer;

    public Question(){}

    public Question(String question, String answer_1, String answer_2, String answer_3, String answer_4, String correctAnswer) {
        this.question = question;
        this.answer_1 = answer_1;
        this.answer_2 = answer_2;
        this.answer_3 = answer_3;
        this.answer_4 = answer_4;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer_1() {
        return answer_1;
    }

    public String getAnswer_2() {
        return answer_2;
    }

    public String getAnswer_3() {
        return answer_3;
    }

    public String getAnswer_4() {
        return answer_4;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
