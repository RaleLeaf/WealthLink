package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wealthlink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.util.Log;
import com.google.firebase.firestore.FieldPath;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class wealthLinkMainPage extends BaseActivity {
    private static final String TAG = "WealthLinkMainPage";

    private RecyclerView rvGroups;
    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    LinearLayout accountPage;

    TextView wallet;
    LinearLayout llWithdraw, llDeposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupDrawer(R.layout.activity_wealth_link_main_page);

        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);
        rvGroups = findViewById(R.id.rvGroups);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        accountPage = navigationView.findViewById(R.id.nav_account);
        llWithdraw = findViewById(R.id.llWithdraw);
        llDeposit = findViewById(R.id.llDeposit);

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        accountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAccount = new Intent(wealthLinkMainPage.this, AccountView.class);
                startActivity(viewAccount);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Add click listener for withdraw button
        llWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(wealthLinkMainPage.this, WithdrawAccount.class);
                startActivity(intent);
            }
        });
        llDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(wealthLinkMainPage.this, DepositAccount.class);
                startActivity(intent);
            }
        });

        wallet = findViewById(R.id.tvWalletAmount);
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Initialize Cloud Firestore
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String balanceStr = documentSnapshot.getString("balance");

                if (balanceStr != null) {
                    try {
                        double balance = Double.parseDouble(balanceStr);
                        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
                        format.setMinimumFractionDigits(2);
                        format.setMaximumFractionDigits(2);

                        String formattedBalance = "Php " + format.format(balance);
                        wallet.setText(formattedBalance);
                    } catch (NumberFormatException e) {
                        wallet.setText("Error");
                    }
                } else {
                    wallet.setText("Error");
                }
            } else {
                // Document does not exist
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
        });

        // GROUPS THE USER IS PART OF
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // 1. Query "groupMemberships" for the user's memberships
            db.collection("groupMemberships")
                    .whereEqualTo("userID", currentUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // Store groupIds and their respective investmentAmounts
                                final List<String> groupIds = new ArrayList<>();
                                final java.util.Map<String, String> groupInvestments = new java.util.HashMap<>();

                                for (QueryDocumentSnapshot membershipDocument : task.getResult()) {
                                    // Get the groupID from each membership document
                                    String groupId = membershipDocument.getString("groupID");
                                    if (groupId != null) {
                                        groupIds.add(groupId);

                                        // Store the investment amount for this group
                                        Object investmentAmount = membershipDocument.get("investmentAmount");
                                        if (investmentAmount != null) {
                                            // Format the amount as currency
                                            String formattedAmount = "$" + investmentAmount.toString();
                                            groupInvestments.put(groupId, formattedAmount);
                                        }
                                    }
                                }

                                Log.d(TAG, "Group IDs found: " + groupIds.size() + " - " + groupIds);

                                // 2. Get the "group" documents based on the groupIDs
                                if (!groupIds.isEmpty()) {
                                    // Firestore allows querying by multiple values in an array using whereIn
                                    db.collection("groups")
                                            .whereIn(FieldPath.documentId(), groupIds) // Query by document ID
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        List<Group> userGroups = new ArrayList<>();
                                                        for (QueryDocumentSnapshot groupDocument : task.getResult()) {
                                                            try {
                                                                // Extract fields to match the actual Firestore document structure
                                                                String name = groupDocument.getString("groupName"); // Changed from "name" to "groupName"
                                                                String groupId = groupDocument.getId();

                                                                // Get the investmentAmount we stored earlier
                                                                String amount = groupInvestments.containsKey(groupId) ?
                                                                        groupInvestments.get(groupId) : "$0";

                                                                // Get the current time as fallback since there's no "time" field
                                                                String time = java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(new java.util.Date());

                                                                if (name != null) {
                                                                    // Create Group object manually
                                                                    Group group = new Group(name, time, amount);
                                                                    userGroups.add(group);
                                                                    Log.d(TAG, "Added group: " + name + " with amount: " + amount);
                                                                }
                                                            } catch (Exception e) {
                                                                Log.e(TAG, "Error parsing group document", e);
                                                            }
                                                        }

                                                        // 3. Populate the RecyclerView
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                // Set up the RecyclerView with the retrieved groups
                                                                rvGroups.setLayoutManager(new LinearLayoutManager(wealthLinkMainPage.this));
                                                                GroupAdapter groupAdapter = new GroupAdapter(userGroups);
                                                                rvGroups.setAdapter(groupAdapter);

                                                                Log.d(TAG, "Successfully retrieved user's groups: " + userGroups.size());

                                                                // If no groups were found, display dummy data
                                                                if (userGroups.isEmpty()) {
                                                                    Log.e(TAG, "User has not joined any groups.", task.getException());
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Log.e(TAG, "Error getting user's groups: ", task.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    // The user is not a member of any groups
                                    Log.d(TAG, "User is not a member of any groups.");
                                }

                            } else {
                                Log.e(TAG, "Error getting group memberships: ", task.getException());
                            }
                        }
                    });
        } else {
            Log.e(TAG, "No current user found");
        }
    }

    // Method to show the withdraw popup
    private void showWithdrawPopup() {
        // Create bottom sheet dialog
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Set this flag to make it expand fully
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        // Force expanded mode at all times - prevents user from dragging it down
        bottomSheetDialog.getBehavior().setSkipCollapsed(true);

        View bottomSheetView = getLayoutInflater().inflate(R.layout.withdraw_popup, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Find views in the bottom sheet
        TextView cashoutOption = bottomSheetView.findViewById(R.id.cashout_option);
        TextView depositOption = bottomSheetView.findViewById(R.id.deposit_option);

        // Set click listeners for options
        cashoutOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle cashout option click
                // Add your cashout logic here
                bottomSheetDialog.dismiss();
            }
        });

        depositOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle deposit option click
                // Add your deposit logic here
                bottomSheetDialog.dismiss();
            }
        });

        // Add callback to force expanded state when dialog is shown
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        });

        // Show the bottom sheet
        bottomSheetDialog.show();
    }
}