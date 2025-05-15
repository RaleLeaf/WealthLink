package com.example.wealthlink;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
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
    private ImageButton btnBack, btnMore,menuButton;
    private Button btnJoinGroup;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Dropdown menu elements
    private CardView cardDropdown;
    private boolean isDropdownVisible = false;
    private TextView tvSettings, tvReportIssue, tvLeaveGroup;

    private String groupID;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_log);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Initialize UI components
        initializeViews();

        // Set up click listeners
        setupClickListeners();

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

    private void initializeViews() {
        tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupDescription = findViewById(R.id.tvGroupDescription);
        tvTotalInvestment = findViewById(R.id.tvTotalInvestment);
        tvMemberCount = findViewById(R.id.tvMemberCount);
        btnBack = findViewById(R.id.btnBack);
        btnMore = findViewById(R.id.btnMore);
        btnJoinGroup = findViewById(R.id.btnJoinGroup);
        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);
        menuButton = findViewById(R.id.btnHamburger); // Replace with your actual button ID
        if (menuButton != null) {
            menuButton.setOnClickListener(v -> {
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
        setupDrawer();

        // Initialize dropdown menu elements
        cardDropdown = findViewById(R.id.cardDropdown);
        tvSettings = findViewById(R.id.tvSettings);
        tvReportIssue = findViewById(R.id.tvReportIssue);
        tvLeaveGroup = findViewById(R.id.tvLeaveGroup);
    }

    private void setupClickListeners() {
        // Set up back button click listener
        btnBack.setOnClickListener(v -> finish()); // Go back to previous activity

        // Set up more options button click listener
        btnMore.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivityLog.this, ReportIssue.class);
            startActivity(intent);
        });

        // Set up Join Group button click listener
        btnJoinGroup.setOnClickListener(v -> {
            if (currentUser != null) {
                joinGroup();
            } else {
                Toast.makeText(GroupActivityLog.this,
                        "Please sign in to join this group", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up dropdown menu click listeners
        tvSettings.setOnClickListener(v -> {
            // Handle settings click
            Toast.makeText(GroupActivityLog.this, "Settings clicked", Toast.LENGTH_SHORT).show();
            cardDropdown.setVisibility(View.GONE);
            isDropdownVisible = false;
        });

        tvReportIssue.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivityLog.this, ReportIssue.class);
            startActivity(intent);
            cardDropdown.setVisibility(View.GONE);
            isDropdownVisible = false;
        });

        tvLeaveGroup.setOnClickListener(v -> {
            // Handle leave group click
            Toast.makeText(GroupActivityLog.this, "Leave Group clicked", Toast.LENGTH_SHORT).show();
            cardDropdown.setVisibility(View.GONE);
            isDropdownVisible = false;
        });
    }

    private void joinGroup() {
        if (groupID == null || groupID.isEmpty() || currentUser == null) {
            Toast.makeText(this, "Cannot join group at this time", Toast.LENGTH_SHORT).show();
            return;
        }

        String userID = currentUser.getUid();

        // Create a new membership document
        db.collection("groupMemberships")
                .whereEqualTo("userID", userID)
                .whereEqualTo("groupID", groupID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // User is not a member yet, add them
                        addMemberToGroup(userID, groupID);
                    } else {
                        // User is already a member
                        Toast.makeText(GroupActivityLog.this,
                                "You are already a member of this group", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking membership", e);
                    Toast.makeText(GroupActivityLog.this,
                            "Error joining group. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void addMemberToGroup(String userID, String groupID) {
        // Create membership object
        java.util.Map<String, Object> membership = new java.util.HashMap<>();
        membership.put("userID", userID);
        membership.put("groupID", groupID);
        membership.put("joinedAt", new java.util.Date());

        // Add to Firestore
        db.collection("groupMemberships")
                .add(membership)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(GroupActivityLog.this,
                            "Successfully joined group!", Toast.LENGTH_SHORT).show();
                    showSubmittedPortfolioDialog();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error joining group", e);
                    Toast.makeText(GroupActivityLog.this,
                            "Failed to join group. Please try again.", Toast.LENGTH_SHORT).show();
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

    private void showSubmittedPortfolioDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.submitted_portfolio_dialog);

        // Set dialog width to match parent
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Show dialog
        dialog.show();

        // Automatically dismiss after a delay (optional)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();

                // Navigate to GroupDetails after joining
                Intent intent = new Intent(GroupActivityLog.this, GroupDetails.class);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
                finish(); // Close this activity
            }
        }, 2000);
    }
    private void setupDrawer() {
        // Find all navigation items
        LinearLayout navHome = navigationView.findViewById(R.id.nav_home);
        LinearLayout navGroups = navigationView.findViewById(R.id.nav_groups);
        LinearLayout navNotifications = navigationView.findViewById(R.id.nav_notifications);
        LinearLayout navHistory = navigationView.findViewById(R.id.nav_history);
        LinearLayout navAccount = navigationView.findViewById(R.id.nav_account);
        LinearLayout navLogout = navigationView.findViewById(R.id.nav_logout);

        // Set click listeners for each navigation item
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivityLog.this, wealthLinkMainPage.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        navGroups.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivityLog.this, GroupListActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        navNotifications.setOnClickListener(v -> {
            // Replace with navigation to your Notifications activity
            Toast.makeText(GroupActivityLog.this, "Notifications clicked", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        navHistory.setOnClickListener(v -> {
            // Replace with navigation to your History activity
            Toast.makeText(GroupActivityLog.this, "History clicked", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivityLog.this, AccountView.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // Add logout handler
        if (navLogout != null) {
            navLogout.setOnClickListener(v -> {
                // Sign out from Firebase Authentication
                FirebaseAuth.getInstance().signOut();

                // Redirect to login screen
                Intent intent = new Intent(GroupActivityLog.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}
