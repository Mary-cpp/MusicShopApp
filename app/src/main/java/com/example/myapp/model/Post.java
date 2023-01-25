package com.example.myapp.model;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Post implements Parcelable {
    private String title, epigraph, text, author;
    private Date date;


    public Post() {
    }

    public Post(String postName, String epigraph, String postText, String author, Date postDate) {
        this.title = postName;
        this.epigraph = epigraph;
        this.text = postText;
        this.author = author;
        this.date = postDate;
    }

    protected Post(Parcel in) {
        title = in.readString();
        epigraph = in.readString();
        text = in.readString();
        author = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getEpigraph() {
        return epigraph;
    }

    public String getText() {
        return text;
    }

    public void setTitle(String postName) {
        this.title = postName;
    }

    public void setEpigraph(String epigraph) {
        this.epigraph = epigraph;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(epigraph);
        parcel.writeString(text);
        parcel.writeString(author);
    }

    // "Упаковка" объекта Post в Bundle для передачи вместе с действием навигации
    public Bundle postToBundle(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("post", this);
        return bundle;
    }
}

