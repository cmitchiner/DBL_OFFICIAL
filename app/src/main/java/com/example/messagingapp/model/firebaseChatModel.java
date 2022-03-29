package com.example.messagingapp.model;

public class firebaseChatModel {

    static String name;
    String uid;

    public firebaseChatModel(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }
    public firebaseChatModel() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
