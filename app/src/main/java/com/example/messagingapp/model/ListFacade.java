package com.example.messagingapp.model;

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

    public String getTitle(){ return this.title; }

    public int getPrice(){
        return this.price;
    }

    public String getType(){
        return this.type;
    }

    public String getCourseCode(){
        return courseCode;
    }

    public String getUniversity(){
        return this.university;
    }

    public boolean getIsBid(){return this.isBid;}

    public Long getIsbn(){ return this.isbn; }

    public String getLocation(){ return this.location; }

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
