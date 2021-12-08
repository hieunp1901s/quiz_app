package com.example.quiz.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.quiz.models.Answer;
import com.example.quiz.models.ChatRoom;
import com.example.quiz.models.Message;
import com.example.quiz.models.Notify;
import com.example.quiz.models.Test;
import com.example.quiz.models.User;
import com.example.quiz.repositories.FirebaseRepository;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FirebaseViewModel extends AndroidViewModel {
    private final FirebaseRepository firebaseRepository;
    private MutableLiveData<Test> selectedMyTest;

    public FirebaseViewModel(@NonNull @NotNull Application application) {
        super(application);
        firebaseRepository = new FirebaseRepository(application);
        selectedMyTest = new MutableLiveData<>();
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

    public FirebaseUser getUser() {return firebaseRepository.getUser();}

    public MutableLiveData<User> getUserInfo() {return firebaseRepository.getUserInfo();}

    public MutableLiveData<Integer> getLogInState() {return firebaseRepository.getLogInState();}

    public MutableLiveData<Test> getFindTestResult() {return firebaseRepository.getFindTestResult();}

    public void addTestFirebase(Test test) {
        firebaseRepository.addTestFirebase(test);
    }

    public MutableLiveData<Boolean> getProgressing() {return firebaseRepository.getProgressing();}

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
        return firebaseRepository.getListJoinTest();
    }

    public MutableLiveData<Notify> getNotifyJoinTestDataChanged() {
        return firebaseRepository.getNotifyJoinTestDataChanged();
    }

    public ArrayList<Test> getListMyTest() {
        return firebaseRepository.getListMyTest();
    }

    public MutableLiveData<Notify> getNotifyMyTestDataChanged() {
        return firebaseRepository.getNotifyMyTestDataChanged();
    }

    public MutableLiveData<Integer> getCheckAnswerSubmitted() {return firebaseRepository.getCheckAnswerSubmitted();}

    public MutableLiveData<ChatRoom> getCurrentChatRoom() {return firebaseRepository.getCurrentChatRoom();}

    public void checkIfAnswerSubmitted(String testID) {
        firebaseRepository.checkIfAnswerSubmitted(testID);
    }

    public ArrayList<Answer> getListAnswer() {
        return firebaseRepository.getListAnswer();
    }

    public MutableLiveData<Boolean> getFinishGetResult() {
        return firebaseRepository.getFinishGetResult();
    }

    public void getTestResult(String testID) {
        firebaseRepository.getTestResult(testID);
    }

    public void deleteTest(String testID) {firebaseRepository.deleteTest(testID);}

    public void sendMessage(String testID, Message message) {
        firebaseRepository.sendMessage(testID, message);
    }

    public ArrayList<Message> getMessages() {return firebaseRepository.getMessages();}

    public void initCurrentChatRoom(String testID) {
        firebaseRepository.initCurrentChatRoom(testID);
    }

    public void removeCurrentChatRoomListener(String testID) {
        firebaseRepository.removeCurrentChatRoomListener(testID);
    }

    public MutableLiveData<Notify> getNotifyChatRoomDataChanged() {return firebaseRepository.getNotifyChatRoomDataChanged();}

    public MutableLiveData<Boolean> getIsFindTestResultNull() {return firebaseRepository.getIsFindTestResultNull();}

    public MutableLiveData<Notify> getNotifyListChatRoomDataChanged() {return firebaseRepository.getNotifyListChatRoomDataChanged();}

    public void removeNewNotification(String testID) {firebaseRepository.removeNewNotification(testID);}

    public ArrayList<ChatRoom> getListChatRoom() {return firebaseRepository.getListChatRoom();}

    public ArrayList<String> getNewNotification() {return firebaseRepository.getNewNotification();}

    public MutableLiveData<Test> getSelectedMyTest() {return selectedMyTest;}

    public void manageTest(Test test) {firebaseRepository.manageTest(test);}

}
