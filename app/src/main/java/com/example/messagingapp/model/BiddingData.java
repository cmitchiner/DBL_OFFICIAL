package com.example.messagingapp.model;

import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


/**
 * TODO Implement usage of this class and retrofit methods
 * Class for implementing the bidding system,
 */
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
    private String highestBidder; // ID of the highestbidder

    private ZonedDateTime date = null; // Zoneddatetime for easy calculation of difference between dates and times

    // Getter for aucID
    public String getAucId() {
        return aucId;
    }

    // Getter for StartingPrice
    public int getStartingPrice() {
        return startingPrice;
    }

    // Getter for currentprice
    public int getCurrentPrice() {
        return currentPrice;
    }

    // Getter for end of the auction, converts it from string to ZonedDateTime first
    public ZonedDateTime getBidEnd() {
        if (date == null) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZZZZZ");
            date = ZonedDateTime.parse(bidEnd, format);
        }
        return date;
    }

    // Get highest bidder ID
    public String getHighestBidder() {
        return highestBidder;
    }
}
