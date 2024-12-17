package com.example.finnew;

public class QRData {
    private String qrName;
    private String imageUrl;

    public QRData() {
        // Default constructor required for Firebase
    }

    public QRData(String qrName, String imageUrl) {
        this.qrName = qrName;
        this.imageUrl = imageUrl;
    }

    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}