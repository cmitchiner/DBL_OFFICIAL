package com.example.messagingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Listing implements Parcelable {
    public static final Creator<Listing> CREATOR = new Creator<Listing>() {
        @Override
        public Listing createFromParcel(Parcel in) {
            return new Listing(in);
        }

        @Override
        public Listing[] newArray(int size) {
            return new Listing[size];
        }
    };
    private String listID;
    private ArrayList<String> photo;
    private int price;
    private String type;
    private int reportCounter;
    private boolean sold;
    private String title;
    private long isbn;
    private String location;
    private String language;
    private String auc_id;
    private String description;
    private String university;
    private String courseCode;
    private String userId;

    //Listing constructor for books
    public Listing(String id, ArrayList<String> photo, int price, String type, int reportCounter, boolean sold, String title, long isbn,
                   String location, String language, String auc_id, String description, String university, String courseCode, String userId) {
        this.listID = id;
        this.photo = photo;
        this.price = price;
        this.type = type;
        this.reportCounter = reportCounter;
        this.sold = sold;
        this.title = title;
        this.isbn = isbn;
        this.location = location;
        this.language = language;
        this.auc_id = auc_id;
        this.description = description;
        this.university = university;
        this.courseCode = courseCode;
        this.userId = userId;
    }

    //Listing constructor for notes and summaries
    public Listing(String id, ArrayList<String> photo, int price, String type, int reportCounter, boolean sold, String title, String location,
                   String language, String auc_id, String description, String university, String courseCode, String userId) {
        this.listID = id;
        this.photo = photo;
        this.price = price;
        this.type = type;
        this.reportCounter = reportCounter;
        this.sold = sold;
        this.title = title;
        this.isbn = 0;
        this.location = location;
        this.language = language;
        this.auc_id = auc_id;
        this.description = description;
        this.university = university;
        this.courseCode = courseCode;
        this.userId = userId;
    }

    //Method for getting the values from the parcel object
    protected Listing(Parcel in) {
        listID = in.readString();
        photo = in.readArrayList(String.class.getClassLoader());
        price = in.readInt();
        type = in.readString();
        reportCounter = in.readInt();
        sold = in.readByte() != 0;
        title = in.readString();
        isbn = in.readLong();
        location = in.readString();
        language = in.readString();
        auc_id = in.readString();
        description = in.readString();
        university = in.readString();
        courseCode = in.readString();
        userId = in.readString();
    }

    //Getter for photo
    public ArrayList<String> getPhotos() {
        return this.photo;
    }

    //Getter for listID
    public String getListId() {
        return this.listID;
    }

    //Getter for price
    public int getPrice() {
        return this.price;
    }

    //Getter for type
    public String getType() {
        return this.type;
    }

    //Getter for reportCounter
    public int getReportCounter() {
        return this.reportCounter;
    }

    //Setter for reportCounter
    public void setReportCounter(int repCounter) {
        this.reportCounter = repCounter;
    }

    //Getter for sold
    public boolean getSold() {
        return this.sold;
    }

    //Getter for title
    public String getTitle() {
        return this.title;
    }

    //Getter for isbn
    public long getIsbn() {
        return this.isbn;
    }

    //Getter for location
    public String getlocation() {
        return this.location;
    }

    //Getter for description
    public String getDescription() {
        return this.description;
    }

    //Getter for university
    public String getUniversity() {
        return this.university;
    }

    //Getter for courseCode
    public String getCourseCode() {
        return this.courseCode;
    }

    //Getter for userId
    public String getUser() {
        return userId;
    }

    //Getter for isBid
    public boolean getIsBid() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Writing values to the parcel object
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(listID);
        parcel.writeList(photo);
        parcel.writeInt(price);
        parcel.writeString(type);
        parcel.writeInt(reportCounter);
        parcel.writeByte((byte) (sold ? 1 : 0));
        parcel.writeString(title);
        parcel.writeLong(isbn);
        parcel.writeString(location);
        parcel.writeString(language);
        parcel.writeString(auc_id);
        parcel.writeString(description);
        parcel.writeString(university);
        parcel.writeString(courseCode);
        parcel.writeString(userId);
    }
}
