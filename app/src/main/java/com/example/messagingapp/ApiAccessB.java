package com.example.messagingapp;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiAccessB {
    @GET("/listings")
    Call<ArrayList<com.example.messagingapp.ListFacade>> getInfo(@Query("apiKey") String apiKey, @Query("numberRows") int numberRows);

}
