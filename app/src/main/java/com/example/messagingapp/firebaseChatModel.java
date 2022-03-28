package com.example.messagingapp;

public class firebaseChatModel {

    String name;
    String uid;
    String image;

    public firebaseChatModel(String name, String uid, String image) {
        this.name = name;
        this.uid = uid;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
