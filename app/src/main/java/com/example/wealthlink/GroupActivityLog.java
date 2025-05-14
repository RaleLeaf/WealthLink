package com.example.wealthlink;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Locale;

public class GroupActivityLog extends AppCompatActivity {
    private static final String TAG = "GroupActivityLog";

    private TextView tvGroupName;
    private TextView tvGroupDescription, tvTotalInvestment, tvMemberCount;
    private ImageButton btnBack;
    private String groupID;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private Button btnJoinGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_log);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize UI components
        tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupDescription = findViewById(R.id.tvGroupDescription);
        tvTotalInvestment = findViewById(R.id.tvTotalInvestment);
        tvMemberCount = findViewById(R.id.tvMemberCount);
        btnBack = findViewById(R.id.btnBack);
        btnJoinGroup = findViewById(R.id.btnJoinGroup);

        // Set up back button click listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });

        btnJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                Object totalInvestmentObj = documentSnapshot.get("totalInvestment");

                // Update UI with group data
                if (name != null) {
                    tvGroupName.setText(name);
                }

                if (description != null) {
                    tvGroupDescription.setText(description);
                } else {
                    tvGroupDescription.setText("No description available");
                }

                // Format and display total investment
                if (tvTotalInvestment != null) {
                    if (totalInvestmentObj != null) {
                        try {
                            double totalInvestment = 0;
                            if (totalInvestmentObj instanceof Number) {
                                totalInvestment = ((Number) totalInvestmentObj).doubleValue();
                            } else if (totalInvestmentObj instanceof String) {
                                String valueStr = (String) totalInvestmentObj;
                                if (!valueStr.isEmpty()) {
                                    totalInvestment = Double.parseDouble(valueStr);
                                }
                            }

                            NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
                            String formattedAmount = "₱" + format.format(totalInvestment);
                            tvTotalInvestment.setText(formattedAmount);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error parsing total investment", e);
                            tvTotalInvestment.setText("Unknown");
                        }
                    } else {
                        tvTotalInvestment.setText("₱0.00");
                    }
                } else {
                    Log.w(TAG, "tvTotalInvestment is null - make sure it exists in your layout");
                }

                // Count number of members in this group
                if (tvMemberCount != null) {
                    db.collection("groupMemberships")
                            .whereEqualTo("groupID", groupID)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                int memberCount = querySnapshot.size();
                                tvMemberCount.setText(String.valueOf(memberCount));
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error counting members", e);
                                tvMemberCount.setText("Unknown");
                            });
                } else {
                    Log.w(TAG, "tvMemberCount is null - make sure it exists in your layout");
                }

                Log.d(TAG, "Group data loaded successfully");
            } else {
                Log.e(TAG, "Group document does not exist");
                tvGroupName.setText("Error: Group not found");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error loading group data", e);
            tvGroupName.setText("Error loading group");
        });
    }
}