package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class wealthLinkMainPage extends BaseActivity {
    private static final String TAG = "WealthLinkMainPage";

    private RecyclerView rvGroups;
    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    LinearLayout accountPage;

    TextView wallet, viewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupDrawer(R.layout.activity_wealth_link_main_page);

        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);
        rvGroups = findViewById(R.id.rvGroups);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        accountPage = navigationView.findViewById(R.id.nav_account);
        viewAll = findViewById(R.id.tvViewAll);

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(wealthLinkMainPage.this, GroupListActivity.class);
                startActivity(intent);
                finish();
            }
        });


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
                                final Map<String, String> groupInvestments = new HashMap<>();

                                for (QueryDocumentSnapshot membershipDocument : task.getResult()) {
                                    // Get the groupID from each membership document
                                    String groupId = membershipDocument.getString("groupID");
                                    if (groupId != null) {
                                        groupIds.add(groupId);

                                        // Store the investment amount for this group
                                        Object investmentAmount = membershipDocument.get("investmentAmount");
                                        if (investmentAmount != null) {
                                            // Format the amount as currency with 2 decimal places
                                            try {
                                                double amount = 0;
                                                if (investmentAmount instanceof Number) {
                                                    amount = ((Number) investmentAmount).doubleValue();
                                                } else {
                                                    amount = Double.parseDouble(investmentAmount.toString());
                                                }
                                                NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
                                                format.setMinimumFractionDigits(2);
                                                format.setMaximumFractionDigits(2);
                                                String formattedAmount = "₱" + format.format(amount);
                                                groupInvestments.put(groupId, formattedAmount);
                                            } catch (NumberFormatException e) {
                                                Log.e(TAG, "Error formatting investment amount", e);
                                                groupInvestments.put(groupId, "₱0.00");
                                            }
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
                                                        Map<String, String> groupNameToIdMap = new HashMap<>();

                                                        for (QueryDocumentSnapshot groupDocument : task.getResult()) {
                                                            try {
                                                                // Extract fields to match the actual Firestore document structure
                                                                String name = groupDocument.getString("groupName");
                                                                String groupId = groupDocument.getId();

                                                                // Get the investmentAmount we stored earlier
                                                                String amount = groupInvestments.containsKey(groupId) ?
                                                                        groupInvestments.get(groupId) : "₱0.00";

                                                                // Get the current time as fallback since there's no "time" field
                                                                String time = java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(new java.util.Date());

                                                                if (name != null) {
                                                                    // Create Group object manually
                                                                    Group group = new Group(name, time, amount);
                                                                    userGroups.add(group);
                                                                    // Store the mapping of group name to group ID
                                                                    groupNameToIdMap.put(name, groupId);
                                                                    Log.d(TAG, "Added group: " + name + " with ID: " + groupId + " and amount: " + amount);
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
                                                                GroupAdapter groupAdapter = new GroupAdapter(userGroups, groupNameToIdMap, wealthLinkMainPage.this);
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
}