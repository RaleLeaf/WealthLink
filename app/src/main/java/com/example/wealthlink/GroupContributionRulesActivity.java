package com.example.wealthlink; // Replace with your actual package name

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wealthlink.ContributionRules;

public class GroupContributionRulesActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack;
    private TextView btnChangeFrequency;
    private TextView btnChangeAmount;
    private TextView btnChangeMinAmount;
    private TextView btnChangeDates;
    private TextView btnChangeVisibility;
    private TextView btnChangeJoinMethod;
    private Button btnSaveChanges;

    // Data model
    private ContributionRules contributionRules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribution_rules);

        // Initialize UI components
        initializeViews();
        setupClickListeners();

        // Load initial data
        loadGroupRulesData();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnChangeFrequency = findViewById(R.id.btnChangeFrequency);
        btnChangeAmount = findViewById(R.id.btnChangeAmount);
        btnChangeMinAmount = findViewById(R.id.btnChangeMinAmount);
        btnChangeDates = findViewById(R.id.btnChangeDates);
        btnChangeVisibility = findViewById(R.id.btnChangeVisibility);
        btnChangeJoinMethod = findViewById(R.id.btnChangeJoinMethod);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Change frequency
        btnChangeFrequency.setOnClickListener(v -> showFrequencySelectionDialog());

        // Change amount type
        btnChangeAmount.setOnClickListener(v -> showAmountTypeDialog());

        // Change minimum amount
        btnChangeMinAmount.setOnClickListener(v -> showMinAmountDialog());

        // Change dates
        btnChangeDates.setOnClickListener(v -> showDateRangeDialog());

        // Change visibility
        btnChangeVisibility.setOnClickListener(v -> showVisibilityDialog());

        // Change join method
        btnChangeJoinMethod.setOnClickListener(v -> showJoinMethodDialog());

        // Save changes
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadGroupRulesData() {
        // Here you would typically load data from your database/API
        // For now, we'll create a default ContributionRules object
        contributionRules = new ContributionRules();

        // Update UI with model data
        updateUI();
    }

    private void updateUI() {
        // Find all TextViews that display the current values
        TextView frequencyValue = findViewById(R.id.btnChangeFrequency)
                .getRootView()
                .findViewById(android.R.id.content)
                .findViewById(R.id.btnChangeFrequency)
                .getRootView()
                .findViewWithTag("frequency_value");

        TextView amountTypeValue = findViewById(R.id.btnChangeAmount)
                .getRootView()
                .findViewById(android.R.id.content)
                .findViewById(R.id.btnChangeAmount)
                .getRootView()
                .findViewWithTag("amount_type_value");

        TextView minAmountValue = findViewById(R.id.btnChangeMinAmount)
                .getRootView()
                .findViewById(android.R.id.content)
                .findViewById(R.id.btnChangeMinAmount)
                .getRootView()
                .findViewWithTag("min_amount_value");

        TextView datesValue = findViewById(R.id.btnChangeDates)
                .getRootView()
                .findViewById(android.R.id.content)
                .findViewById(R.id.btnChangeDates)
                .getRootView()
                .findViewWithTag("dates_value");

        TextView visibilityValue = findViewById(R.id.btnChangeVisibility)
                .getRootView()
                .findViewById(android.R.id.content)
                .findViewById(R.id.btnChangeVisibility)
                .getRootView()
                .findViewWithTag("visibility_value");

        TextView joinMethodValue = findViewById(R.id.btnChangeJoinMethod)
                .getRootView()
                .findViewById(android.R.id.content)
                .findViewById(R.id.btnChangeJoinMethod)
                .getRootView()
                .findViewWithTag("join_method_value");

        // Update TextViews if they exist
        if (frequencyValue != null) frequencyValue.setText(contributionRules.getFrequency());
        if (amountTypeValue != null) amountTypeValue.setText(contributionRules.getAmountType());
        if (minAmountValue != null) minAmountValue.setText(contributionRules.getFormattedMinimumAmount());
        if (datesValue != null) datesValue.setText(contributionRules.getFormattedDateRange());
        if (visibilityValue != null) visibilityValue.setText(contributionRules.getVisibility());
        if (joinMethodValue != null) joinMethodValue.setText(contributionRules.getJoinMethod());
    }

    private void showFrequencySelectionDialog() {
        String[] frequencies = {"Weekly", "Biweekly", "Monthly", "Quarterly", "Yearly"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Frequency")
                .setItems(frequencies, (dialog, which) -> {
                    // Update model
                    contributionRules.setFrequency(frequencies[which]);

                    // Update UI
                    updateUI();

                    Toast.makeText(this, "Frequency updated to " + frequencies[which], Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAmountTypeDialog() {
        String[] amountTypes = {"Fixed", "Flexible"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Amount Type")
                .setItems(amountTypes, (dialog, which) -> {
                    // Update model
                    contributionRules.setAmountType(amountTypes[which]);

                    // Update UI
                    updateUI();

                    // If "Flexible" is selected, enable minimum amount field
                    if (amountTypes[which].equals("Flexible")) {
                        // Optionally show the minimum amount dialog immediately
                        showMinAmountDialog();
                    } else {
                        // Reset minimum amount for fixed type
                        contributionRules.setMinimumAmount(0.0);
                        updateUI();
                    }

                    Toast.makeText(this, "Amount type updated to " + amountTypes[which], Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showMinAmountDialog() {
        // Create a custom dialog with an EditText for entering the minimum amount
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Set current value if available
        if (contributionRules.getMinimumAmount() > 0) {
            input.setText(String.valueOf(contributionRules.getMinimumAmount()));
        }

        builder.setTitle("Set Minimum Amount")
                .setMessage("Enter the minimum contribution amount required:")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Get the input value and update the model
                    try {
                        double amount = Double.parseDouble(input.getText().toString());
                        contributionRules.setMinimumAmount(amount);
                        updateUI();
                        Toast.makeText(this, "Minimum amount set", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDateRangeDialog() {
        // In a real app, you'd use a DatePickerDialog or a custom date range picker
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Date Range")
                .setMessage("This would open a date range picker in a real app")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showVisibilityDialog() {
        String[] visibilityOptions = {"Public", "Private", "Hidden"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Group Visibility")
                .setItems(visibilityOptions, (dialog, which) -> {
                    // Update model
                    contributionRules.setVisibility(visibilityOptions[which]);

                    // Update UI
                    updateUI();

                    Toast.makeText(this, "Visibility updated to " + visibilityOptions[which], Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showJoinMethodDialog() {
        String[] joinMethods = {"Invite-only", "Application", "Open join"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Join Request Method")
                .setItems(joinMethods, (dialog, which) -> {
                    // Update model
                    contributionRules.setJoinMethod(joinMethods[which]);

                    // Update UI
                    updateUI();

                    Toast.makeText(this, "Join method updated to " + joinMethods[which], Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveChanges() {
        // Validate required fields
        if (contributionRules.getFrequency() == null || contributionRules.getFrequency().isEmpty()) {
            Toast.makeText(this, "Please select a frequency", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contributionRules.getAmountType() == null || contributionRules.getAmountType().isEmpty()) {
            Toast.makeText(this, "Please select an amount type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contributionRules.getVisibility() == null || contributionRules.getVisibility().isEmpty()) {
            Toast.makeText(this, "Please select group visibility", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contributionRules.getJoinMethod() == null || contributionRules.getJoinMethod().isEmpty()) {
            Toast.makeText(this, "Please select join request method", Toast.LENGTH_SHORT).show();
            return;
        }

        // Here you would save all the settings to your database/API
        // Example:
        // groupRepository.saveContributionRules(groupId, contributionRules);

        // Show a success message
        Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();

        // Return to previous screen
        finish();
    }
}