package com.example.messagingapp.utilities;

import java.util.HashMap;

public class Constants {
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String,String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAq2tBGf4:APA91bE5nirp8oofzY6IgzTaXlYEyk2DxckLiUFmOSG83OgQsNcMLrqEUmDzDkiVqGSyo2Hz6yZtzZ5yxTeMHpez6jFKKUAwlmVOtrJLbxAKM_2dOuB9rhOPw5FrKyrRetFBHN6ygQAo"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }

}
