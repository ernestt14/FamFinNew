package com.example.finnew;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainQR extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.goToCameraButton).setOnClickListener(v -> {
            Intent intent = new Intent(MainQR.this, CameraActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.goToQRGalleryButton).setOnClickListener(v -> {
            Intent intent = new Intent(MainQR.this, QRGalleryActivity.class);
            startActivity(intent);
        });
    }
}