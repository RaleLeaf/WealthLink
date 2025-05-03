package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WithdrawAccount extends AppCompatActivity {

    private RecyclerView recyclerAccounts;
    private AccountAdapter adapter;
    private List<Account> accountList;
    private Button btnAddAccount;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_account);

        // Initialize views
        recyclerAccounts = findViewById(R.id.recyclerAccounts);
        btnAddAccount = findViewById(R.id.btnAddAccount);
        btnBack = findViewById(R.id.btnBack);

        //RecyclerView
        recyclerAccounts.setLayoutManager(new LinearLayoutManager(this));

        // dummy account list
        accountList = new ArrayList<>();
        accountList.add(new Account("BPI Debit 2110", "Kurt Zander Kaw"));
        accountList.add(new Account("Metrobank 8723", "Kurt Zander Kaw"));

        // Set up adapter
        adapter = new AccountAdapter(accountList);
        recyclerAccounts.setAdapter(adapter);

        // Add Account view
        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WithdrawAccount.this, AddAccount.class);
                startActivity(intent);
            }
        });

        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });
    }
}