package com.example.messagingapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ListFacade implements Parcelable {
    @SerializedName("listID")
    private String list_iD;
    @SerializedName("title")
    private String title;
    @SerializedName("price")
    private int price;
    @SerializedName("type")
    private String type;
    @SerializedName("courseCode")
    private String courseCode;
    @SerializedName("university")
    private String university;
    @SerializedName("isBid")
    private boolean isBid;
    @SerializedName("isbn")
    private Long isbn;
    @SerializedName("location")
    private String location;

    //Construtor
    public ListFacade(String iD, String atitle, int aprice, String atype, String acourseCode,
                      String auniversity, boolean aisBid, Long aisbn, String alocation ){
        list_iD = iD;
        title = atitle;
        price = aprice;
        type = atype;
        courseCode = acourseCode;
        university = auniversity;
        isBid = aisBid;
        isbn = aisbn;
        location = alocation;

    }

    protected ListFacade(Parcel in) {
        list_iD = in.readString();
        title = in.readString();
        price = in.readInt();
        type = in.readString();
        courseCode = in.readString();
        university = in.readString();
        isBid = in.readByte() != 0;
        isbn = in.readLong();
        location = in.readString();
    }

    public static final Creator<ListFacade> CREATOR = new Creator<ListFacade>() {
        @Override
        public ListFacade createFromParcel(Parcel in) {
            return new ListFacade(in);
        }

        @Override
        public ListFacade[] newArray(int size) {
            return new ListFacade[size];
        }
    };

    public String getList_iD(){return this.list_iD;}
    public void setList_iD(String listId){this.list_iD = listId;}

    public String getTitle(){ return this.title; }
    public void setTitle(String title){this.title = title;}

    public int getPrice(){
        return this.price;
    }
    public void setPrice(int price){this.price = price;}

    public String getType(){
        return this.type;
    }
    public void setType(String type){this.type = type;}

    public String getCourseCode(){
        return courseCode;
    }
    public void setCourseCode(String courseCode){this.courseCode = courseCode;}

    public String getUniversity(){
        return this.university;
    }
    public void setUniversity(String university){this.university = university;}

    public boolean getIsBid(){return this.isBid;}
    public void setBid(boolean isBid){this.isBid = isBid;}

    public Long getIsbn(){ return this.isbn; }
    public void setIsbn(Long isbn) { this.isbn = isbn; }

    public String getLocation(){ return this.location; }
    public void setLocation(String location){ this.location = location; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(list_iD);
        parcel.writeString(title);
        parcel.writeInt(price);
        parcel.writeString(type);
        parcel.writeString(courseCode);
        parcel.writeString(university);
        parcel.writeLong(isbn);
        parcel.writeString(location);
    }
}
