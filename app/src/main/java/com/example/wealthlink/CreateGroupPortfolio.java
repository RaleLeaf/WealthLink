package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateGroupPortfolio extends AppCompatActivity {
    private static final String TAG = "CreateGroupPortfolio";

    private EditText etGroupName;
    private Button btnCreateGroup;
    private ImageButton btnBack;
    private TextView tvInvite;
    private String inviteCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_group_portfolio);

        // Initialize UI components based on the XML layout
        etGroupName = findViewById(R.id.etGroupName);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnBack = findViewById(R.id.btnBack);
        tvInvite = findViewById(R.id.tvInvite);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set up the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Generate a random invite code
        inviteCode = generateInviteCode();
        tvInvite.setText(inviteCode);

        // Set up back button to return to GroupListActivity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set up create button functionality
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = etGroupName.getText().toString().trim();

                if (groupName.isEmpty()) {
                    Toast.makeText(CreateGroupPortfolio.this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                    return;
                }

                createNewGroup(groupName, inviteCode);
            }
        });
    }

    /**
     * Generates a random 8-character alphanumeric invite code
     */
    private String generateInviteCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int)(Math.random() * chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }

    /**
     * Creates a new group in Firestore with the given name and invite code
     */
    private void createNewGroup(String groupName, String inviteCode) {
        // Show loading or disable button
        btnCreateGroup.setEnabled(false);
        btnCreateGroup.setText("Creating...");

        // Get current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser == null) {
            Toast.makeText(this, "Please log in to create a group", Toast.LENGTH_SHORT).show();
            btnCreateGroup.setEnabled(true);
            btnCreateGroup.setText("Create Group");
            return;
        }

        // Create a unique ID for the group
        String groupId = UUID.randomUUID().toString();

        // Set up group data
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("groupName", groupName);
        groupData.put("creatorId", currentUser.getUid());
        groupData.put("totalInvestment", 0.00); // Initialize with 0.00 to ensure 2 decimal places
        groupData.put("creationDate", java.util.Calendar.getInstance().getTime());
        groupData.put("inviteCode", inviteCode);

        // Create the group in Firestore
        db.collection("groups").document(groupId)
                .set(groupData)
                .addOnSuccessListener(aVoid -> {
                    // Also add the creator as a member of the group
                    Map<String, Object> membershipData = new HashMap<>();
                    membershipData.put("userID", currentUser.getUid());
                    membershipData.put("groupID", groupId);
                    membershipData.put("investmentAmount", 0.00); // Initialize with 0.00
                    membershipData.put("joinDate", java.util.Calendar.getInstance().getTime());
                    membershipData.put("isAdmin", true);

                    db.collection("groupMemberships").add(membershipData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(CreateGroupPortfolio.this,
                                        "Group created successfully!", Toast.LENGTH_SHORT).show();

                                // Navigate back to the Group List
                                Intent intent = new Intent(CreateGroupPortfolio.this, GroupListActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CreateGroupPortfolio.this,
                                        "Error adding membership: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                btnCreateGroup.setEnabled(true);
                                btnCreateGroup.setText("Create Group");
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateGroupPortfolio.this,
                            "Error creating group: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnCreateGroup.setEnabled(true);
                    btnCreateGroup.setText("Create Group");
                });
    }

    @Override
    public void onBackPressed() {
        // Navigate back to GroupListActivity
        Intent intent = new Intent(CreateGroupPortfolio.this, GroupListActivity.class);
        startActivity(intent);
        finish();
    }
}