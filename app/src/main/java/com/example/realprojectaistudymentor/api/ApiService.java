package com.example.realprojectaistudymentor.api;

import com.example.realprojectaistudymentor.model.Answer;
import com.example.realprojectaistudymentor.model.PracticeQuiz;
import com.example.realprojectaistudymentor.model.Student;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // Auth
    @POST("auth/register")
    Call<Student> register(@Body Student student);

    @POST("auth/login")
    Call<Student> login(@Body Student student);

    // AI Q&A
    @POST("ai/ask")
    Call<Answer> askQuestion(@Body String question);

    // Quiz
    @GET("quiz/generate/{subject}")
    Call<PracticeQuiz> generateQuiz(@Path("subject") String subject);
}
