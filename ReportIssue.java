package com.example.wealthlink;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ReportIssue extends AppCompatActivity {

    // UI components for form
    private LinearLayout reportFormLayout;
    private EditText issueTitleEditText;
    private EditText issueDescriptionEditText;
    private MaterialButton submitButton;
    private ImageButton backButton;

    // UI components for success view
    private LinearLayout successLayout;
    private MaterialButton returnButton;
    private ImageButton backButtonSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        // Initialize form UI components
        reportFormLayout = findViewById(R.id.reportFormLayout);
        issueTitleEditText = findViewById(R.id.issueTitle);
        issueDescriptionEditText = findViewById(R.id.issueDescription);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);

        // Initialize success view UI components
        successLayout = findViewById(R.id.successLayout);
        returnButton = findViewById(R.id.returnButton);
        backButtonSuccess = findViewById(R.id.backButtonSuccess);

        // Set initial visibility
        reportFormLayout.setVisibility(View.VISIBLE);
        successLayout.setVisibility(View.GONE);

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Form back button - return to previous screen
        backButton.setOnClickListener(v -> finish());

        // Submit button - validate and submit form
        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                submitReport();
            }
        });

        // Success screen back button - return to form
        backButtonSuccess.setOnClickListener(v -> {
            // Show form again and hide success view
            reportFormLayout.setVisibility(View.VISIBLE);
            successLayout.setVisibility(View.GONE);

            // Clear form fields
            clearForm();
        });

        // Return button - go back to home screen
        returnButton.setOnClickListener(v -> {
            // Just finish this activity to go back
            finish();
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validate issue title
        String title = issueTitleEditText.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            issueTitleEditText.setError("Title is required");
            isValid = false;
        }

        // Validate issue description
        String description = issueDescriptionEditText.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            issueDescriptionEditText.setError("Description is required");
            isValid = false;
        }

        return isValid;
    }

    private void submitReport() {
        // Show loading state
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");

        // Simulate a brief loading period
        submitButton.postDelayed(() -> {
            // Hide the form
            reportFormLayout.setVisibility(View.GONE);

            // Show the success view
            successLayout.setVisibility(View.VISIBLE);

            // Reset button state
            submitButton.setEnabled(true);
            submitButton.setText("Submit Report");
        }, 1000); // 1 second delay
    }

    private void clearForm() {
        // Clear all form fields
        issueTitleEditText.setText("");
        issueDescriptionEditText.setText("");

        // Clear any error messages
        issueTitleEditText.setError(null);
        issueDescriptionEditText.setError(null);
    }
}