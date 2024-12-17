package com.example.finnew;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QRGalleryActivity extends AppCompatActivity {

    private RecyclerView qrRecyclerView;
    private QRAdapter qrAdapter;
    private List<QRData> qrDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_gallery);

        qrRecyclerView = findViewById(R.id.qrRecyclerView);
        qrRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        qrAdapter = new QRAdapter(qrDataList);
        qrRecyclerView.setAdapter(qrAdapter);

        loadQRsFromSharedPreferences();
    }

    private void loadQRsFromSharedPreferences() {
        Map<String, ?> allEntries = getSharedPreferences("qr_gallery_prefs", MODE_PRIVATE).getAll();
        qrDataList.clear();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String value = (String) entry.getValue();
            String[] parts = value.split("::");
            if (parts.length == 2) {
                String qrName = parts[0];
                String imageUrl = parts[1];
                qrDataList.add(new QRData(qrName, imageUrl));
            }
        }

        qrAdapter.notifyDataSetChanged();

        if (qrDataList.isEmpty()) {
            Toast.makeText(this, "No QR data found", Toast.LENGTH_SHORT).show();
        }
    }
}
