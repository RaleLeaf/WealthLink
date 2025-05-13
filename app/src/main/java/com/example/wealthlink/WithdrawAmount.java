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

public class WithdrawAmount extends AppCompatActivity {
    Button btnBack, btnWithdraw;
    TextView tvMainBankName, tvMainAccountHolder;

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
        tvMainAccountHolder = findViewById(R.id.tvMainAccountHolder);

        // Get account details from intent
        String accountName = getIntent().getStringExtra("ACCOUNT_NAME");
        String accountHolder = getIntent().getStringExtra("ACCOUNT_HOLDER");

        // Display account details if available
        if (accountName != null) {
            tvMainBankName.setText(accountName);
        }

        if (accountHolder != null) {
            tvMainAccountHolder.setText(accountHolder);
        }

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