package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GroupSettings extends AppCompatActivity {
    ImageView btnBack, groupProfileImage;
    TextView tvGroupName;
    LinearLayout profile, rules, members, settings;

    private FirebaseFirestore db;
    private String groupID;
    private String groupName;
    private String groupProfilePicUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Get groupID from intent
        groupID = getIntent().getStringExtra("groupID");
        if (groupID == null) {
            // Handle error - no groupID provided
            Toast.makeText(this, "Error: No group ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI elements
        btnBack = findViewById(R.id.btnBack);
        groupProfileImage = findViewById(R.id.groupProfileImage);
        tvGroupName = findViewById(R.id.tvGroupName);
        profile = findViewById(R.id.profileItem);
        rules = findViewById(R.id.rulesItem);
        members = findViewById(R.id.membersItem);
        settings = findViewById(R.id.settingsItem);

        // Load group data from Firebase
        loadGroupData();

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupSettings.this, GroupSettingsProfile.class);
                intent.putExtra("groupID", groupID);
                intent.putExtra("groupName", groupName);
                intent.putExtra("groupProfilePicUrl", groupProfilePicUrl);
                startActivity(intent);
            }
        });

        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupSettings.this, ContributionRules.class);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
            }
        });

        members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Members screen when implemented
                // Intent intent = new Intent(GroupSettings.this, GroupMembers.class);
                // intent.putExtra("groupID", groupID);
                // startActivity(intent);
                Toast.makeText(GroupSettings.this, "Members feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Settings screen when implemented
                // Intent intent = new Intent(GroupSettings.this, GroupGeneralSettings.class);
                // intent.putExtra("groupID", groupID);
                // startActivity(intent);
                Toast.makeText(GroupSettings.this, "Settings feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGroupData() {
        db.collection("groups").document(groupID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get group data - check both potential field names
                                groupName = document.getString("groupName");
                                if (groupName == null) {
                                    groupName = document.getString("name");
                                }

                                groupProfilePicUrl = document.getString("profilePicUrl");

                                // Update UI
                                if (groupName != null) {
                                    tvGroupName.setText(groupName);
                                } else {
                                    tvGroupName.setText("Unnamed Group");
                                }

                                // Load profile image if URL is available
                                if (groupProfilePicUrl != null && !groupProfilePicUrl.isEmpty()) {
                                    Glide.with(GroupSettings.this)
                                            .load(groupProfilePicUrl)
                                            .placeholder(R.drawable.profile_pic)
                                            .error(R.drawable.profile_pic)
                                            .into(groupProfileImage);
                                }
                            } else {
                                // Document doesn't exist
                                Toast.makeText(GroupSettings.this, "Group not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle the error
                            Toast.makeText(GroupSettings.this, "Error loading group data: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh group data when returning to this screen
        if (groupID != null) {
            loadGroupData();
        }
    }
}