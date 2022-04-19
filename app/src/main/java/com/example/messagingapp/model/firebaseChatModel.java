package com.example.messagingapp.model;

/**
 * A class for the FirebaseRecyclerAdapater such that when reading in data from firebase real-time database
 * it can be automatically parsed into the recylcer.
 */
public class firebaseChatModel {
    /** VARIABLES **/

    static String name; //name of sender
    String uid; //UID of sender

    /**
     * Paramaterized constructor
     *
     * @param name the senders name to be set
     * @param uid the senders UID to be set
     */
    public firebaseChatModel(String name, String uid) {
        //Init variables
        this.name = name;
        this.uid = uid;
    }

    /**
     * Default constructor
     */
    public firebaseChatModel() {}

    /**
     * GETTERS AND SETTERS
     */
    //Get string name
    public String getName() {
        return name;
    }
    //Set string name
    public void setName(String name) {
        this.name = name;
    }
    //get sender UID
    public String getUid() {
        return uid;
    }
    //set sender UID
    public void setUid(String uid) {
        this.uid = uid;
    }

}
