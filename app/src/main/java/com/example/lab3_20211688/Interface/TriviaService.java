package com.example.lab3_20211688.Interface;

import com.example.lab3_20211688.Bean.TriviaResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TriviaService {
    @GET("/api.php")
    Call<TriviaResponse> getTrivia(
            @Query("amount") int amount,
            @Query("category") int category,
            @Query("difficulty") String difficulty,
            @Query("type") String type
    );

    TriviaService triviaService = new Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TriviaService.class);
}
