package com.example.finnew;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.Map;

public class SeeExpensesActivity extends AppCompatActivity {

    private ListView lvExpenses;
    private ArrayList<Expense> expenseList;
    private ArrayList<String> expenseKeys;    // Store Firebase keys for each expense
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_expenses);

        lvExpenses = findViewById(R.id.lvExpenses);
        expenseList = new ArrayList<>();
        expenseKeys = new ArrayList<>();

        // Check if user is authenticated
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(SeeExpensesActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity if the user is not authenticated
            return;
        }

        // Get the user ID
        String uid = user.getUid();
        // Set the database reference to the user's expenses
        database = FirebaseDatabase.getInstance().getReference("expenses").child(uid);

        // Retrieve expenses from Firebase Realtime Database
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expenseList.clear();  // Clear old data
                expenseKeys.clear();   // Clear old keys

                // Loop through each expense data from Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the Firebase key for the expense
                    String key = snapshot.getKey();
                    expenseKeys.add(key);  // Add the key to the list

                    Map<String, Object> expenseData = (Map<String, Object>) snapshot.getValue();
                    String amount = (String) expenseData.get("amount");
                    String category = (String) expenseData.get("category");
                    String date = (String) expenseData.get("date");
                    double latitude = (double) expenseData.get("latitude");
                    double longitude = (double) expenseData.get("longitude");

                    // Create an Expense object
                    Expense expense = new Expense(amount, category, "", date, latitude, longitude);
                    expenseList.add(expense);  // Add the expense to the list
                }

                // Now, pass the list of Expense objects to the adapter
                ExpenseAdapter adapter = new ExpenseAdapter(SeeExpensesActivity.this, expenseList);
                lvExpenses.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SeeExpensesActivity.this, "Failed to load expenses: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up click listener for ListView items
        lvExpenses.setOnItemClickListener((parent, view, position, id) -> {
            // Get the key for the selected expense
            String selectedExpenseKey = expenseKeys.get(position);

            // Launch the EditExpenseActivity
            Intent intent = new Intent(SeeExpensesActivity.this, EditExpenseActivity.class);
            intent.putExtra("expenseKey", selectedExpenseKey);  // Pass the Firebase key
            startActivity(intent);
        });
    }
}
