package com.example.finnew;

public class Expense {
    public String amount, category, description, date, imageUrl;
    public double latitude, longitude; // Store latitude and longitude as doubles

    // Default constructor (required for Firebase)
    public Expense() {}

    // Parameterized constructor with latitude and longitude
    public Expense(String amount, String category, String description, String date, double latitude, double longitude) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
