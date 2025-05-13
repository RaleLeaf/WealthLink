package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionHistory extends BaseActivity {
    private TextView tvTotalBalance;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupDrawer(R.layout.activity_transaction_history);

        //Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        RecyclerView recyclerView = findViewById(R.id.recyclerTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        LinearLayout accountPage = navigationView.findViewById(R.id.nav_account);
        ivMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        loadUserBalance();
        loadTransactionHistory(recyclerView);
    }

    private void loadUserBalance() {
        if (currentUser == null) return;

        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String balanceStr = documentSnapshot.getString("balance");

                if (balanceStr != null) {
                    try {
                        double balance = Double.parseDouble(balanceStr);
                        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
                        format.setMinimumFractionDigits(2);
                        format.setMaximumFractionDigits(2);

                        String formattedBalance = "Php " + format.format(balance);
                        tvTotalBalance.setText(formattedBalance);
                    } catch (NumberFormatException e) {
                        tvTotalBalance.setText("Php 0.00");
                    }
                } else {
                    tvTotalBalance.setText("Php 0.00");
                }
            }
        }).addOnFailureListener(e -> {
            tvTotalBalance.setText("Php 0.00");
        });
    }

    private void loadTransactionHistory(RecyclerView recyclerView) {
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        List<transactionItemClass> transactions = new ArrayList<>();

        db.collection("transactions")
                .whereEqualTo("userID", userId)
                .orderBy("transactionDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                        int totalTransactions = task.getResult().size();
                        final int[] processedCount = {0};

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String groupID = document.getString("groupID");
                            String amount = document.getString("amount");
                            String transactionType = document.getString("transactionType");
                            Date transactionDate = document.getDate("transactionDate");

                            String formattedDate = transactionDate != null
                                    ? dateFormat.format(transactionDate)
                                    : "Unknown Date";

                            String formattedAmount = "Php " + (amount != null ? amount : "0.00");

                            boolean isIncome = "withdrawal".equalsIgnoreCase(transactionType);

                            final String finalFormattedDate = formattedDate;
                            final String finalFormattedAmount = formattedAmount;
                            final boolean finalIsIncome = isIncome;

                            if (groupID != null && !groupID.isEmpty()) {
                                db.collection("groups").document(groupID)
                                        .get()
                                        .addOnSuccessListener(groupDoc -> {
                                            String groupName = groupDoc.exists() ?
                                                    groupDoc.getString("groupName") :
                                                    "Unknown Group";

                                            if (groupName == null) groupName = "Group " + groupID;
                                            transactions.add(new transactionItemClass(
                                                    groupName,
                                                    finalFormattedDate,
                                                    finalFormattedAmount,
                                                    finalIsIncome
                                            ));

                                            processedCount[0]++;

                                            if (processedCount[0] == totalTransactions) {
                                                TransactionAdapter adapter = new TransactionAdapter(transactions);
                                                recyclerView.setAdapter(adapter);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            transactions.add(new transactionItemClass(
                                                    "Group " + groupID,
                                                    finalFormattedDate,
                                                    finalFormattedAmount,
                                                    finalIsIncome
                                            ));

                                            processedCount[0]++;

                                            if (processedCount[0] == totalTransactions) {
                                                TransactionAdapter adapter = new TransactionAdapter(transactions);
                                                recyclerView.setAdapter(adapter);
                                            }
                                        });
                            } else {
                                transactions.add(new transactionItemClass(
                                        "Unknown Group",
                                        finalFormattedDate,
                                        finalFormattedAmount,
                                        finalIsIncome
                                ));

                                processedCount[0]++;

                                if (processedCount[0] == totalTransactions) {
                                    TransactionAdapter adapter = new TransactionAdapter(transactions);
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                        }
                    } else {
                        transactions.add(new transactionItemClass("No transactions found", "", "", false));
                        TransactionAdapter adapter = new TransactionAdapter(transactions);
                        recyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(e -> {
                    transactions.add(new transactionItemClass("Failed to load transactions: " + e.getMessage(), "", "", false));
                    TransactionAdapter adapter = new TransactionAdapter(transactions);
                    recyclerView.setAdapter(adapter);
                });
    }
}