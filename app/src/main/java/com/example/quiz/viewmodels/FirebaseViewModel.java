package com.example.quiz.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.quiz.models.Answer;
import com.example.quiz.models.Test;
import com.example.quiz.models.User;
import com.example.quiz.repositories.FirebaseRepository;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FirebaseViewModel extends AndroidViewModel {
    private FirebaseRepository firebaseRepository;
    private FirebaseUser user;
    private MutableLiveData<User> userInfo;
    private MutableLiveData<Integer> logInState;
    private MutableLiveData<Test> findTestResult;
    private MutableLiveData<Boolean> progressing;

    private ArrayList<Test> listJoinTest;
    private MutableLiveData<Integer> updateJoinTest;

    private ArrayList<Test> listMyTest;
    private MutableLiveData<Integer> updateMyTest;

    private MutableLiveData<Integer> checkAnswerSubmitted;

    public FirebaseViewModel(@NonNull @NotNull Application application) {
        super(application);

        firebaseRepository = new FirebaseRepository(application);
        user = firebaseRepository.getUser();
        userInfo = firebaseRepository.getUserInfo();
        findTestResult = firebaseRepository.getFindTestResult();
        logInState = firebaseRepository.getLogInState();
        progressing = firebaseRepository.getProgressing();

        listJoinTest = firebaseRepository.getListJoinTest();
        updateJoinTest = firebaseRepository.getUpdateJoinTest();

        listMyTest = firebaseRepository.getListMyTest();
        updateMyTest = firebaseRepository.getUpdateMyTest();

        checkAnswerSubmitted = firebaseRepository.getCheckAnswerSubmitted();
    }

    public void login(String email, String password) {
        firebaseRepository.login(email, password);
    }

    public void register(String userName, String email, String password) {
        firebaseRepository.register(userName, email, password);
    }
    public void logOut() {
        firebaseRepository.logOut();
    }

    public FirebaseUser getUser() {return user;}

    public MutableLiveData<User> getUserInfo() {return userInfo;}

    public MutableLiveData<Integer> getLogInState() {return logInState;}

    public MutableLiveData<Test> getFindTestResult() {return findTestResult;}

    public void addTestFirebase(Test test) {
        firebaseRepository.addTestFirebase(test);
    }

    public MutableLiveData<Boolean> getProgressing() {return progressing;}




    public void findTestFromFirebase(String id) {
        firebaseRepository.findTestFromFirebase(id);
    }

    public void addTestToRepoFirebase(String id, String repoType) {
        firebaseRepository.addTestToRepoFirebase(id, repoType);
    }

    public void submitAnswerToRepoFirebase(Answer answer, String testID) {
        firebaseRepository.submitAnswerToRepoFirebase(answer, testID);
    }



    public ArrayList<Test> getListJoinTest() {
        return listJoinTest;
    }

    public MutableLiveData<Integer> getUpdateJoinTest() {
        return updateJoinTest;
    }

    public ArrayList<Test> getListMyTest() {
        return listMyTest;
    }

    public MutableLiveData<Integer> getUpdateMyTest() {
        return updateMyTest;
    }

    public MutableLiveData<Integer> getCheckAnswerSubmitted() {return  checkAnswerSubmitted;}

    public void checkIfAnswerSubmitted(String testID) {
        firebaseRepository.checkIfAnswerSubmitted(testID);
    }
}
