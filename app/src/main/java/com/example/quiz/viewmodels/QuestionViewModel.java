package com.example.quiz.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quiz.models.Answer;
import com.example.quiz.models.Test;

public class QuestionViewModel extends ViewModel {
    private MutableLiveData<Test> test;
    private MutableLiveData<Long> timer;
    private MutableLiveData<Integer> navIndex;
    private MutableLiveData<String> participantName;

    public QuestionViewModel() {
        test = new MutableLiveData<>();
        timer = new MutableLiveData<>();
        navIndex = new MutableLiveData<>();
        participantName = new MutableLiveData<>();
    }

    public MutableLiveData<Test> getTest() {
        return test;
    }

    public MutableLiveData<Long> getTimer() {
        return timer;
    }

    public MutableLiveData<Integer> getNavIndex() {
        return navIndex;
    }

    public MutableLiveData<String> getParticipantName() {return participantName;}

}
