package com.example.wealthlink;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GroupDetails extends AppCompatActivity {

    private static final String TAG = "GroupDetails";

    Button btnDeposit, btnWithdraw;
    ImageButton btnBack, btnMore;
    private RecyclerView recyclerActivities;
    private GroupActivityAdapter activityAdapter;
    private List<GroupActivityModel> activityList;
    TextView tvGroupName, tvGroupDescription, tvTotalInvestment, tvMemberCount, tvUserInvestment;

    // Dropdown menu elements
    private CardView cardDropdown;
    private boolean isDropdownVisible = false;
    private TextView tvSettings, tvReportIssue, tvLeaveGroup;

    private String groupID;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Define the progress dialog
    private AlertDialog progressDialog;

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
        tvTotalInvestment = findViewById(R.id.tvTotalInvestment);
        tvMemberCount = findViewById(R.id.tvMemberCount);
        tvUserInvestment = findViewById(R.id.tvUserInvestment);

        btnDeposit = findViewById(R.id.btnDeposit);
        btnWithdraw = findViewById(R.id.btnWithdraw);
        btnBack = findViewById(R.id.btnBack);
        btnMore = findViewById(R.id.btnMore);

        // Initialize dropdown components
        cardDropdown = findViewById(R.id.cardDropdown);
        cardDropdown.setVisibility(View.GONE); // Ensure it's initially hidden

        tvSettings = findViewById(R.id.tvSettings);
        tvReportIssue = findViewById(R.id.tvReportIssue);
        tvLeaveGroup = findViewById(R.id.tvLeaveGroup);

        // Set up RecyclerView
        recyclerActivities = findViewById(R.id.recyclerActivities);
        recyclerActivities.setLayoutManager(new LinearLayoutManager(this));

        // Add divider between items
        recyclerActivities.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Initialize adapter with empty list
        activityList = new ArrayList<>();
        activityAdapter = new GroupActivityAdapter(activityList);
        recyclerActivities.setAdapter(activityAdapter);

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

        // Set up dropdown menu click listeners
        btnMore.setOnClickListener(v -> toggleDropdown());

        tvSettings.setOnClickListener(v -> {
            // Handle settings click
            Intent intent = new Intent(GroupDetails.this, GroupSettings.class);
            startActivity(intent);
            Toast.makeText(GroupDetails.this, "Settings clicked", Toast.LENGTH_SHORT).show();
            cardDropdown.setVisibility(View.GONE);
            isDropdownVisible = false;
        });

        tvReportIssue.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetails.this, ReportIssue.class);
            startActivity(intent);
            cardDropdown.setVisibility(View.GONE);
            isDropdownVisible = false;
        });

        tvLeaveGroup.setOnClickListener(v -> {
            // Show confirmation dialog before leaving the group
            new AlertDialog.Builder(GroupDetails.this)
                    .setTitle("Leave Group")
                    .setMessage("Are you sure you want to leave this group? Your investment amount will remain in the group unless withdrawn first.")
                    .setPositiveButton("Leave", (dialog, which) -> {
                        // Show loading indicator
                        showProgressDialog("Leaving group...");

                        // Get current user ID
                        String userID = currentUser.getUid();

                        // Query to find the specific membership document
                        db.collection("groupMemberships")
                                .whereEqualTo("groupID", groupID)
                                .whereEqualTo("userID", userID)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        // Get the document ID of the membership record
                                        String membershipDocID = querySnapshot.getDocuments().get(0).getId();

                                        // Delete the membership document
                                        db.collection("groupMemberships").document(membershipDocID)
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    hideProgressDialog();
                                                    Toast.makeText(GroupDetails.this, "You have left the group", Toast.LENGTH_SHORT).show();

                                                    // Return to the previous activity (likely the groups list)
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    hideProgressDialog();
                                                    Log.e(TAG, "Error leaving group", e);
                                                    Toast.makeText(GroupDetails.this, "Failed to leave group: " + e.getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        // No membership found
                                        hideProgressDialog();
                                        Log.e(TAG, "No membership found for this user in this group");
                                        Toast.makeText(GroupDetails.this, "You are not a member of this group",
                                                Toast.LENGTH_SHORT).show();
                                        finish(); // Return to previous screen anyway
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    hideProgressDialog();
                                    Log.e(TAG, "Error querying group membership", e);
                                    Toast.makeText(GroupDetails.this, "Error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            // Close the dropdown menu
            cardDropdown.setVisibility(View.GONE);
            isDropdownVisible = false;
        });

        // Get the group ID from the intent
        if (getIntent().hasExtra("groupID")) {
            groupID = getIntent().getStringExtra("groupID");
            loadGroupData(groupID);
            if (currentUser != null) {
                loadUserInvestmentData(groupID, currentUser.getUid());
            }
            // Load group activity data
            loadGroupActivities(groupID);
        } else {
            Log.e(TAG, "No group ID provided");
            tvGroupName.setText("Error: No group found");
        }
    }

    /**
     * Shows a progress dialog with a custom message
     */
    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.dialog_progress, null);
            TextView tvMessage = view.findViewById(R.id.tvProgressMessage);
            tvMessage.setText(message);
            builder.setView(view);
            builder.setCancelable(false);
            progressDialog = builder.create();
        }
        progressDialog.show();
    }

    /**
     * Hides the progress dialog if it's showing
     */
    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
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
            // Refresh group activities data
            loadGroupActivities(groupID);
        }
    }

    // Toggle dropdown visibility
    private void toggleDropdown() {
        isDropdownVisible = !isDropdownVisible;
        cardDropdown.setVisibility(isDropdownVisible ? View.VISIBLE : View.GONE);
    }

    // Handle clicks outside the dropdown to dismiss it
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isDropdownVisible && event.getAction() == MotionEvent.ACTION_DOWN) {
            Rect dropdownRect = new Rect();
            cardDropdown.getGlobalVisibleRect(dropdownRect);

            if (!dropdownRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                cardDropdown.setVisibility(View.GONE);
                isDropdownVisible = false;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void loadGroupActivities(String groupID) {
        if (groupID == null || groupID.isEmpty()) {
            Log.e(TAG, "Invalid group ID for loading activities");
            return;
        }

        // Create a new list each time we load activities
        activityList = new ArrayList<>();

        // Show loading state
        showProgressDialog("Loading group activities...");

        // Query transactions collection for records related to this group
        db.collection("transactions")
                .whereEqualTo("groupID", groupID)
                .orderBy("transactionDate", Query.Direction.DESCENDING)
                .limit(20) // Limit to most recent 20 transactions
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // If no transactions found, show a placeholder message
                            Log.d(TAG, "No activity found for this group");
                            activityList.add(new GroupActivityModel(
                                    "No Activity",
                                    "N/A",
                                    "No recent transactions",
                                    "₱0.00",
                                    false));

                            // Update adapter with empty state
                            activityAdapter.updateActivities(activityList);
                            hideProgressDialog();
                        } else {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                            // First, create all transaction items with placeholder names
                            // This ensures we have a complete list before updating the adapter
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get transaction data
                                String transactionType = document.getString("transactionType");
                                String userID = document.getString("userID");
                                Object amountObj = document.get("netAmount");
                                Date transactionDate = document.getDate("transactionDate");

                                // Process amount
                                double amount = 0.0;
                                if (amountObj instanceof Number) {
                                    amount = ((Number) amountObj).doubleValue();
                                } else if (amountObj instanceof String) {
                                    try {
                                        amount = Double.parseDouble((String) amountObj);
                                    } catch (NumberFormatException e) {
                                        Log.e(TAG, "Error parsing amount", e);
                                    }
                                }

                                // Format amount as currency
                                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                                String formattedAmount = currencyFormat.format(amount);

                                // Format date
                                String formattedDate = transactionDate != null
                                        ? dateFormat.format(transactionDate)
                                        : "Unknown date";

                                // Determine if this is a withdrawal or deposit
                                boolean isWithdrawal = "withdraw".equalsIgnoreCase(transactionType);

                                // Get the transaction title
                                final String transactionTitle = transactionType != null
                                        ? transactionType.substring(0, 1).toUpperCase() + transactionType.substring(1)
                                        : "Transaction";

                                // Add to our list with initial "Member" placeholder (we'll update names later)
                                activityList.add(new GroupActivityModel(
                                        transactionTitle,
                                        "Member",
                                        formattedDate,
                                        formattedAmount,
                                        isWithdrawal
                                ));
                            }

                            // Update the adapter with initial data
                            activityAdapter.updateActivities(activityList);
                            hideProgressDialog();

                            // Now try to look up user names asynchronously
                            // (This happens after initial display, so app won't seem frozen)
                            lookupUserNames();
                        }
                    } else {
                        Log.e(TAG, "Error loading group activities", task.getException());
                        activityList.add(new GroupActivityModel(
                                "Error",
                                "Failed to load",
                                "Please try again later",
                                "₱0.00",
                                false));
                        activityAdapter.updateActivities(activityList);
                        hideProgressDialog();
                    }
                });
    }

    // Lookup user names after transactions are loaded
    private void lookupUserNames() {
        // We re-query to get the transaction data with user IDs
        db.collection("transactions")
                .whereEqualTo("groupID", groupID)
                .orderBy("transactionDate", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        int index = 0;
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            final int currentIndex = index;
                            String userID = document.getString("userID");

                            if (userID != null && !userID.isEmpty() && currentIndex < activityList.size()) {
                                db.collection("users").document(userID)
                                        .get()
                                        .addOnSuccessListener(userDoc -> {
                                            if (userDoc.exists() && currentIndex < activityList.size()) {
                                                String firstName = userDoc.getString("firstName");
                                                String lastName = userDoc.getString("lastName");
                                                String username = "Member";

                                                if (firstName != null && !firstName.isEmpty()) {
                                                    username = firstName;
                                                    if (lastName != null && !lastName.isEmpty()) {
                                                        username += " " + lastName.charAt(0) + ".";
                                                    }
                                                }

                                                // Create updated activity with real username
                                                GroupActivityModel currentActivity = activityList.get(currentIndex);
                                                GroupActivityModel updatedActivity = new GroupActivityModel(
                                                        currentActivity.getTitle(),
                                                        username,
                                                        currentActivity.getDate(),
                                                        currentActivity.getAmount(),
                                                        currentActivity.isWithdrawal()
                                                );

                                                // Update the list and notify adapter of change
                                                activityList.set(currentIndex, updatedActivity);
                                                activityAdapter.notifyItemChanged(currentIndex);
                                            }
                                        });
                            }
                            index++;
                        }
                    }
                });
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
                                    NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                                    String formattedAmount = format.format(amount);
                                    tvUserInvestment.setText(formattedAmount);
                                } else {
                                    Log.w(TAG, "tvUserInvestment is null - make sure it exists in your layout");
                                }
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "Error parsing investment amount", e);
                                if (tvUserInvestment != null) {
                                    tvUserInvestment.setText("Unknown");
                                }
                            }
                        } else {
                            if (tvUserInvestment != null) {
                                tvUserInvestment.setText("₱0.00");
                            }
                        }
                    } else {
                        if (tvUserInvestment != null) {
                            tvUserInvestment.setText("₱0.00");
                        } else {
                            Log.w(TAG, "tvUserInvestment is null - make sure it exists in your layout");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user investment data", e);
                    if (tvUserInvestment != null) {
                        tvUserInvestment.setText("Error");
                    }
                });
    }
}