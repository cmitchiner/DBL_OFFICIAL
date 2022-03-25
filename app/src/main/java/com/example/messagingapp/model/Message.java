package com.example.messagingapp.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    @SerializedName("sender")
    private String senderId;
    @SerializedName("sender")
    private String recieverId;
    @SerializedName("text")
    private String text;
    @SerializedName("image")
    private String img;
    @SerializedName("date")
    private String date;
    @SerializedName("read")
    private boolean read;

    private ZonedDateTime dateObject;

    public String getSenderId() {
        return senderId;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public String getText() {
        return text;
    }

    public String getImg() {
        return img;
    }

    public ZonedDateTime getDate() {
        if(dateObject == null) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZZZZZ");
            dateObject = ZonedDateTime.parse(date, format);
        }
        return dateObject;
    }

    public boolean isRead() {
        return read;
    }
}
