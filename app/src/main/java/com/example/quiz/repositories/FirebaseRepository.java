package com.example.quiz.repositories;

import android.app.AlarmManager;
import android.app.Application;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;


import com.example.quiz.models.AlarmReceiver;
import com.example.quiz.models.Answer;
import com.example.quiz.models.ChatRoom;
import com.example.quiz.models.FirebaseService;

import com.example.quiz.models.Message;
import com.example.quiz.models.Test;
import com.example.quiz.models.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirebaseRepository {
    private Application application;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private MutableLiveData<User> userInfo;
    private MutableLiveData<Test> findTestResult;
    private MutableLiveData<Boolean> isFindTestResultNull;

    //retrofit
    Retrofit retrofit;
    FirebaseService service;

    //for navigate between login fragment and home fragment
    private MutableLiveData<Integer> logInState;

    //for progress dialog fragment
    private MutableLiveData<Boolean> progressing;

    //setup for realtime listener
    DatabaseReference databaseReference;
    ValueEventListener joinTestRepoListener;
    ValueEventListener myTestRepoListener;
    ValueEventListener singleJoinTestListener;
    ValueEventListener singleMyTestListener;

    //save testID for later use
    ArrayList<String> listOfJoinTestID;
    ArrayList<String> listOfMyTestID;

    //save test for adapter
    private ArrayList<Test> listMyTest;
    private ArrayList<Test> listJoinTest;


    //if value change, notify adapter to update dataset
    MutableLiveData<Integer> updateMyTest;
    MutableLiveData<Integer> updateJoinTest;

    MutableLiveData<Integer> checkAnswerSubmitted;

    ArrayList<Answer> listAnswer;
    MutableLiveData<Boolean> finishGetResult;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Integer requestCodeCount;

    //chatroom listener
    ValueEventListener chatRoomListener;
    MutableLiveData<Integer> notification;
    ArrayList<String> newNotification;

    //currentroom listener
    ChildEventListener currentChatRoomListener;
    ArrayList<Message> messages;
    MutableLiveData<String> currentChatRoomID;
    MutableLiveData<Integer> updateChatRoom;
    long messageCount = 0;
    ArrayList<ChatRoom> listChatRoom;

    public FirebaseRepository(Application application) {
        this.application = application;
        initData();
        initValueEventListener();
        initRetrofit();
        initFirebaseAuth();
    }

    public void register(String userName, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(application.getMainExecutor(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = firebaseAuth.getCurrentUser();
                            DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                            String myTests = root.child("lists").push().getKey();
                            String joinTests = root.child("lists").push().getKey();
                            User newUser = new User(userName, myTests, joinTests);
                            addUserFirebase(user.getUid(), newUser);
                            userInfo.setValue(newUser);
                            logInState.postValue(1);
                            progressing.setValue(false);
                        }
                        else {
                            progressing.setValue(false);
                            Toast.makeText(application.getApplicationContext(), "Registration Failure" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(application.getMainExecutor(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = firebaseAuth.getCurrentUser();
                            logInState.postValue(1);
                            progressing.setValue(false);
                        }
                        else {
                            progressing.setValue(false);
                            Toast.makeText(application.getApplicationContext(), "Login Failure: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void changePassword(String newPassWord) {
        user.updatePassword(newPassWord).addOnCompleteListener(application.getMainExecutor(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
                else {
                    Toast.makeText(application.getApplicationContext(), "Change Password Failure: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void logOut() {
        removeValueListenerToRepositories();
        removeAllListener(0);
        removeAllListener(1);
        listJoinTest.clear();
        listMyTest.clear();
        firebaseAuth.signOut();
        userInfo.postValue(null);
    }

    private void initData() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPref = application.getSharedPreferences("com.example.quiz", Context.MODE_PRIVATE);
        editor = sharedPref.edit();


        listMyTest = new ArrayList<>();
        listJoinTest = new ArrayList<>();
        updateJoinTest = new MutableLiveData<>();
        updateMyTest = new MutableLiveData<>();
        updateMyTest.postValue(0);
        updateJoinTest.postValue(0);
        this.findTestResult = new MutableLiveData<>();
        this.progressing = new MutableLiveData<>();
        this.userInfo = new MutableLiveData<>();
        this.logInState = new MutableLiveData<>();
        this.checkAnswerSubmitted = new MutableLiveData<>();
        checkAnswerSubmitted.setValue(3);
        listAnswer = new ArrayList<>();
        this.finishGetResult = new MutableLiveData<>();
        this.finishGetResult.setValue(false);
        this.listOfJoinTestID = new ArrayList<>();
        this.listOfMyTestID = new ArrayList<>();
        notification = new MutableLiveData<>();
        newNotification = new ArrayList<>();
        notification.postValue(0);
        messages = new ArrayList<>();
        currentChatRoomID = new MutableLiveData<>();
        updateChatRoom = new MutableLiveData<>();
        updateChatRoom.setValue(0);
        isFindTestResultNull = new MutableLiveData<>();
        listChatRoom = new ArrayList<>();
    }

    private void initValueEventListener() {
        joinTestRepoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //when onDataChange clear listJoinTest and remove all listener
                listJoinTest.clear();
                updateJoinTest.postValue(updateJoinTest.getValue() + 1);
                removeAllListener(0);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    listOfJoinTestID.add(dataSnapshot.getKey());
                }

                //add all listener again
                addAllListener(0);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        myTestRepoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listMyTest.clear();
                updateMyTest.postValue(updateJoinTest.getValue() + 1);
                removeAllListener(1);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    listOfMyTestID.add(dataSnapshot.getKey());
                }

                //add all listener again
                addAllListener(1);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        singleJoinTestListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //check if test exists or not
                if (snapshot.exists()) {
                    //remove test from listJoinTest if it already exists
                    for (int i = 0; i  < listJoinTest.size(); i++) {
                        if (listJoinTest.get(i).getTestID().equals(snapshot.getKey())) {
                            listJoinTest.remove(i);
                            break;
                        }
                    }

                    //add to list chatroom
                    listChatRoom.add(new ChatRoom(snapshot.getValue(Test.class)));
//                    Boolean check = false;
//                    for (int i = 0; i < listChatRoom.size(); i++) {
//                        if (listChatRoom.get(i).getId().equals(snapshot.getValue(Test.class).getTestID())) {
//                            check = true;
//                            break;
//                        }
//
//                    }
//
//                    if (!check)
//                        listChatRoom.add(new ChatRoom(snapshot.getValue(Test.class)));
                    //add test to listJoinTest
                    listJoinTest.add(snapshot.getValue(Test.class));
                    updateJoinTest.postValue(updateJoinTest.getValue() + 1);

                    //
                    addAlarm(snapshot.getValue(Test.class));
                }
                //if test was removed, remove it from repo + remove this listener
                else {
                    databaseReference.child("repositories").child(userInfo.getValue().getJoinTests()).child(snapshot.getKey()).removeValue();
                    removeAlarm(snapshot.getKey());
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        singleMyTestListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //check if test exists or not
                if (snapshot.exists()) {
                    //remove test from listMyTest if it already exists
                    for (int i = 0; i < listMyTest.size(); i++) {
                        if (listMyTest.get(i).getTestID().equals(snapshot.getKey())) {
                            listMyTest.remove(i);
                            break;
                        }
                    }

                    listChatRoom.add(new ChatRoom(snapshot.getValue(Test.class)));

//                    Boolean check = false;
//                    for (int i = 0; i < listChatRoom.size(); i++) {
//                        if (listChatRoom.get(i).getId().equals(snapshot.getValue(Test.class).getTestID())) {
//                            check = true;
//                            break;
//                        }
//
//                    }
//
//                    if (!check)
//                        listChatRoom.add(new ChatRoom(snapshot.getValue(Test.class)));

                    //add test to listMyTest
                    listMyTest.add(snapshot.getValue(Test.class));
                    updateMyTest.postValue(updateJoinTest.getValue() + 1);

                }

                //if test was removed, remove it from repo + remove this listener
                else {
                    databaseReference.child("repositories").child(userInfo.getValue().getMyTests()).child(snapshot.getKey()).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        chatRoomListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String sharedPrefKey = snapshot.getKey() + ".";
                long count = sharedPref.getLong(sharedPrefKey, 0);
                if (snapshot.getChildrenCount() > count) {
                    addNewNotification(snapshot.getKey());

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        currentChatRoomListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (snapshot.getValue() instanceof String) {

                }
                else {
                    messages.add(snapshot.getValue(Message.class));
                    messageCount++;
                    updateChatRoom.setValue(updateChatRoom.getValue() + 1);
                }

            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://quiz-5bb63-default-rtdb.firebaseio.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(FirebaseService.class);//async
    }

    private void initFirebaseAuth() {
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    user = firebaseAuth.getCurrentUser();

                    Boolean loop = true;
                    while (loop) {// wait for retrofit service build complete
                        if (service != null) {
                            if (user != null && userInfo.getValue() == null) {
                                getUserInfoFirebase();
                            }
                            loop = false;
                        }
                    }

                    logInState.postValue(1);
                }
                else {
                    logInState.postValue(0);
                }
            }
        };

        //Init and attach
        this.firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void addValueListenerToRepositories() {
        //Add value listener to the node
        databaseReference.child("repositories").child(userInfo.getValue().getJoinTests()).addValueEventListener(joinTestRepoListener);
        databaseReference.child("repositories").child(userInfo.getValue().getMyTests()).addValueEventListener(myTestRepoListener);
    }

    private void removeValueListenerToRepositories() {
        databaseReference.child("repositories").child(userInfo.getValue().getJoinTests()).removeEventListener(joinTestRepoListener);
        databaseReference.child("repositories").child(userInfo.getValue().getMyTests()).removeEventListener(myTestRepoListener);
    }

    private void addAllListener(int flag) {
        if (flag == 0) {
            for (int i = 0; i < listOfJoinTestID.size(); i++) {
                databaseReference.child("tests").child(listOfJoinTestID.get(i)).addValueEventListener(singleJoinTestListener);
                databaseReference.child("chatroom").child(listOfJoinTestID.get(i)).addValueEventListener(chatRoomListener);
            }
        }
        else if (flag == 1){
            for (int i = 0; i < listOfMyTestID.size(); i++) {
                databaseReference.child("tests").child(listOfMyTestID.get(i)).addValueEventListener(singleMyTestListener);
                databaseReference.child("chatroom").child(listOfMyTestID.get(i)).addValueEventListener(chatRoomListener);
            }
        }
    }

    private void removeAllListener(int flag) {
        if (flag == 0) {
            for (int i = 0; i < listOfJoinTestID.size(); i++) {
                databaseReference.child("tests").child(listOfJoinTestID.get(i)).removeEventListener(singleJoinTestListener);
                databaseReference.child("chatroom").child(listOfJoinTestID.get(i)).removeEventListener(chatRoomListener);
            }

            listOfJoinTestID.clear();
        }
        else if (flag == 1){
            for (int i = 0; i < listOfMyTestID.size(); i++) {
                databaseReference.child("tests").child(listOfMyTestID.get(i)).removeEventListener(singleMyTestListener);
                databaseReference.child("chatroom").child(listOfMyTestID.get(i)).removeEventListener(chatRoomListener);
            }

            listOfMyTestID.clear();

        }
    }

    public void getUserInfoFirebase() {
        Call<User> call = service.getUser(user.getUid());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body()!= null) {
                    userInfo.setValue(response.body());
                    addValueListenerToRepositories();
                }
                else {
                    new CountDownTimer(5000, 5000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            getUserInfoFirebase();
                        }
                    }.start();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                new CountDownTimer(5000, 5000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        getUserInfoFirebase();
                    }
                }.start();
            }
        });
    }

    private void addUserFirebase(String userID, User user) {
        Call call = service.createUser(userID, user);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }

    public void addTestFirebase(Test test) {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        String testID = root.child("tests").push().getKey();
        test.setTestID(testID);
        Call call = service.addTest(testID, test);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                addTestToRepoFirebase(testID, "myTests");
                createChatRoomFirebase(test.getTestID(), user.getUid());
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

    }

    public void addTestToRepoFirebase(String testID, String repoType) {
        Call call;
        if (repoType.equals("myTests"))
            call = service.addTestToRepository(userInfo.getValue().getMyTests(), testID, "1");
        else
            call = service.addTestToRepository(userInfo.getValue().getJoinTests(), testID, "1");

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private void createChatRoomFirebase(String testID, String adminID) {
        Call call;
        call = service.createChatRoom(testID, "ADMIN", adminID);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("creating chat room success", "onResponse: ");
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    public void findTestFromFirebase(String id) {
        Call call = service.getTestFromFirebase(id);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if ((Test) response.body() != null)
                    findTestResult.postValue((Test) response.body());
                else
                    isFindTestResultNull.setValue(true);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                isFindTestResultNull.setValue(false);
            }
        });
    }

    public void submitAnswerToRepoFirebase(Answer answer, String testID) {
        Call call = service.checkIfAnswerSubmitted(testID, user.getUid());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if ((Answer) response.body() == null) {
                    Call call1 = service.submitAnswer(testID, user.getUid(), answer);
                    call1.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {

                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });
                }
                else {
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }

    public void checkIfAnswerSubmitted(String testID) {
        Call call = service.checkIfAnswerSubmitted(testID, user.getUid());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if ((Answer) response.body() == null) {
                    checkAnswerSubmitted.postValue(0);
                }
                else {
                    checkAnswerSubmitted.postValue(1);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    public void getTestResult(String testID) {
        finishGetResult.setValue(false);
        listAnswer.clear();
        databaseReference.child("answers").child(testID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    listAnswer.add(dataSnapshot.getValue(Answer.class));
                }
                finishGetResult.postValue(true);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void deleteTest(String testID) {
        databaseReference.child("tests").child(testID).removeValue();
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

    public MutableLiveData<User> getUserInfo() {return userInfo;}

    public FirebaseUser getUser() {return user;}

    public MutableLiveData<Integer> getLogInState() {return logInState;};

    public MutableLiveData<Test> getFindTestResult() {
        return findTestResult;
    }

    public MutableLiveData<Boolean> getProgressing() {return progressing;}

    public MutableLiveData<Integer> getCheckAnswerSubmitted() {return checkAnswerSubmitted;}

    public ArrayList<Answer> getListAnswer() {
        return listAnswer;
    }

    public MutableLiveData<Boolean> getFinishGetResult() {
        return finishGetResult;
    }
    
    public MutableLiveData<String> getCurrentChatRoomID() {return currentChatRoomID;}

    public ArrayList<Message> getMessages() {return messages;}

    public MutableLiveData<Integer> getUpdateChatRoom() {return updateChatRoom;}

    public MutableLiveData<Boolean> getIsFindTestResultNull() {return isFindTestResultNull;}

    public MutableLiveData<Integer> getNotification() {return notification;}

    public ArrayList<String> getNewNotification() {return newNotification;}

    public ArrayList<ChatRoom> getListChatRoom() {return listChatRoom;}


    private synchronized void addAlarm(Test test) {
        requestCodeCount = sharedPref.getInt("request code", 0);
        int requestCode = sharedPref.getInt(test.getTestID(), -1);
        //if not exist
        if (requestCode == -1) {
            requestCode = requestCodeCount;
            editor.putInt(test.getTestID(), requestCodeCount);
            editor.putInt("request code", requestCodeCount + 1);
            editor.commit();
        }
        AlarmManager alarmMgr = (AlarmManager)application.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(application, AlarmReceiver.class);
        intent.putExtra("test name", test.getTestName());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(application, requestCode, intent, 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String date = test.getDate() + " " + test.getStartTime() + ":00";

        try {
            calendar.setTime(simpleDateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (new Date().before(calendar.getTime()))
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }

    private void removeAlarm(String testID) {
        int requestCode = sharedPref.getInt(testID, -1);
        AlarmManager alarmMgr = (AlarmManager)application.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(application, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(application, requestCode, intent, 0);
        alarmMgr.cancel(alarmIntent);
    }

    private synchronized void addNewNotification(String testID) {
        Boolean check = false;
        for (int i = 0; i < newNotification.size(); i++) {
            if (newNotification.get(i).equals(testID)) {
                check = true;
                break;
            }

        }
        if (check == false) {
            newNotification.add(testID);
            notification.setValue(notification.getValue() + 1);
        }
    }

    public void removeNewNotification(String testID) {
        for (int i = 0; i < newNotification.size(); i++) {
            if (newNotification.get(i).equals(testID)) {
                newNotification.remove(i);
                notification.setValue(notification.getValue() - 1);
                break;
            }
        }
    }

    public void sendMessage(String testID, Message message) {
        Call call = service.sendMessage(testID, message);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    public void initCurrentChatRoom(String testID) {
//        messages.clear();
        databaseReference.child("chatroom").child(testID).addChildEventListener(currentChatRoomListener);
        currentChatRoomID.setValue(testID);
    }

    public void removeCurrentChatRoomListener(String testID) {
        messages.clear();
        databaseReference.child("chatroom").child(testID).removeEventListener(currentChatRoomListener);
    }
}
