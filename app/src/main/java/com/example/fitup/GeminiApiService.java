package com.example.fitup;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApiService {
    @Headers("Content-Type: application/json")

    @POST("v1/models/gemini-2.0-flash:generateContent")
    Call<GeminiModels.Response> getResponse(
            @Query("key") String apiKey,
            @Body GeminiModels.Request requestBody
    );
}