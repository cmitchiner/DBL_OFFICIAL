package com.example.messagingapp.network;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Class for getting a retrofit client that connects to the google api
 */
public class ApiClient {
    // Retrofit instance
    private static Retrofit retrofit = null;

    /**
     * Create retrofit instance if none has been created yet
     *
     * @return retrofit instance
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/fcm/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
