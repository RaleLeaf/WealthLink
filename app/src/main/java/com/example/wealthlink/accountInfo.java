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

public class accountInfo extends AppCompatActivity {
    private RecyclerView recyclerAccounts;
    private AccountAdapter adapter;
    private List<Account> accountList;
    Button btnAddAccount, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        recyclerAccounts = findViewById(R.id.recyclerAccounts);
        recyclerAccounts.setLayoutManager(new LinearLayoutManager(this));

        accountList = new ArrayList<>();
        accountList.add(new Account("BPI Debit 2110", "Kurt Zander Kaw"));
        accountList.add(new Account("Metrobank 8723", "Kurt Zander Kaw"));

        adapter = new AccountAdapter(accountList);
        recyclerAccounts.setAdapter(adapter);
        btnAddAccount = findViewById(R.id.btnAddAccount);
        btnBack = findViewById(R.id.btnBack);

        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(accountInfo.this, AddAccount.class);
                startActivity(intent);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
