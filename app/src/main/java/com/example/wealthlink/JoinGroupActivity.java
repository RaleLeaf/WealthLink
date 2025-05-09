package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;

public class JoinGroupActivity extends AppCompatActivity {

    // UI Components
    private ConstraintLayout mainJoinLayout;
    private LinearLayout groupDetailsLayout;
    private LinearLayout successLayout;
    private EditText inviteCodeEditText;
    private EditText inviteCodeDisplay;
    private MaterialButton joinGroupButton;
    private MaterialButton viewGroupButton;
    private ImageButton backButton;
    private ImageButton backButtonDetails;
    private ImageButton backButtonSuccess;

    // Demo mode constants
    private static final String DEMO_CODE = "DEMO123";
    private static final String LIVELY_CODE = "HDJ2enSNliN15";
    private boolean isDemoMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        // Initialize UI components
        initializeViews();
        setupListeners();

        // Check if we should start in demo mode
        if (getIntent().getBooleanExtra("DEMO_MODE", false)) {
            startDemoMode();
        }
    }

    private void initializeViews() {
        // Main layouts
        mainJoinLayout = findViewById(R.id.mainJoinLayout);
        groupDetailsLayout = findViewById(R.id.groupDetailsLayout);
        successLayout = findViewById(R.id.successLayout);

        // Input fields
        inviteCodeEditText = findViewById(R.id.inviteCodeEditText);
        inviteCodeDisplay = findViewById(R.id.inviteCodeDisplay);

        // Buttons
        joinGroupButton = findViewById(R.id.joinGroupButton);
        viewGroupButton = findViewById(R.id.viewGroupButton);

        // Navigation buttons
        backButton = findViewById(R.id.backButton);
        backButtonDetails = findViewById(R.id.backButtonDetails);
        backButtonSuccess = findViewById(R.id.backButtonSuccess);

        // Set initial state
        mainJoinLayout.setVisibility(View.VISIBLE);
        groupDetailsLayout.setVisibility(View.GONE);
        successLayout.setVisibility(View.GONE);
    }

    private void setupListeners() {
        // Text change listener for invite code
        inviteCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code = s.toString().trim();

                if (code.equalsIgnoreCase(DEMO_CODE)) {
                    validateInviteCode(DEMO_CODE);
                } else if (code.equalsIgnoreCase(LIVELY_CODE)) {
                    validateInviteCode(LIVELY_CODE);
                }
            }


            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        // Join Group button
        joinGroupButton.setOnClickListener(v -> {
            // Show success overlay
            showSuccessOverlay();
        });

        // View Group button
        viewGroupButton.setOnClickListener(v -> {
            // In a real app, this would navigate to the group view
            Toast.makeText(JoinGroupActivity.this, "View Group clicked", Toast.LENGTH_SHORT).show();
        });

        // Back buttons
        backButton.setOnClickListener(v -> finish());

        backButtonDetails.setOnClickListener(v -> {
            groupDetailsLayout.setVisibility(View.GONE);
            mainJoinLayout.setVisibility(View.VISIBLE);
        });

        backButtonSuccess.setOnClickListener(v -> {
            successLayout.setVisibility(View.GONE);
            mainJoinLayout.setVisibility(View.VISIBLE);
        });

        // Long press on the title for quick demo mode
        findViewById(R.id.titleTextView).setOnLongClickListener(v -> {
            startDemoMode();
            return true;
        });

        // Toolbar menu button
        ImageButton menuButton = findViewById(R.id.btnMenu);
        menuButton.setOnClickListener(v -> {
            // Open navigation drawer or menu
            if (isDemoMode) {
                // In demo mode, cycle through the views
                cycleDemoViews();
            } else {
                Toast.makeText(JoinGroupActivity.this, "Menu clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Start demo mode for testing all screens
     */
    private void startDemoMode() {
        isDemoMode = true;
        Toast.makeText(this, "Demo mode activated. Type 'd', 'l', or 's' in the code field for quick demos",
                Toast.LENGTH_LONG).show();

        // Auto-fill a test code after a delay
        new Handler().postDelayed(() -> {
            if (!isFinishing()) {
                inviteCodeEditText.setText(LIVELY_CODE);
            }
        }, 1500);
    }

    /**
     * Cycle through the different views in demo mode
     */
    private void cycleDemoViews() {
        if (mainJoinLayout.getVisibility() == View.VISIBLE) {
            // Show group details
            mainJoinLayout.setVisibility(View.GONE);
            groupDetailsLayout.setVisibility(View.VISIBLE);
            successLayout.setVisibility(View.GONE);
            inviteCodeDisplay.setText(LIVELY_CODE);
        } else if (groupDetailsLayout.getVisibility() == View.VISIBLE) {
            // Show success overlay
            mainJoinLayout.setVisibility(View.GONE);
            groupDetailsLayout.setVisibility(View.GONE);
            successLayout.setVisibility(View.VISIBLE);
        } else {
            // Back to main
            mainJoinLayout.setVisibility(View.VISIBLE);
            groupDetailsLayout.setVisibility(View.GONE);
            successLayout.setVisibility(View.GONE);
        }
    }

    private void validateInviteCode(String code) {
        // In a real app, you'd make an API call to validate the code
        // For demo purposes, we'll just show the group details overlay

        // Set the code in the display
        inviteCodeDisplay.setText(code);

        // Show the group details overlay
        mainJoinLayout.setVisibility(View.GONE);
        groupDetailsLayout.setVisibility(View.VISIBLE);
        successLayout.setVisibility(View.GONE);
    }

    private void showSuccessOverlay() {
        // Show the success overlay
        mainJoinLayout.setVisibility(View.GONE);
        groupDetailsLayout.setVisibility(View.GONE);
        successLayout.setVisibility(View.VISIBLE);
    }
}
