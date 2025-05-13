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

public class DepositAmount extends AppCompatActivity {
    Button btnBack,btnDeposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deposit_amount);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnBack = findViewById(R.id.btnBack);
        btnDeposit = findViewById(R.id.btnDeposit);

        btnBack.setOnClickListener(v -> {
            finish(); // Go back to previous activity
        });
        btnDeposit.setOnClickListener(v -> {
            showDepositSuccessDialog();
        });
    }
    private void showDepositSuccessDialog() {
        // Create dialog with custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(DepositAmount.this);
        View dialogView = getLayoutInflater().inflate(R.layout.account_added, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        // Find and modify the text in the dialog to show "Deposit Successful!"
        TextView tvAdded = dialogView.findViewById(R.id.tv_added);
        if (tvAdded != null) {
            tvAdded.setText("Deposit Successful!");
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
