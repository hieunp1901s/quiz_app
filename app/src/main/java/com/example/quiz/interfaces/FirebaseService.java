package com.example.quiz.interfaces;
import com.example.quiz.models.Answer;
import com.example.quiz.models.Message;
import com.example.quiz.models.Test;
import com.example.quiz.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FirebaseService {
    @GET("/users/{id}.json")
    Call<User> getUser(@Path("id") String id);

    @PUT("/users/{id}.json")
    Call<Void> createUser(@Path("id") String id, @Body User user);

    @PUT("/repositories/{repoID}/{testID}.json")
    Call<Void> addTestToRepository(@Path("repoID") String repoID, @Path("testID") String testID, @Body String status);

    @PUT("tests/{id}.json")
    Call<Void> addTest(@Path("id") String id, @Body Test test);

    @GET("/tests/{id}.json")
    Call<Test> getTestFromFirebase(@Path("id") String id);

    @GET("/answers/{testID}/{answerID}.json")
    Call<Answer> checkIfAnswerSubmitted(@Path("testID") String testID, @Path("answerID") String answerID);

    @PUT("/answers/{testID}/{answerID}.json")
    Call<Void> submitAnswer(@Path("testID") String testID, @Path("answerID") String answerID, @Body Answer answer);

    @PUT("/chatroom/{testID}/{admin}.json")
    Call<Void> createChatRoom(@Path("testID") String testID, @Path("admin") String admin, @Body String adminID);

    @POST("/chatroom/{testID}.json")
    Call<Void> sendMessage(@Path("testID") String testID, @Body Message message);

    @PUT("/tests/{testID}.json")
    Call<Void>  manageTest(@Path("testID") String testID, @Body Test test);

}
