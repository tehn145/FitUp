package com.example.fitup;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GroqApiService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    Call<GroqModels.Response> getChatCompletion(
            @Header("Authorization") String authHeader,
            @Body GroqModels.Request requestBody
    );
}