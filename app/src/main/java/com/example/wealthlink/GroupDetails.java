package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GroupDetails extends AppCompatActivity {
    Button btnDeposit, btnWithdraw;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Make sure this layout file exists with this exact name
        setContentView(R.layout.activity_group_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Correctly cast the buttons to their appropriate types
        btnDeposit = findViewById(R.id.btnDeposit);
        btnWithdraw = findViewById(R.id.btnWithdraw);
        btnBack = findViewById(R.id.btnBack);  // Now correctly cast as ImageButton

        btnBack.setOnClickListener(v -> {
            finish(); // Go back to previous activity
        });

        btnWithdraw.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetails.this, WithdrawAccount.class);
            startActivity(intent);
        });

        btnDeposit.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetails.this, DepositAccount.class);
            startActivity(intent);
        });
    }
}