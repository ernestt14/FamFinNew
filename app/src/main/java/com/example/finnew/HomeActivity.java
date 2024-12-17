package com.example.finnew;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        Button btnEnterExpenses = findViewById(R.id.btnEnterExpenses);
        Button btnSeeExpenses = findViewById(R.id.btnSeeExpenses);

        if (user != null) {
            tvWelcome.setText("Welcome back, " + user.getEmail() + "!");
        }

        btnEnterExpenses.setOnClickListener(v -> startActivity(new Intent(this, EnterExpenseActivity.class)));
        btnSeeExpenses.setOnClickListener(v -> startActivity(new Intent(this, SeeExpensesActivity.class)));

        Button goToCameraButton = findViewById(R.id.goToCameraButton);
        Button goToQRGalleryButton = findViewById(R.id.goToQRGalleryButton);

        // Button for Camera Activity
        goToCameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CameraActivity.class);
            startActivity(intent);
        });

        // Button for QR Gallery Activity
        goToQRGalleryButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, QRGalleryActivity.class);
            startActivity(intent);
        });
    }
}