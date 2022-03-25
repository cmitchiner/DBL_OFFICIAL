package com.example.messagingapp.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.messagingapp.User;

import java.util.ArrayList;

public class Listing implements Parcelable {
    private ArrayList<String> photo;
    private int price;
    private String type;
    private int reportCounter;
    private boolean sold;
    private String title;
    private long isbn;
    private Location location;
    private String language;
    private String auc_id;
    private String description;
    private String university;
    private String courseCode;
    private String userId;

    public Listing(ArrayList<String> photo, int price, String type, int reportCounter, boolean sold, String title, long isbn, Location location, String language, String auc_id, String description, String university, String courseCode, String userId) {
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

    protected Listing(Parcel in) {
        photo = in.readArrayList(String.class.getClassLoader());
        price = in.readInt();
        type = in.readString();
        reportCounter = in.readInt();
        sold = in.readByte() != 0;
        title = in.readString();
        isbn = in.readLong();
        location = in.readParcelable(Location.class.getClassLoader());
        language = in.readString();
        auc_id = in.readString();
        description = in.readString();
        university = in.readString();
        courseCode = in.readString();
        userId = in.readString();
    }

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

    public ArrayList<String> getPhotos(){
        return this.photo;
    }
    public void addPhoto(String Photo){ this.photo.add(Photo); }

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

    public String getAucId() { return this.auc_id; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getUniversity() { return this.university; }
    public void setUniversity(String university) { this.university = university; }

    public String getCourseCode() { return this.courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getUser() { return userId; }

    public boolean getIsBid() {
        return false;
    }

    public void getAuction(){
    }

    public void report(String reason){
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(photo);
        parcel.writeInt(price);
        parcel.writeString(type);
        parcel.writeInt(reportCounter);
        parcel.writeByte((byte) (sold ? 1 : 0));
        parcel.writeString(title);
        parcel.writeLong(isbn);
        parcel.writeParcelable(location, i);
        parcel.writeString(language);
        parcel.writeString(auc_id);
        parcel.writeString(description);
        parcel.writeString(university);
        parcel.writeString(courseCode);
        parcel.writeString(userId);
    }
}
