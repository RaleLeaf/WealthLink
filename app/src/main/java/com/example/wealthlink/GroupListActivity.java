package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupListActivity extends BaseActivity {
    private static final String TAG = "GroupListActivity";
    Button joinGroup, createGroup;
    private RecyclerView rvGroups;
    private NavigationView navigationView;
    private EditText etSearch;
    private GroupAdapter groupAdapter;
    private List<Group> allGroups = new ArrayList<>();
    private Map<String, String> groupNameToIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupDrawer(R.layout.activity_group_list);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        navigationView = findViewById(R.id.navigation_view);
        LinearLayout accountPage = navigationView.findViewById(R.id.nav_account);
        rvGroups = findViewById(R.id.rvGroups);
        joinGroup = findViewById(R.id.btnJoinViaInvite);
        createGroup = findViewById(R.id.btnCreateGroup);
        etSearch = findViewById(R.id.etSearch);

        // Set up menu button click listener
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set up account page navigation
        accountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAccount = new Intent(GroupListActivity.this, AccountView.class);
                startActivity(viewAccount);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Set up join group button click listener
        joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jintent = new Intent(GroupListActivity.this, JoinGroupActivity.class);
                startActivity(jintent);
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cintent = new Intent(GroupListActivity.this, CreateGroupPortfolio.class);
                startActivity(cintent);
                // Removed finish() to prevent activity stack issues
            }
        });

        // Set up RecyclerView
        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter = new GroupAdapter(allGroups, groupNameToIdMap, this);
        rvGroups.setAdapter(groupAdapter);

        // Set up search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the list based on search query
                groupAdapter.filter(s.toString());
                Log.d(TAG, "Filtering with query: " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        // Fetch groups data
        fetchGroups();
    }

    /**
     * Fetch all groups from Firestore
     */
    private void fetchGroups() {
        // Initialize Firebase Auth and Firestore
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Clear previous data
        allGroups.clear();
        groupNameToIdMap.clear();

        // Fetch all groups from the Firestore "groups" collection
        db.collection("groups")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot groupDocument : task.getResult()) {
                                try {
                                    // Extract group name from document
                                    String name = groupDocument.getString("groupName");
                                    String groupId = groupDocument.getId();

                                    // Format the totalInvestment with exactly two decimal places
                                    String amount = "₱0.00"; // Default placeholder with 2 decimal places

                                    // Check if there's a totalInvestment field
                                    Object totalInvestment = groupDocument.get("totalInvestment");
                                    if (totalInvestment != null) {
                                        try {
                                            double investmentValue = 0;
                                            if (totalInvestment instanceof Number) {
                                                investmentValue = ((Number) totalInvestment).doubleValue();
                                            } else {
                                                investmentValue = Double.parseDouble(totalInvestment.toString());
                                            }

                                            // Format with 2 decimal places
                                            NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
                                            format.setMinimumFractionDigits(2);
                                            format.setMaximumFractionDigits(2);
                                            amount = "₱" + format.format(investmentValue);
                                        } catch (NumberFormatException e) {
                                            Log.e(TAG, "Error parsing totalInvestment", e);
                                        }
                                    }

                                    // Get current time as group time
                                    String time = java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT)
                                            .format(new java.util.Date());

                                    if (name != null) {
                                        // Create Group object
                                        Group group = new Group(name, time, amount);
                                        allGroups.add(group);
                                        // Store the mapping of group name to group ID
                                        groupNameToIdMap.put(name, groupId);
                                        Log.d(TAG, "Added group: " + name + " with ID: " + groupId);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing group document", e);
                                }
                            }

                            // Update UI on the main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Notify adapter of data change
                                    groupAdapter = new GroupAdapter(allGroups, groupNameToIdMap, GroupListActivity.this);
                                    rvGroups.setAdapter(groupAdapter);

                                    Log.d(TAG, "Successfully retrieved all groups: " + allGroups.size());

                                    // Handle empty state
                                    if (allGroups.isEmpty()) {
                                        Log.d(TAG, "No groups found in the database.");
                                        Toast.makeText(GroupListActivity.this,
                                                "No groups available",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    // Apply any existing search filter
                                    if (etSearch != null && etSearch.getText().length() > 0) {
                                        groupAdapter.filter(etSearch.getText().toString());
                                    }
                                }
                            });
                        } else {
                            Log.e(TAG, "Error getting groups: ", task.getException());
                            Toast.makeText(GroupListActivity.this,
                                    "Error loading groups. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}