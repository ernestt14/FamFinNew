package com.example.finnew;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditExpenseActivity extends AppCompatActivity implements LocationListener {

    private TextView tvCurrentDetails, tvLocation;
    private EditText etAmount, etDescription;
    private Spinner spinnerCategory;
    private TextView tvSelectedDate;
    private Button btnSelectDate, btnSave, btnDelete, btnCaptureLocation;

    private DatabaseReference database;
    private String expenseKey; // Firebase key for the expense
    private String selectedCategory; // Stores selected category
    private String selectedDate; // Stores selected date

    private LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        tvCurrentDetails = findViewById(R.id.tvCurrentDetails);
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnCaptureLocation = findViewById(R.id.btnCaptureLocation);
        tvLocation = findViewById(R.id.tvLocationDetails);  // Assuming a TextView for displaying location

        // Get the Firebase expense key from the intent
        expenseKey = getIntent().getStringExtra("expenseKey");
        if (expenseKey == null || expenseKey.isEmpty()) {
            Toast.makeText(this, "Invalid expense", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if user is authenticated
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get the user ID and reference to the specific expense
        String uid = user.getUid();
        database = FirebaseDatabase.getInstance().getReference("expenses").child(uid).child(expenseKey);

        // Load current expense details from Firebase
        loadExpenseDetails();

        // Set up the category spinner
        setupCategorySpinner();

        // Select date functionality
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Capture Location Button
        btnCaptureLocation.setOnClickListener(v -> captureLocation());

        // Save button functionality
        btnSave.setOnClickListener(v -> saveExpense());

        // Delete button functionality
        btnDelete.setOnClickListener(v -> confirmDeleteExpense());
    }

    private void loadExpenseDetails() {
        database.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot snapshot = task.getResult();
                String amount = snapshot.child("amount").getValue(String.class);
                String category = snapshot.child("category").getValue(String.class);
                String date = snapshot.child("date").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);
                Double latitude = snapshot.child("latitude").getValue(Double.class);
                Double longitude = snapshot.child("longitude").getValue(Double.class);

                // Show current expense details in the TextView
                tvCurrentDetails.setText(
                        "Current Details:\n" +
                                "Amount: " + amount + "\n" +
                                "Category: " + category + "\n" +
                                "Date: " + date + "\n" +
                                "Description: " + description + "\n" +
                                "Location: Lat: " + latitude + ", Lon: " + longitude
                );

                // Pre-fill the edit fields with the current data
                etAmount.setText(amount);
                etDescription.setText(description);
                tvSelectedDate.setText(date);
                selectedDate = date;

                // Set the spinner to the current category
                ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
                int position = adapter.getPosition(category);
                spinnerCategory.setSelection(position);
            } else {
                Toast.makeText(this, "Failed to load expense details", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    private void setupCategorySpinner() {
        String[] categories = {"Food", "Transport", "Shopping", "Bills", "Entertainment", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Listener for spinner selection
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "Others"; // Default category
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            tvSelectedDate.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void captureLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Request location updates
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);

            if (provider != null) {
                locationManager.requestLocationUpdates(provider, 0, 0, this);
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        tvLocation.setText("Latitude: " + latitude + ", Longitude: " + longitude);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}

    private void saveExpense() {
        String amount = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (amount.isEmpty()) {
            etAmount.setError("Amount is required");
            etAmount.requestFocus();
            return;
        }

        if (selectedDate == null || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare updated expense data
        Map<String, Object> updatedExpense = new HashMap<>();
        updatedExpense.put("amount", amount);
        updatedExpense.put("category", selectedCategory);
        updatedExpense.put("date", selectedDate);
        updatedExpense.put("description", description);

        // Update the expense in Firebase
        database.updateChildren(updatedExpense).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditExpenseActivity.this, "Expense updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditExpenseActivity.this, "Failed to update expense", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteExpense() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Yes", (dialog, which) -> deleteExpense())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteExpense() {
        database.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditExpenseActivity.this, "Expense deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditExpenseActivity.this, "Failed to delete expense", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureLocation();
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
