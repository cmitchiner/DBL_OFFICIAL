package com.example.messagingapp.model;

import android.location.Location;

public class Listing {
    private String photo;
    private int price;
    private String type;
    private int reportCounter;
    private boolean sold;
    private String title;
    private long isbn;
    private Location location;
    private String language;
    private int sellingPrice;
    private String description;
    private String university;
    private String courseCode;
    private int Rating;

    public String getPhotos(){
        return this.photo;
    }
    public void setPhoto(String Photo){ this.photo = photo; }

    public int getPrice(){ return this.price;}
    public void setPrice(int price){this.price = price; }

    public String getType(){ return this.type;}
    public void setType( String type){ this.type = type; }

    public int getReportCounter(){ return this.reportCounter; }
    public void setReportCounter(int repCounter){ this.reportCounter = repCounter; }

    public boolean getSold(){
        return this.sold;
    }
    public void setSold(boolean sold){ this.sold = sold; }

    public String getTitle(){ return this.title; }
    public void setTitle(String title){ this.title = title; }

    public long getIsbn(){ return this.isbn; }
    public void setIsbn(long isbn){ this.isbn = isbn; }

    public Location getlocation() { return this.location; }
    public void setLocation(Location location) { this.location = location; }

    public String getLanguage() { return this.language; }
    public void setLanguage(String language) { this.language = language; }

    public int getSellingPrice() { return this.sellingPrice; }
    public void setSellingPrice(int sellingPrice) { this.sellingPrice = sellingPrice; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getUniversity() { return this.university; }
    public void setUniversity(String university) { this.university = university; }

    public String getCourseCode() { return this.courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public int getRating() { return Rating; }
    public void setRating(int rating) { Rating = rating; }

    public void getAuction(){
    }

    public void report(String reason){
    }
}
