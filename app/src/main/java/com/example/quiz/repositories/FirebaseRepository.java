package com.example.quiz.repositories;

import android.app.Application;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.lifecycle.MutableLiveData;


import com.example.quiz.models.Answer;
import com.example.quiz.models.FirebaseService;

import com.example.quiz.models.Test;
import com.example.quiz.models.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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

    //retrofit
    Retrofit retrofit;
    FirebaseService service;

    //for navigate between login fragment and home fragment
    private MutableLiveData<Integer> logInState;

    //for progress dialog fragment
    private MutableLiveData<Boolean> progressing;

    //for getting realtime repository update
    DatabaseReference databaseReference;
    ValueEventListener joinTestListener;
    ValueEventListener myTestListener;


    //save test for adapter
    private ArrayList<Test> listMyTest;
    private ArrayList<Test> listJoinTest;

    //temp var for value event listener
    private long myTestCount;
    private long joinTestCount;

    //if value change, notify adapter to update dataset
    MutableLiveData<Integer> updateMyTest;
    MutableLiveData<Integer> updateJoinTest;

    MutableLiveData<Integer> checkAnswerSubmitted;

    public FirebaseRepository(Application application) {
        this.application = application;

        databaseReference = FirebaseDatabase.getInstance().getReference();
        initValueListener();
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

        retrofit = new Retrofit.Builder()
                .baseUrl("https://quiz-5bb63-default-rtdb.firebaseio.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(FirebaseService.class);//async

        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    user = firebaseAuth.getCurrentUser();

                    Boolean loop = true;
                    while (loop) {// wait for retrofit service build complete
                        if (service != null) {
                            if (user != null) {
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

    public void logOut() {
        removeValueListenerToRepositories();
        listJoinTest.clear();
        listMyTest.clear();
        firebaseAuth.signOut();
        userInfo.postValue(null);
    }

    private void initValueListener() {
        joinTestListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listJoinTest.clear();
                joinTestCount = 0;
                joinTestCount = snapshot.getChildrenCount();
                updateJoinTest.setValue(0);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Call call = service.getTestFromFirebase(dataSnapshot.getKey());
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            listJoinTest.add((Test) response.body());
                            joinTestCount--;
                            if (joinTestCount == 0) {
                                updateJoinTest.postValue(updateJoinTest.getValue() + 1);
                                //update Adapter
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        myTestListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listMyTest.clear();
                myTestCount = 0;
                myTestCount = snapshot.getChildrenCount();
                updateMyTest.setValue(0);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Call call = service.getTestFromFirebase(dataSnapshot.getKey());
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            listMyTest.add((Test) response.body());
                            myTestCount--;
                            if (myTestCount == 0)
                                updateMyTest.postValue(updateMyTest.getValue() + 1);
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };


    }

    private void addValueListenerToRepositories() {
        //Add value listener to the node
        databaseReference.child("repositories").child(userInfo.getValue().getJoinTests()).addValueEventListener(joinTestListener);
        databaseReference.child("repositories").child(userInfo.getValue().getMyTests()).addValueEventListener(myTestListener);
    }

    private void removeValueListenerToRepositories() {
        databaseReference.child("repositories").child(userInfo.getValue().getJoinTests()).removeEventListener(joinTestListener);
        databaseReference.child("repositories").child(userInfo.getValue().getMyTests()).removeEventListener(myTestListener);
    }

    public void getUserInfoFirebase() {
        Call<User> call = service.getUser(user.getUid());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                userInfo.setValue(response.body());
                addValueListenerToRepositories();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void addUserFirebase(String userID, User user) {
        Call call = service.createUser(userID, user);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("result", "creating user success!");
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("result", "creating user failed!");
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

    public void findTestFromFirebase(String id) {
        Call call = service.getTestFromFirebase(id);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                findTestResult.postValue((Test) response.body());
            }

            @Override
            public void onFailure(Call call, Throwable t) {

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
                    Log.d("response body", "answer already submitted");
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("result", "onFailure: ");
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

}
