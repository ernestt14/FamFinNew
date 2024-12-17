package com.example.finnew;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EnterExpenseActivity extends AppCompatActivity implements LocationListener {
    private static final int REQUEST_LOCATION_PERMISSION = 300;

    private EditText etAmount, etDescription;
    private Spinner spinnerCategory;
    private TextView tvSelectedDate, tvLocation;
    private Button btnSave, btnSelectDate, btnCaptureLocation;

    private DatabaseReference databaseReference;
    private String selectedDate = "";
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_expense);

        // Initialize UI elements
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvLocation = findViewById(R.id.tvLocation);
        btnSave = findViewById(R.id.btnSave);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnCaptureLocation = findViewById(R.id.btnCaptureLocation);

        // Initialize Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(uid);

        // Category Spinner Setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Date Picker
        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());

        // Capture Location Button
        btnCaptureLocation.setOnClickListener(v -> captureLocation());

        // Save Expense
        btnSave.setOnClickListener(v -> saveExpenseToFirebase());

        // Check for location permission
        checkLocationPermission();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    tvSelectedDate.setText(selectedDate);
                }, year, month, day);
        datePicker.show();
    }

    private void captureLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Get the best available provider
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);

        // Get location updates
        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(provider, 0, 0, this);
            } else {
                checkLocationPermission();
            }
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void saveExpenseToFirebase() {
        String amount = etAmount.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String description = etDescription.getText().toString();

        if (amount.isEmpty() || category.isEmpty() || description.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String expenseId = databaseReference.push().getKey();
        if (expenseId != null) {
            Expense expense = new Expense(amount, category, description, selectedDate, latitude, longitude);
            databaseReference.child(expenseId).setValue(expense)
                    .addOnSuccessListener(aVoid -> {
                        // Show success toast
                        Toast.makeText(this, "Expense saved successfully", Toast.LENGTH_SHORT).show();
                        // Navigate to HomeActivity
                        Intent intent = new Intent(EnterExpenseActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); // Optionally finish EnterExpenseActivity to prevent back navigation
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        tvLocation.setText("Lat: " + latitude + ", Lon: " + longitude);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}
}
