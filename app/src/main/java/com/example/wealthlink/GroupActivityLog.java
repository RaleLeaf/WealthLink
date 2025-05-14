package com.example.wealthlink;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class GroupActivityLog extends AppCompatActivity {
    private static final String TAG = "GroupActivityLog";

    private TextView tvGroupName;
    private TextView tvGroupDescription;
    private ImageButton btnBack;
    private String groupID;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_log);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupDescription = findViewById(R.id.tvGroupDescription);
        btnBack = findViewById(R.id.btnBack);

        // Set up back button click listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });

        // Get the group ID from the intent
        if (getIntent() != null && getIntent().hasExtra("groupID")) {
            groupID = getIntent().getStringExtra("groupID");
            if (groupID != null && !groupID.isEmpty()) {
                loadGroupData(groupID);
            } else {
                Log.e(TAG, "Empty group ID provided");
                tvGroupName.setText("Error: Invalid group ID");
                Toast.makeText(this, "Invalid group ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "No group ID provided");
            tvGroupName.setText("Error: No group found");
            Toast.makeText(this, "No group data found", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadGroupData(String groupID) {
        if (groupID == null || groupID.isEmpty()) {
            Log.e(TAG, "Invalid group ID");
            return;
        }

        DocumentReference groupRef = db.collection("groups").document(groupID);
        groupRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Extract group data
                String name = documentSnapshot.getString("groupName");
                String description = documentSnapshot.getString("description");

                // Update UI with group data
                if (name != null) {
                    tvGroupName.setText(name);
                } else {
                    tvGroupName.setText("Unknown Group");
                }

                if (description != null) {
                    tvGroupDescription.setText(description);
                } else {
                    tvGroupDescription.setText("No description available");
                }

                Log.d(TAG, "Group data loaded successfully");
            } else {
                Log.e(TAG, "Group document does not exist");
                tvGroupName.setText("Error: Group not found");
                Toast.makeText(GroupActivityLog.this,
                        "This group no longer exists",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error loading group data", e);
            tvGroupName.setText("Error loading group");
            Toast.makeText(GroupActivityLog.this,
                    "Error connecting to server. Please try again later.",
                    Toast.LENGTH_SHORT).show();
        });
    }
}