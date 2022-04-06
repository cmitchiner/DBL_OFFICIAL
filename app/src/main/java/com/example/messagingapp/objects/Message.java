package com.example.messagingapp.objects;

import android.net.Uri;

import java.io.File;
import java.net.URI;
import java.util.UUID;

public class Message {


    String message;
    String senderID;
    long timestamp;
    String currentTime;
    String uniqueID;
    //Boolean isNew;
    Boolean isImage;

    public Message() {
    }


    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public Message(String message, String senderID, long timestamp, String currentTime) {
        this.message = message;
        this.senderID = senderID;
        this.timestamp = timestamp;
        this.currentTime = currentTime;
        this.isImage = false;
        this.uniqueID = UUID.randomUUID().toString();
    }
    public Message( String senderID, long timestamp, String currentTime) {
        this.senderID = senderID;
        this.timestamp = timestamp;
        this.currentTime = currentTime;
        this.isImage = true;
        this.uniqueID = UUID.randomUUID().toString();
    }

    public Boolean getImage() {
        return isImage;
    }

    public void setImage(Boolean image) {
        isImage = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

}
