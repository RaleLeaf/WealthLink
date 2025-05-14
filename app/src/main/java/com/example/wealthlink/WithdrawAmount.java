package com.example.wealthlink;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WithdrawAmount extends AppCompatActivity {
    Button btnBack, btnWithdraw;
    TextView tvMainBankName, tvBalance, tvTotalAmount, tvFee, tvAvailableBalance;
    EditText etAmount;

    // Constants
    private static final double FEE_PERCENTAGE = 0.001; // 0.1%

    // Variables
    private String groupID;
    private double userBalance = 0.0;
    private double userInvestmentInGroup = 0.0;
    private String groupName = "";
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_withdraw_amount);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        btnWithdraw = findViewById(R.id.btnWithdraw);
        tvMainBankName = findViewById(R.id.tvMainBankName);
        tvBalance = findViewById(R.id.tvBalance);
        tvAvailableBalance = findViewById(R.id.tvAvailableBalance);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvFee = findViewById(R.id.tvFeeAmount);
        etAmount = findViewById(R.id.etAmount);

        // Get group ID from intent
        groupID = getIntent().getStringExtra("groupID");
        if (groupID == null) {
            Toast.makeText(this, "Error: Group ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Load user data and group data
        loadUserData();
        loadGroupData();

        // Set back button click listener
        btnBack.setOnClickListener(v -> finish());

        // Add text change listener to calculate fees in real-time
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateWithdrawAmount();
            }
        });

        // Set withdraw button click listener
        btnWithdraw.setOnClickListener(v -> {
            if (validateWithdrawal()) {
                processWithdrawal();
            }
        });
    }

    private void loadUserData() {
        if (currentUser == null) return;

        // Get user balance
        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String balanceStr = documentSnapshot.getString("balance");

                if (balanceStr != null) {
                    try {
                        userBalance = Double.parseDouble(balanceStr);
                        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
                        format.setMinimumFractionDigits(2);
                        format.setMaximumFractionDigits(2);

                        String formattedBalance = "Php " + format.format(userBalance);
                        tvBalance.setText(formattedBalance);
                    } catch (NumberFormatException e) {
                        tvBalance.setText("Error");
                    }
                } else {
                    tvBalance.setText("Error");
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        // Get user's investment amount in this group
        db.collection("groupMemberships")
                .whereEqualTo("userID", currentUser.getUid())
                .whereEqualTo("groupID", groupID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Object investmentAmount = queryDocumentSnapshots.getDocuments().get(0).get("investmentAmount");
                        if (investmentAmount != null) {
                            if (investmentAmount instanceof Double) {
                                userInvestmentInGroup = (Double) investmentAmount;
                            } else if (investmentAmount instanceof Long) {
                                userInvestmentInGroup = ((Long) investmentAmount).doubleValue();
                            } else if (investmentAmount instanceof String) {
                                try {
                                    userInvestmentInGroup = Double.parseDouble((String) investmentAmount);
                                } catch (NumberFormatException e) {
                                    userInvestmentInGroup = 0.0;
                                }
                            }

                            NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
                            format.setMinimumFractionDigits(2);
                            format.setMaximumFractionDigits(2);
                            tvAvailableBalance.setText("Php " + format.format(userInvestmentInGroup));
                        }
                    } else {
                        tvAvailableBalance.setText("Php 0.00");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading user investment data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadGroupData() {
        db.collection("groups").document(groupID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        groupName = documentSnapshot.getString("name");
                        if (groupName != null) {
                            tvMainBankName.setText(groupName);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading group data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void calculateWithdrawAmount() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            tvTotalAmount.setText("Php 0.00");
            tvFee.setText("Php 0.00");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            double fee = amount * FEE_PERCENTAGE;
            double totalAmount = amount - fee;

            NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
            format.setMinimumFractionDigits(2);
            format.setMaximumFractionDigits(2);

            tvTotalAmount.setText("Php " + format.format(totalAmount));
            tvFee.setText("Php " + format.format(fee));
        } catch (NumberFormatException e) {
            tvTotalAmount.setText("Error");
            tvFee.setText("Error");
        }
    }

    private boolean validateWithdrawal() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (amount > userInvestmentInGroup) {
                Toast.makeText(this, "Cannot withdraw more than your investment in this group", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void processWithdrawal() {
        double withdrawAmount = Double.parseDouble(etAmount.getText().toString().trim());
        double fee = withdrawAmount * FEE_PERCENTAGE;
        double netAmount = withdrawAmount - fee;

        // First check if user has membership in this group
        db.collection("groupMemberships")
                .whereEqualTo("userID", currentUser.getUid())
                .whereEqualTo("groupID", groupID)
                .get()
                .addOnSuccessListener(membershipSnapshot -> {
                    if (!membershipSnapshot.isEmpty()) {
                        // Start transaction after getting membership info
                        executeWithdrawalTransaction(withdrawAmount, fee, netAmount, membershipSnapshot);
                    } else {
                        Toast.makeText(this, "You don't have any investment in this group", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to check membership: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void executeWithdrawalTransaction(double withdrawAmount, double fee, double netAmount,
                                              com.google.firebase.firestore.QuerySnapshot membershipSnapshot) {
        // Start a Firestore transaction to ensure data consistency
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // 1. Update user balance
            DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
            double newUserBalance = userBalance + netAmount;
            transaction.update(userDocRef, "balance", String.valueOf(newUserBalance));

            // 2. Update user's investment amount in the group
            DocumentReference groupMembershipRef = membershipSnapshot.getDocuments().get(0).getReference();
            double newInvestmentAmount = userInvestmentInGroup - withdrawAmount;
            transaction.update(groupMembershipRef, "investmentAmount", newInvestmentAmount);

            // 3. Update group's total investment
            DocumentReference groupRef = db.collection("groups").document(groupID);
            transaction.update(groupRef, "totalInvestment", FieldValue.increment(-withdrawAmount));

            // 4. Create a new transaction record
            Map<String, Object> transactionData = new HashMap<>();
            transactionData.put("amount", withdrawAmount);
            transactionData.put("groupID", groupID);
            transactionData.put("transactionDate", new Date());
            transactionData.put("transactionType", "withdraw");
            transactionData.put("userID", currentUser.getUid());
            transactionData.put("fee", fee);
            transactionData.put("netAmount", netAmount);

            // Add transaction document
            transaction.set(db.collection("transactions").document(), transactionData);

            return null;
        }).addOnSuccessListener(aVoid -> {
            showWithdrawSuccessDialog();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Withdrawal failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showWithdrawSuccessDialog() {
        // Create dialog with custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawAmount.this);
        View dialogView = getLayoutInflater().inflate(R.layout.account_added, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        // Find and modify the text in the dialog to show "Withdraw Successful!"
        TextView tvAdded = dialogView.findViewById(R.id.tv_added);
        if (tvAdded != null) {
            tvAdded.setText("Withdraw Successful!");
        }

        // Set transparent background to show only the card
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Show dialog
        dialog.show();

        // Auto-dismiss after 2 seconds and return to previous screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                finish(); // Return to previous screen
            }
        }, 2000);
    }
}