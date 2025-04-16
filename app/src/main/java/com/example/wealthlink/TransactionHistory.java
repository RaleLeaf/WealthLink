package com.example.wealthlink;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class TransactionHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        RecyclerView recyclerView = findViewById(R.id.recyclerTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<transactionItemClass> list = new ArrayList<>();
        list.add(new TransactionItem("ABC Investment", "13 Oct 2019", "Php 15,000.00", false));
        list.add(new TransactionItem("Return Corn Partnership", "10 Oct 2019", "Php 15,000.00", true));
        list.add(new TransactionItem("Citronella Oil", "10 Oct 2019", "Php 15,000.00", false));

        TransactionAdapter adapter = new TransactionAdapter(list);
        recyclerView.setAdapter(adapter);

    }
}