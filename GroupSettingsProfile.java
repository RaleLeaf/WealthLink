package com.example.wealthlink; // Replace with your actual package name

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class GroupSettingsProfile extends AppCompatActivity {

    private ImageButton btnBack;
    private CardView profileImageContainer;
    private ImageView profileImage;
    private EditText groupNameEditText;
    private EditText descriptionEditText;
    private TextView changeText;
    private TextView investmentGoalText;
    private Button btnSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings_profile);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        profileImageContainer = findViewById(R.id.profileImageContainer);
        profileImage = findViewById(R.id.profileImage);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        changeText = findViewById(R.id.changeText);
        investmentGoalText = findViewById(R.id.investmentGoalText);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Set up click listeners
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous screen
            }
        });

        profileImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open image selection dialog or camera
                showImageSelectionOptions();
            }
        });

        changeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set focus to description field
                descriptionEditText.requestFocus();
            }
        });

        investmentGoalText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show investment goal type selection dialog
                showInvestmentGoalTypeDialog();
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the changes
                saveChanges();
            }
        });

        // Load initial data
        loadGroupData();
    }

    private void showImageSelectionOptions() {
        // Implement logic to open a dialog for selecting profile image
        // For now, we'll just show a toast
        Toast.makeText(this, "Image selection options", Toast.LENGTH_SHORT).show();
    }

    private void showInvestmentGoalTypeDialog() {
        // Implement logic to show a dialog for selecting investment goal type
        // For now, we'll just show a toast
        Toast.makeText(this, "Investment goal type options", Toast.LENGTH_SHORT).show();
    }

    private void saveChanges() {
        // Implement logic to save changes to database or preferences
        String groupName = groupNameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        // Validate inputs
        if (groupName.isEmpty()) {
            groupNameEditText.setError("Group name cannot be empty");
            return;
        }

        // Show success message
        Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
        finish(); // Go back to previous screen
    }

    private void loadGroupData() {
        // Load data from database or preferences
        // For now, we'll use hardcoded values
        groupNameEditText.setText("Lively Inc.");
        // Description could be empty or null, so check before setting
        descriptionEditText.setText("");
        investmentGoalText.setText("Savings, Investment Portfolio, Hybrid");

        // Load profile image (sample code)
        // Glide.with(this).load(groupProfileImageUrl).into(profileImage);
    }
}