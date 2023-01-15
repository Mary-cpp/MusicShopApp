package com.example.myapp;

public class Order {

    private String number;
    private String details;
    private int price;

    public Order(String number, String details, int price) {
        this.number = number;
        this.details = details;
        this.price = price;
    }

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
