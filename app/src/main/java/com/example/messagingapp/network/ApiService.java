package com.example.messagingapp.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ApiService {
    /**
     * Retrofit specification for sending a message
     *
     * @param headers Headers of the message
     * @param messageBody the message to be send
     * @return
     */
    @POST("send")
    Call<String> sendMessage(
                @HeaderMap HashMap<String, String> headers,
                @Body String messageBody
            );
}
