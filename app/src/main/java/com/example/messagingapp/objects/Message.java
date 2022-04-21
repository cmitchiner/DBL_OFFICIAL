package com.example.messagingapp.objects;

import java.util.UUID;

/**
 * Class for each individual message sent by a user
 */
public class Message {

    /**
     * VARIABLES
     **/
    String message; //body of message
    String senderID; //Sender UID
    long timestamp; //Time message was sent
    String currentTime; //Current time in string format
    String uniqueID; // the messages unique identifier
    Boolean isImage; //Boolean to define whether a message contains an image

    /**
     * Default Constructor
     */
    public Message() {
    }

    /**
     * getUniqueID() : returns the messages unique identifier
     *
     * @return a randomly generated unique identifier to reference the message by
     */
    public String getUniqueID() {
        return uniqueID;
    }

    /**
     * setUniqueID : sets a messages unique id to a passed value
     *
     * @param uniqueID the unique ID to be set
     * @post @code{this.uniqueID = uniqueID;}
     */
    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    /**
     * Paramterized constructor for a message that only contains text i.e NO image
     *
     * @param message     the text of the message
     * @param senderID    the senders UID
     * @param timestamp   the timestamp the message was sent at
     * @param currentTime a string containing the time the message was sent at
     * @post a UID was assigned to the message
     */
    public Message(String message, String senderID, long timestamp, String currentTime) {
        this.message = message;
        this.senderID = senderID;
        this.timestamp = timestamp;
        this.currentTime = currentTime;
        //Set a boolean to reference that this message is not an image
        this.isImage = false;
        this.uniqueID = UUID.randomUUID().toString();
    }

    /**
     * Paramterized constructor for a message that only contains an image i.e NO text
     *
     * @param senderID    the senders UID
     * @param timestamp   the timestamp the message was sent at
     * @param currentTime a string containing the time the message was sent at
     * @post a UID was assigned to the message
     */
    public Message(String senderID, long timestamp, String currentTime) {
        this.senderID = senderID;
        this.timestamp = timestamp;
        this.currentTime = currentTime;
        //Set a boolean to reference that this message is an image
        this.isImage = true;
        this.uniqueID = UUID.randomUUID().toString();
    }

    /**
     * Getter function for the isImage boolean
     *
     * @return isImage
     */
    public Boolean getImage() {
        return isImage;
    }

    /**
     * Getter function for the message body string
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter function for the message body string
     *
     * @param message the string to set @code{this.message} to
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter function for the senders UID
     *
     * @return the senders UID
     */
    public String getSenderID() {
        return senderID;
    }

    /**
     * Getter function for the current time
     *
     * @return the current time as a string
     */
    public String getCurrentTime() {
        return currentTime;
    }


}
