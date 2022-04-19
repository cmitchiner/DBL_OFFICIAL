package com.example.messagingapp.objects;

public class User {

    /** VARIABLES **/
    //Strings to hold information for each users profile
    //token is for firebase cloud messaging notification service
    public String fullName, email, username, phone, token, UID;

    //Float to hold users rating, should be within 0.0 through 5.0
    public float rating;

    /**
     * Default constructor
     */
    public User() {

    }

    /**
     * Paramaterized constructor to initialize necessary profile information
     *
     * @param fullName the users full name
     * @param username the users username
     * @param phone the users phone number
     * @param email the users email address
     *
     */
    public User(String fullName, String username, String phone, String email) {
        //Initialize user object
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.phone = phone;
    }
}
