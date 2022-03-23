package com.example.messagingapp;

import java.util.ArrayList;

public class User {

    public String fullName, email, username, phone;

    public ArrayList<String> usersInChatWith;

    public User() {

    }

    public User(String fullName, String username, String phone, String email) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.phone = phone;
    }

    public ArrayList<String> getUsersInChatWith() {
        return usersInChatWith;
    }

    public void addUserInChat(String uid) {
        usersInChatWith.add(uid);
    }
}
