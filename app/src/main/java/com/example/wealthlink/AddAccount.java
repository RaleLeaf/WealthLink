package com.example.wealthlink;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
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
            // Show the dialog
            accountAddedDialog.setVisibility(View.VISIBLE);

            // Auto-hide after 2 seconds
            new Handler(Looper.getMainLooper()).postDelayed(() ->
            {accountAddedDialog.setVisibility(View.GONE);
                onBackPressed();}, 2000);

        });

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

    }
}