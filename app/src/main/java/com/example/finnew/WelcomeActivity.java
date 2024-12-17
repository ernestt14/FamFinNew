package com.example.finnew;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    // SharedPreferences File Name
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_WELCOME_SHOWN = "welcome_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if Welcome Screen was already shown
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isWelcomeShown = prefs.getBoolean(KEY_WELCOME_SHOWN, false);

        if (isWelcomeShown) {
            // Redirect to MainActivity if already shown
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Show Welcome Page
        setContentView(R.layout.activity_welcome);

        // Find the Get Started TextView
        TextView startButton = findViewById(R.id.textDone);

        // Add a click listener
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the preference to mark the welcome screen as shown
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_WELCOME_SHOWN, true);
                editor.apply();

                // Navigate to MainActivity
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);

                // Close the current WelcomeActivity
                finish();
            }
        });
    }
}
