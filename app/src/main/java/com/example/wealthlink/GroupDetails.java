package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Locale;

public class GroupDetails extends AppCompatActivity {
    private static final String TAG = "GroupDetails";

    Button btnDeposit, btnWithdraw;
    ImageButton btnBack;
    TextView tvGroupName, tvGroupDescription, tvTotalInvestment, tvMemberCount, tvUserInvestment;

    private String groupID;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_details);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize UI components
        tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupDescription = findViewById(R.id.tvGroupDescription);

        // Uncomment these lines to properly initialize the TextViews
        tvTotalInvestment = findViewById(R.id.tvTotalInvestment);
        tvMemberCount = findViewById(R.id.tvMemberCount);
        tvUserInvestment = findViewById(R.id.tvUserInvestment);

        btnDeposit = findViewById(R.id.btnDeposit);
        btnWithdraw = findViewById(R.id.btnWithdraw);
        btnBack = findViewById(R.id.btnBack);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up button click listeners
        btnBack.setOnClickListener(v -> {
            finish(); // Go back to previous activity
        });

        btnWithdraw.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetails.this, WithdrawAmount.class);
            intent.putExtra("groupID", groupID);
            startActivity(intent);
        });

        btnDeposit.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetails.this, DepositAmount.class);
            intent.putExtra("groupID", groupID);
            startActivity(intent);
        });

        // Get the group ID from the intent
        if (getIntent().hasExtra("groupID")) {
            groupID = getIntent().getStringExtra("groupID");
            loadGroupData(groupID);
            if (currentUser != null) {
                loadUserInvestmentData(groupID, currentUser.getUid());
            }
        } else {
            Log.e(TAG, "No group ID provided");
            tvGroupName.setText("Error: No group found");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        if (groupID != null && !groupID.isEmpty()) {
            loadGroupData(groupID);
            if (currentUser != null) {
                loadUserInvestmentData(groupID, currentUser.getUid());
            }
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

                            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
                            String formattedAmount = format.format(totalInvestment);
                            tvTotalInvestment.setText("" + formattedAmount);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error parsing total investment", e);
                            tvTotalInvestment.setText("Unknown");
                        }
                    } else {
                        tvTotalInvestment.setText("Php 0.00");
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
                                tvMemberCount.setText("" + memberCount);
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

    private void loadUserInvestmentData(String groupID, String userID) {
        db.collection("groupMemberships")
                .whereEqualTo("groupID", groupID)
                .whereEqualTo("userID", userID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Object investmentAmount = querySnapshot.getDocuments().get(0).get("investmentAmount");

                        if (investmentAmount != null) {
                            try {
                                double amount = 0;
                                if (investmentAmount instanceof Number) {
                                    amount = ((Number) investmentAmount).doubleValue();
                                } else if (investmentAmount instanceof String) {
                                    String valueStr = (String) investmentAmount;
                                    if (!valueStr.isEmpty()) {
                                        amount = Double.parseDouble(valueStr);
                                    }
                                }

                                if (tvUserInvestment != null) {
                                    NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
                                    String formattedAmount = format.format(amount);
                                    tvUserInvestment.setText("" + formattedAmount);
                                } else {
                                    Log.w(TAG, "tvUserInvestment is null - make sure it exists in your layout");
                                }
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "Error parsing investment amount", e);
                                if (tvUserInvestment != null) {
                                    tvUserInvestment.setText("Your Investment: Unknown");
                                }
                            }
                        } else {
                            if (tvUserInvestment != null) {
                                tvUserInvestment.setText("Your Investment: Php 0.00");
                            }
                        }
                    } else {
                        if (tvUserInvestment != null) {
                            tvUserInvestment.setText("Your Investment: Php 0.00");
                        } else {
                            Log.w(TAG, "tvUserInvestment is null - make sure it exists in your layout");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user investment data", e);
                    if (tvUserInvestment != null) {
                        tvUserInvestment.setText("Your Investment: Error");
                    }
                });
    }
}