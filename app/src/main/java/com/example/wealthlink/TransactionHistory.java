package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistory extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupDrawer(R.layout.activity_transaction_history);

        RecyclerView recyclerView = findViewById(R.id.recyclerTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<transactionItemClass> list = new ArrayList<>();
        list.add(new transactionItemClass("ABC Investment", "13 Oct 2019", "Php 15,000.00", false));
        list.add(new transactionItemClass("Return Corn Partnership", "10 Oct 2019", "Php 15,000.00", true));
        list.add(new transactionItemClass("Citronella Oil", "10 Oct 2019", "Php 15,000.00", false));

        TransactionAdapter adapter = new TransactionAdapter(list);
        recyclerView.setAdapter(adapter);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        LinearLayout accountPage = navigationView.findViewById(R.id.nav_account);

        accountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAccount = new Intent(TransactionHistory.this, AccountView.class);
                startActivity(viewAccount);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }
}
