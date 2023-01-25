package com.example.myapp.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Product implements Parcelable {

    private String name, pic, description;
    private long price, value, like;

    public Product() {
    }

    public Product(String name, String pic, String description, long price) {
        this.name = name;
        this.pic = pic;
        this.description = description;
        this.price = price;
    }

    protected Product(Parcel in) {
        name = in.readString();
        pic = in.readString();
        description = in.readString();
        price = in.readLong();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public HashMap<String, Object> makeMap(){
        HashMap<String, Object> productToCart = new HashMap<>();
        productToCart.put("name", name);
        productToCart.put("pic", pic);
        productToCart.put("price", price);
        productToCart.put("value", 1);
        return productToCart;
    }

    public long getLike() {
        return like;
    }

    public void setLike(long like) {
        this.like = like;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        parcel.writeString(name);
        parcel.writeString(pic);
        parcel.writeString(description);
        parcel.writeLong(price);
    }

    // преобразование экземпляра класса в Bundle для передачи в Intent
    public Bundle productToBundle(){
        Bundle bundle = new Bundle();
        // Передаем экземпляр класса
        bundle.putParcelable("product", this);
        return bundle;
    }
}
