package com.example.myapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Order implements Parcelable{

    private String number;
    private String details;
    private long price;

    public Order() {
    }

    public Order(String number, String details, long price) {
        this.number = number;
        this.details = details;
        this.price = price;
    }

    public Order(Parcel in){
        number = in.readString();
        details = in.readString();
        price = in.readLong();
    }

    public HashMap<String, Object> makeMap(){
        HashMap<String, Object> orderToList = new HashMap<>();
        orderToList.put("number", number);
        orderToList.put("details", details);
        orderToList.put("price", price);
        return orderToList;
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(number);
        parcel.writeString(details);
        parcel.writeLong(price);
    }
}
