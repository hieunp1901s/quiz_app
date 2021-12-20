package com.example.quiz.repositories;

import android.annotation.SuppressLint;
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
import com.example.quiz.interfaces.FirebaseService;
import com.example.quiz.models.Message;
import com.example.quiz.models.Notification;
import com.example.quiz.models.Test;
import com.example.quiz.models.User;
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
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirebaseRepository {
    private final Application application;

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
    private static final int LOG_IN_STATE_LOG_IN = 1;
    private static final int LOG_IN_STATE_LOG_OUT = 0;
    private static final int ADD_JOINED_TEST_REPO = 477;
    private static final int ADD_MY_TEST_REPO = 623;

    //for progress dialog fragment
    private MutableLiveData<Boolean> progressing;

    //setup for realtime listener
    DatabaseReference databaseReference;
    ValueEventListener joinedTestRepoListener;
    ValueEventListener myTestRepoListener;
    ValueEventListener joinedTestListener;
    ValueEventListener myTestListener;

    //save testID for later use
    ArrayList<String> joinedTestIdList;
    ArrayList<String> myTestIdList;

    private static final int REMOVE_JOINED_TEST = 526;
    private static final int REMOVE_MY_TEST = 251;

    //save test for adapter
    private MutableLiveData<ArrayList<Test>> myTestList;
    private MutableLiveData<ArrayList<Test>> joinedTestList;


    MutableLiveData<Integer> checkAnswerSubmitted;

    ArrayList<Answer> answerList;
    MutableLiveData<Boolean> finishGetResult;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Integer requestCodeCount;

    //chatroom listener
    ValueEventListener chatRoomListener;
    MutableLiveData<ArrayList<String>> newNotification;

    //current room listener
    ChildEventListener currentChatRoomListener;
    MutableLiveData<ArrayList<Message>> messages;
    MutableLiveData<ChatRoom> currentChatRoom;
    long messageCount = 0;
    MutableLiveData<ArrayList<ChatRoom>> chatRoomList;

    //log
    MutableLiveData<ArrayList<Notification>> scoreLog;
    MutableLiveData<ArrayList<Notification>> normalLog;
    ValueEventListener scoreLogListener;
    ValueEventListener normalLogListener;
    final String ADMIN = "hieunp62@wru.vn";
    MutableLiveData<Integer> checkAdmin;

    public FirebaseRepository(Application application) {
        this.application = application;
        initData();
        initValueEventListener();
        initRetrofit();
        initFirebaseAuth();
    }

    public void register(String userName, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(application.getMainExecutor(), task -> {
                    if (task.isSuccessful()) {
                        user = firebaseAuth.getCurrentUser();
                        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                        String myTest = root.child("lists").push().getKey();
                        String joinedTest = root.child("lists").push().getKey();
                        User newUser = new User(userName, myTest, joinedTest, email);
                        addUserFirebase(user.getUid(), newUser);
                        getUserInfoFirebase();
                        userInfo.setValue(newUser);
                        logInState.setValue(LOG_IN_STATE_LOG_IN);
                        progressing.setValue(false);
                    }
                    else {
                        progressing.setValue(false);
                        Toast.makeText(application.getApplicationContext(), "Registration Failure" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(application.getMainExecutor(), task -> {
                    if (task.isSuccessful()) {
                        user = firebaseAuth.getCurrentUser();
                        getUserInfoFirebase();
                        logInState.setValue(LOG_IN_STATE_LOG_IN);
                        progressing.setValue(false);
                        if (email.equals(ADMIN))
                            checkAdmin.setValue(1);
                        else
                            checkAdmin.setValue(0);
                    }
                    else {
                        progressing.setValue(false);
                        Toast.makeText(application.getApplicationContext(), "Login Failure: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    public void changePassword(String newPassWord) {
//        user.updatePassword(newPassWord).addOnCompleteListener(application.getMainExecutor(), new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull @NotNull Task<Void> task) {
//                if (task.isSuccessful()) {
//
//                }
//                else {
//                    Toast.makeText(application.getApplicationContext(), "Change Password Failure: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    public void logOut() {
        removeValueListenerToRepositories();
        removeAllListeners(REMOVE_JOINED_TEST);
        removeAllListeners(REMOVE_MY_TEST);
        Objects.requireNonNull(joinedTestList.getValue()).clear();
        newNotification.getValue().clear();
        firebaseAuth.signOut();
        logInState.setValue(LOG_IN_STATE_LOG_OUT);
        userInfo.postValue(null);
        normalLog.getValue().clear();
        scoreLog.getValue().clear();
        checkAdmin.setValue(2);
    }

    @SuppressLint("CommitPrefEdits")
    private void initData() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPref = application.getSharedPreferences("com.example.quiz", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        myTestList = new MutableLiveData<>();
        myTestList.setValue(new ArrayList<>());
        joinedTestList = new MutableLiveData<>();
        joinedTestList.setValue(new ArrayList<>());
        findTestResult = new MutableLiveData<>();
        progressing = new MutableLiveData<>();
        userInfo = new MutableLiveData<>();
        logInState = new MutableLiveData<>();
        checkAnswerSubmitted = new MutableLiveData<>();
        checkAnswerSubmitted.setValue(3);
        answerList = new ArrayList<>();
        finishGetResult = new MutableLiveData<>();
        finishGetResult.setValue(false);
        joinedTestIdList = new ArrayList<>();
        myTestIdList = new ArrayList<>();
        newNotification = new MutableLiveData<>();
        newNotification.setValue(new ArrayList<>());
        messages = new MutableLiveData<>();
        messages.setValue(new ArrayList<>());
        isFindTestResultNull = new MutableLiveData<>();
        chatRoomList = new MutableLiveData<>();
        chatRoomList.setValue(new ArrayList<>());
        currentChatRoom = new MutableLiveData<>();
        scoreLog = new MutableLiveData<>();
        scoreLog.setValue(new ArrayList<>());
        normalLog = new MutableLiveData<>();
        normalLog.setValue(new ArrayList<>());
        checkAdmin = new MutableLiveData<>();
        checkAdmin.setValue(2);
    }

    private void initValueEventListener() {
        joinedTestRepoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                removeAllListeners(REMOVE_JOINED_TEST);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    joinedTestIdList.add(dataSnapshot.getKey());
                }

                //add all listener again
                addAllListeners(ADD_JOINED_TEST_REPO);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        myTestRepoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                removeAllListeners(REMOVE_MY_TEST);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    myTestIdList.add(dataSnapshot.getKey());
                }

                //add all listener again
                addAllListeners(ADD_MY_TEST_REPO);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        joinedTestListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Test> list = new ArrayList<>(Objects.requireNonNull(joinedTestList.getValue()));
                //check if test exists or not
                if (snapshot.exists()) {
                    //add chat room to chat room list
                    ChatRoom chatRoom = new ChatRoom(snapshot.getValue(Test.class));
                    ArrayList<ChatRoom> tempList = new ArrayList<>(chatRoomList.getValue());
                    boolean check = false;
                    for (int i = 0; i < tempList.size(); i++) {

                        if (tempList.get(i).getId().equals(chatRoom.getId())) {
                            tempList.remove(i);
                            tempList.add(chatRoom);
                            check = true;
                            break;
                        }

                    }
                    if (!check)
                        tempList.add(chatRoom);

                    chatRoomList.setValue(tempList);

                    boolean replace = false;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getTestID().equals(snapshot.getKey())) {
                            list.remove(i);
                            list.add(i, snapshot.getValue(Test.class));
                            replace = true;
                            break;
                        }
                    }
                    if (!replace)
                        list.add(snapshot.getValue(Test.class));

                    //
                    addAlarm(Objects.requireNonNull(snapshot.getValue(Test.class)));
                }
                //if test was removed, remove it from repo + remove this listener
                else {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getTestID().equals(snapshot.getKey())) {
                            list.remove(i);
                            break;
                        }
                    }
                    databaseReference.child("repositories").child(Objects.requireNonNull(userInfo.getValue()).getJoinTests()).child(Objects.requireNonNull(snapshot.getKey())).removeValue();
                    removeAlarm(snapshot.getKey());
                }
                joinedTestList.setValue(list);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        myTestListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //check if test exists or not
                ArrayList<Test> list = new ArrayList<>(Objects.requireNonNull(myTestList.getValue()));

                if (snapshot.exists()) {
                    //add chat room to chat room list
                    ChatRoom chatRoom = new ChatRoom(snapshot.getValue(Test.class));
                    ArrayList<ChatRoom> tempList = new ArrayList<>(chatRoomList.getValue());
                    boolean check = false;
                    for (int i = 0; i < tempList.size(); i++) {

                        if (tempList.get(i).getId().equals(chatRoom.getId())) {
                            tempList.remove(i);
                            tempList.add(chatRoom);
                            check = true;
                            break;
                        }

                    }
                    if (!check)
                        tempList.add(chatRoom);

                    chatRoomList.setValue(tempList);


                    boolean replace = false;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getTestID().equals(snapshot.getKey())) {
                            list.remove(i);
                            list.add(i, snapshot.getValue(Test.class));
                            replace = true;
                            break;
                        }
                    }
                    if (!replace)
                        list.add(snapshot.getValue(Test.class));
                }

                //if test was removed, remove it from my test repo to trigger my test repo listener
                else {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getTestID().equals(snapshot.getKey())) {
                            list.remove(i);
                            break;
                        }
                    }
                    databaseReference.child("repositories").child(Objects.requireNonNull(userInfo.getValue()).getMyTests()).child(Objects.requireNonNull(snapshot.getKey())).removeValue();
                }

                myTestList.setValue(list);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        chatRoomListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (!Objects.equals(dataSnapshot.getKey(), "-ADMIN")) {
                        //check
                        String sharedPrefKey = snapshot.getKey() + ".";
                        String lastMessage = sharedPref.getString(sharedPrefKey, "");
                        if (!Objects.requireNonNull(dataSnapshot.getValue(Message.class)).getMessage().equals(lastMessage)) {
                            addNewNotification(snapshot.getKey());
                        }

                        ArrayList<ChatRoom> list = new ArrayList<>(chatRoomList.getValue());

                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getId().equals(snapshot.getKey())) {
                                list.get(i).setLastMessage(dataSnapshot.getValue(Message.class));
                                chatRoomList.setValue(list);
                                break;
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        currentChatRoomListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (!(snapshot.getValue() instanceof String)) {
                    ArrayList<Message> list = new ArrayList<>(messages.getValue());
                    list.add(snapshot.getValue(Message.class));
                    messageCount++;
                    messages.setValue(list);
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

        scoreLogListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<Notification> list = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        list.add(dataSnapshot.getValue(Notification.class));
                    }
                    scoreLog.setValue(list);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        normalLogListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<Notification> list = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        list.add(dataSnapshot.getValue(Notification.class));
                    }
                    normalLog.setValue(list);
                }
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
//        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
//            if (firebaseAuth.getCurrentUser() != null){
//                user = firebaseAuth.getCurrentUser();
//
//                boolean loop = true;
//                while (loop) {// wait for retrofit service build complete
//                    if (service != null) {
//                        if (user != null && userInfo.getValue() == null) {
//                            getUserInfoFirebase();
//                        }
//                        loop = false;
//                    }
//                }
//
//                logInState.postValue(1);
//            }
//            else {
//                logInState.postValue(0);
//            }
//        };

        //Init and attach
        this.firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }

    private void addValueListenerToRepositories() {
        //Add value listener to the node
        databaseReference.child("repositories").child(Objects.requireNonNull(userInfo.getValue()).getJoinTests()).addValueEventListener(joinedTestRepoListener);
        databaseReference.child("repositories").child(userInfo.getValue().getMyTests()).addValueEventListener(myTestRepoListener);
        databaseReference.child("log").child(user.getUid()).child("score").addValueEventListener(scoreLogListener);
        databaseReference.child("log").child(user.getUid()).child("log").addValueEventListener(normalLogListener);
    }

    private void removeValueListenerToRepositories() {
        databaseReference.child("repositories").child(Objects.requireNonNull(userInfo.getValue()).getJoinTests()).removeEventListener(joinedTestRepoListener);
        databaseReference.child("repositories").child(userInfo.getValue().getMyTests()).removeEventListener(myTestRepoListener);
        databaseReference.child("log").child(user.getUid()).child("score").removeEventListener(scoreLogListener);
        databaseReference.child("log").child(user.getUid()).child("log").removeEventListener(normalLogListener);
    }

    private void addAllListeners(int flag) {
        if (flag == ADD_JOINED_TEST_REPO) {
            for (int i = 0; i < joinedTestIdList.size(); i++) {
                databaseReference.child("tests").child(joinedTestIdList.get(i)).addValueEventListener(joinedTestListener);
                databaseReference.child("chatroom").child(joinedTestIdList.get(i)).limitToLast(1).addValueEventListener(chatRoomListener);
            }
        }
        else if (flag == ADD_MY_TEST_REPO){
            for (int i = 0; i < myTestIdList.size(); i++) {
                databaseReference.child("tests").child(myTestIdList.get(i)).addValueEventListener(myTestListener);
                databaseReference.child("chatroom").child(myTestIdList.get(i)).limitToLast(1).addValueEventListener(chatRoomListener);
            }
        }
    }

    private void removeAllListeners(int flag) {
        if (flag == REMOVE_JOINED_TEST) {
            for (int i = 0; i < joinedTestIdList.size(); i++) {
                databaseReference.child("tests").child(joinedTestIdList.get(i)).removeEventListener(joinedTestListener);
                databaseReference.child("chatroom").child(joinedTestIdList.get(i)).removeEventListener(chatRoomListener);
            }

            joinedTestIdList.clear();
        }
        else if (flag == REMOVE_MY_TEST){
            for (int i = 0; i < myTestIdList.size(); i++) {
                databaseReference.child("tests").child(myTestIdList.get(i)).removeEventListener(myTestListener);
                databaseReference.child("chatroom").child(myTestIdList.get(i)).removeEventListener(chatRoomListener);
            }

            myTestIdList.clear();

        }
    }

    public void getUserInfoFirebase() {
        Call<User> call = service.getUser(user.getUid());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
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
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
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
            public void onResponse(@NotNull Call call, @NotNull Response response) {
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {
                addUserFirebase(userID, user);

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
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                addTestToRepoFirebase(testID, "myTests");
                createChatRoomFirebase(test.getTestID(), user.getUid());
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {

            }
        });

    }

    public void addTestToRepoFirebase(String testID, String repoType) {
        Call call;
        if (repoType.equals("myTests"))
            call = service.addTestToRepository(Objects.requireNonNull(userInfo.getValue()).getMyTests(), testID, "1");
        else
            call = service.addTestToRepository(Objects.requireNonNull(userInfo.getValue()).getJoinTests(), testID, "1");

        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {

            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {

            }
        });
    }

    private void createChatRoomFirebase(String testID, String adminID) {
        Call call;
        call = service.createChatRoom(testID, "-ADMIN", adminID);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {

            }
        });
    }

    public void findTestFromFirebase(String id) {
        Call call = service.getTestFromFirebase(id);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.body() != null)
                    findTestResult.postValue((Test) response.body());
                else
                    isFindTestResultNull.setValue(true);
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {
                isFindTestResultNull.setValue(false);
            }
        });
    }

    public void submitAnswerToRepoFirebase(Answer answer, String testID) {
        Call call = service.checkIfAnswerSubmitted(testID, user.getUid());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.body() == null) {
                    Call call1 = service.submitAnswer(testID, user.getUid(), answer);
                    call1.enqueue(new Callback() {
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) {

                        }

                        @Override
                        public void onFailure(@NotNull Call call, @NotNull Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {
            }
        });
    }

    public void checkIfAnswerSubmitted(String testID) {
        Call call = service.checkIfAnswerSubmitted(testID, user.getUid());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.body() == null) {
                    checkAnswerSubmitted.postValue(0);
                }
                else {
                    checkAnswerSubmitted.postValue(1);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {

            }
        });
    }

    public void getTestResult(String testID) {
        finishGetResult.setValue(false);
        answerList.clear();
        databaseReference.child("answers").child(testID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    answerList.add(dataSnapshot.getValue(Answer.class));
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

    public MutableLiveData<ArrayList<Test>> getJoinedTestList() {
        return joinedTestList;
    }

    public void manageTest(Test test) {
        Call call = service.manageTest(test.getTestID(), test);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {

            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {

            }
        });
    }

    public MutableLiveData<ArrayList<Test>> getMyTestList() {
        return myTestList;
    }

    public MutableLiveData<User> getUserInfo() {return userInfo;}

    public FirebaseUser getUser() {return user;}

    public MutableLiveData<Integer> getLogInState() {return logInState;}

    public MutableLiveData<Test> getFindTestResult() {
        return findTestResult;
    }

    public MutableLiveData<Boolean> getProgressing() {return progressing;}

    public MutableLiveData<Integer> getCheckAnswerSubmitted() {return checkAnswerSubmitted;}

    public ArrayList<Answer> getAnswerList() {
        return answerList;
    }

    public MutableLiveData<Boolean> getFinishGetResult() {
        return finishGetResult;
    }

    public MutableLiveData<ChatRoom> getCurrentChatRoom() {return currentChatRoom;}

    public MutableLiveData<ArrayList<Message>> getMessages() {return messages;}

    public MutableLiveData<Boolean> getIsFindTestResultNull() {return isFindTestResultNull;}

    public MutableLiveData<ArrayList<String>> getNewNotification() {return newNotification;}

    public MutableLiveData<ArrayList<ChatRoom>> getChatRoomList() {return chatRoomList;}

    public MutableLiveData<ArrayList<Notification>> getScoreLog() {return scoreLog;}

    public MutableLiveData<ArrayList<Notification>> getNormalLog() {return normalLog;}

    public MutableLiveData<Integer> getCheckAdmin() {return checkAdmin;}


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

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String date = test.getDate() + " " + test.getStartTime() + ":00";

        try {
            calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(date)));
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
        ArrayList<String> list = new ArrayList<>(newNotification.getValue());
        boolean check = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(testID)) {
                check = true;
                break;
            }

        }
        if (!check) {
            list.add(testID);
            newNotification.setValue(list);
        }
    }

    public void removeNewNotification(String testID) {
        ArrayList<String> list = new ArrayList<>(newNotification.getValue());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(testID)) {
                list.remove(i);
                newNotification.setValue(list);
                break;
            }
        }
    }

    public void sendMessage(String testID, Message message) {
        Call call = service.sendMessage(testID, message);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {

            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {

            }
        });
    }

    public void saveScoreLog(Notification notification) {
        Call call = service.saveScoreLog(user.getUid(), notification);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    public void saveNormalLog(Notification notification) {
        Call call = service.saveLog(user.getUid(), notification);
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
        Objects.requireNonNull(messages.getValue()).clear();
        databaseReference.child("chatroom").child(testID).addChildEventListener(currentChatRoomListener);
    }

    public void removeCurrentChatRoomListener(String testID) {
        Objects.requireNonNull(messages.getValue()).clear();
        databaseReference.child("chatroom").child(testID).removeEventListener(currentChatRoomListener);
    }
}
