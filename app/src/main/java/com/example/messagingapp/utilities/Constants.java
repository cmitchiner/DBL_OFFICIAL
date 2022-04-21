package com.example.messagingapp.utilities;

import java.util.HashMap;

public class Constants {
    /**
     * Constants for Notifications through firebase cloud messaging
     */
    //A string to reference the authorization part of the JSON object
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    //A string to reference the content type part of the JSON object
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    //A string to reference the data part of the JSON object
    public static final String REMOTE_MSG_DATA = "data";
    //A string to reference the UIDs of sender and receiver for the JSON object
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    //Hashmap to hold firebase cloud messaging authorization key
    public static HashMap<String, String> remoteMsgHeaders = null;

    /**
     * getRemoteMsgHeaders() : Initializes remoteMsgHeaders hashmap if non-null
     *
     * @return a non-null hashmap containing the content type and authorization key
     */
    public static HashMap<String, String> getRemoteMsgHeaders() {
        //Verify we have not already initialized the hash map
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            //define firebase cloud messaging authorization key
            remoteMsgHeaders.put(REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAq2tBGf4" +
                            ":APA91bE5nirp8oofzY6IgzTaXlYEyk2DxckLiUFmOSG83OgQsNcMLrqEUmDzDkiVqGSyo2Hz6yZtzZ5yxTeMHpez6jFKKUAwlmVOtrJLbxAKM_2dOuB9rhOPw5FrKyrRetFBHN6ygQAo");
            //make sure type is a json object
            remoteMsgHeaders.put(REMOTE_MSG_CONTENT_TYPE, "application/json");
        }
        return remoteMsgHeaders;
    }

}
