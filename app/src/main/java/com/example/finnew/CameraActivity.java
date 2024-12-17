package com.example.finnew;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.util.UUID;

public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 101;

    private ImageView capturedImageView;
    private Uri imageUri;
    private EditText qrNameEditText;
    private Button captureButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        capturedImageView = findViewById(R.id.capturedImageView);
        captureButton = findViewById(R.id.captureButton);
        saveButton = findViewById(R.id.saveButton);
        qrNameEditText = findViewById(R.id.qrNameEditText);

        captureButton.setOnClickListener(v -> {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                // Request Camera Permission
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
        });


        saveButton.setOnClickListener(v -> {
            String qrName = qrNameEditText.getText().toString();
            if (!TextUtils.isEmpty(qrName) && imageUri != null) {
                saveImageToGallery(qrName);
            } else {
                Toast.makeText(this, "Please provide QR Name and take a picture", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @Nullable int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            capturedImageView.setImageBitmap(photo);
            imageUri = saveTempImageToUri(photo);
        }
    }

    private Uri saveTempImageToUri(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, UUID.randomUUID().toString() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QRGalleryApp");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                return uri;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveImageToGallery(String qrName) {
        // Save the image URI and name to SharedPreferences
        getSharedPreferences("qr_gallery_prefs", MODE_PRIVATE)
                .edit()
                .putString(UUID.randomUUID().toString(), qrName + "::" + imageUri.toString())
                .apply();

        Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        finish(); // Close the activity
    }
}