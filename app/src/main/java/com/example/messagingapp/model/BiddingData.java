package com.example.messagingapp.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BiddingData {

    @SerializedName("auc_id")
    private String aucId;  //ID of auction entry
    @SerializedName("starting_price")
    private int startingPrice;  //Starting price of auction in cents
    @SerializedName("current_price")
    private int currentPrice;   //Current highest bid in cents
    @SerializedName("auction_end")
    private String bidEnd;   //Ending date and time of
    @SerializedName("highest_bidder")
    private String highestBidder;

    private ZonedDateTime date = null;

    public String getAucId() {
        return aucId;
    }

    public int getStartingPrice() {
        return startingPrice;
    }

    public int getCurrentPrice() {
        return currentPrice;
    }

    public ZonedDateTime getBidEnd() {
        if(date == null) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZZZZZ");
            date = ZonedDateTime.parse(bidEnd, format);
        }
        return date;
    }

    public String getHighestBidder() {
        return highestBidder;
    }
}
