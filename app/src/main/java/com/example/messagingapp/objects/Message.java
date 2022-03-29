package com.example.messagingapp.objects;

public class Message {


    String message;
    String senderID;
    long timestamp;
    String currentTime;
    Boolean isNew;

    public Message() {
    }

    public Message(String message, String senderID, long timestamp, String currentTime, boolean isNew) {
        this.message = message;
        this.senderID = senderID;
        this.timestamp = timestamp;
        this.currentTime = currentTime;
        this.isNew = isNew;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean aNew) {
        isNew = aNew;
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
