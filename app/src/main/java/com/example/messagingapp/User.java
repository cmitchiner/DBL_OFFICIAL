package com.example.messagingapp;

public class User {

    public String fullName, email, username, phone;

    public User() {

    }

    public User(String fullName, String username, String phone, String email) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.phone = phone;
    }
}
