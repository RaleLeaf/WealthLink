package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DepositAccount extends AppCompatActivity {
    Button btnBack, btnAddAccount;
    private RecyclerView recyclerAccounts;
    private AccountAdapter adapter;
    private List<Account> accountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deposit_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        btnAddAccount = findViewById(R.id.btnAddAccount);
        recyclerAccounts = findViewById(R.id.recyclerAccounts);

        // Set up RecyclerView
        recyclerAccounts.setLayoutManager(new LinearLayoutManager(this));

        // Create dummy account list (replace with your actual data source)
        accountList = new ArrayList<>();
        accountList.add(new Account("BPI Debit 2110", "Kurt Zander Kaw"));
        accountList.add(new Account("Metrobank 8723", "Kurt Zander Kaw"));

        // Initialize and set adapter with click listener
        adapter = new AccountAdapter(accountList, new AccountAdapter.OnAccountClickListener() {
            @Override
            public void onAccountClick(Account account) {
                Intent intent = new Intent(DepositAccount.this, DepositAmount.class);
                // Use the correct getter methods
                intent.putExtra("ACCOUNT_NAME", account.getBankName());
                intent.putExtra("ACCOUNT_HOLDER", account.getAccountHolder());
                startActivity(intent);
            }
        });
        recyclerAccounts.setAdapter(adapter);

        // Back button click listener
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Add account button click listener
        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DepositAccount.this, AddAccount.class);
                startActivity(intent);
            }
        });

        // Set click listener for the main account card
        findViewById(R.id.cardMainAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DepositAccount.this, DepositAmount.class);
                intent.putExtra("ACCOUNT_NAME", "BDO Credit 5295");
                intent.putExtra("ACCOUNT_HOLDER", "Kurt Zander Kaw");
                startActivity(intent);
            }
        });
    }
}