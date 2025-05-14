package com.example.wealthlink;

import android.os.Bundle;
import android.widget.ImageView;
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

        ivMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        loadUserBalance();
        loadTransactionHistory(recyclerView);
    }

    private void loadUserBalance() {
        if (currentUser == null) return;

        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Object balanceObj = documentSnapshot.get("balance");
                double balance = 0.0;

                if (balanceObj instanceof String) {
                    try {
                        balance = Double.parseDouble((String) balanceObj);
                    } catch (NumberFormatException e) {
                        // Handle parsing error
                    }
                } else if (balanceObj instanceof Number) {
                    balance = ((Number) balanceObj).doubleValue();
                }

                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                String formattedBalance = format.format(balance);
                tvTotalBalance.setText(formattedBalance);
            } else {
                tvTotalBalance.setText("Php 0.00");
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

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get transaction data with safer access methods
                            String groupID = document.getString("groupID");

                            // Handle amount - could be String or Number
                            Object amountObj = document.get("amount");
                            String amount = "0.00";
                            if (amountObj instanceof String) {
                                amount = (String) amountObj;
                            } else if (amountObj instanceof Number) {
                                amount = String.format(Locale.US, "%.2f", ((Number) amountObj).doubleValue());
                            }

                            String transactionType = document.getString("transactionType");
                            Date transactionDate = document.getDate("transactionDate");

                            // Default values if data is missing
                            String formattedDate = transactionDate != null
                                    ? dateFormat.format(transactionDate)
                                    : "Unknown Date";

                            String formattedAmount = "Php " + amount;

                            // Determine if transaction is income based on transactionType
                            // Assuming "deposit" is income and everything else is expense
                            boolean isIncome = "deposit".equalsIgnoreCase(transactionType);

                            // Add transaction to list with placeholder group name
                            final String finalGroupID = groupID;
                            final String finalFormattedDate = formattedDate;
                            final String finalFormattedAmount = formattedAmount;
                            final boolean finalIsIncome = isIncome;

                            if (groupID != null && !groupID.isEmpty()) {
                                // Try to get group name
                                db.collection("groups").document(groupID)
                                        .get()
                                        .addOnSuccessListener(groupDoc -> {
                                            String groupName = groupDoc.exists() ?
                                                    groupDoc.getString("groupName") :
                                                    "Group " + finalGroupID;

                                            if (groupName == null) groupName = "Group " + finalGroupID;

                                            transactions.add(new transactionItemClass(
                                                    groupName,
                                                    finalFormattedDate,
                                                    finalFormattedAmount,
                                                    finalIsIncome
                                            ));

                                            // Update adapter after adding item
                                            updateAdapter(recyclerView, transactions);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Fallback if group fetch fails
                                            transactions.add(new transactionItemClass(
                                                    "Group " + finalGroupID,
                                                    finalFormattedDate,
                                                    finalFormattedAmount,
                                                    finalIsIncome
                                            ));

                                            // Update adapter after adding item
                                            updateAdapter(recyclerView, transactions);
                                        });
                            } else {
                                // If no group ID
                                transactions.add(new transactionItemClass(
                                        "Personal Transaction",
                                        finalFormattedDate,
                                        finalFormattedAmount,
                                        finalIsIncome
                                ));

                                // Update adapter after adding item
                                updateAdapter(recyclerView, transactions);
                            }
                        }
                    } else {
                        // No transactions found
                        transactions.add(new transactionItemClass("No transactions found", "", "", false));
                        updateAdapter(recyclerView, transactions);
                    }
                })
                .addOnFailureListener(e -> {
                    transactions.add(new transactionItemClass("Failed to load transactions", "", "", false));
                    updateAdapter(recyclerView, transactions);
                });
    }

    private void updateAdapter(RecyclerView recyclerView, List<transactionItemClass> transactions) {
        // If the adapter is already set with our data, update it
        TransactionAdapter existingAdapter = (TransactionAdapter) recyclerView.getAdapter();
        if (existingAdapter != null) {
            existingAdapter.updateTransactions(transactions);
        } else {
            // Otherwise create a new adapter
            TransactionAdapter adapter = new TransactionAdapter(new ArrayList<>(transactions));
            recyclerView.setAdapter(adapter);
        }
    }
}