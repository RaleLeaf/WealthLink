package com.example.wealthlink;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Locale;

public class WithdrawAmount extends AppCompatActivity {
    Button btnBack, btnWithdraw;
    TextView tvMainBankName, tvBalance;

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

        // Get account details from intent
        String accountName = getIntent().getStringExtra("ACCOUNT_NAME");
        String accountHolder = getIntent().getStringExtra("ACCOUNT_HOLDER");

        // Display account details if available
        if (accountName != null) {
            tvMainBankName.setText(accountName);
        }

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
                        tvBalance.setText(formattedBalance);
                    } catch (NumberFormatException e) {
                        tvBalance.setText("Error");
                    }
                } else {
                    tvBalance.setText("Error");
                }
            } else {
                // Document does not exist
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
        });

        // Set back button click listener
        btnBack.setOnClickListener(v -> {
            finish(); // Go back to previous activity
        });

        // Set withdraw button click listener
        btnWithdraw.setOnClickListener(v -> {
            showWithdrawSuccessDialog();
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