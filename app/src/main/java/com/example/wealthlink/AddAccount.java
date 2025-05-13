package com.example.wealthlink;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddAccount extends AppCompatActivity {
    Button btnAddAccount, btnBack;
    private LinearLayout accountAddedDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnAddAccount = findViewById(R.id.btnAddAccount);
        btnBack = findViewById(R.id.btnBack);
        accountAddedDialog = findViewById(R.id.accountAddedDialog);

        btnAddAccount.setOnClickListener(v -> {
            // Show the custom dialog
            showAccountAddedDialog();
        });

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void showAccountAddedDialog() {
        // Create dialog with custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(AddAccount.this);
        View dialogView = getLayoutInflater().inflate(R.layout.account_added, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        // Set transparent background to show only the card
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Show dialog
        dialog.show();

        // Auto-dismiss after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                onBackPressed(); // Return to previous screen
            }
        }, 2000);
    }
}