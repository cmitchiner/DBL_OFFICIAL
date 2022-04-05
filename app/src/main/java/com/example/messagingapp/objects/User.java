package com.example.messagingapp.objects;

import java.util.ArrayList;

public class User {

    public String fullName, email, username, phone, token, UID;

    public float rating;

    public ArrayList<String> usersInChatWith;

    public User() {

    }

    public User(String fullName, String username, String phone, String email) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.phone = phone;
    }
}
