package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wealthlink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class wealthLinkMainPage extends BaseActivity {
    private RecyclerView rvGroups;
    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    LinearLayout accountPage;

    TextView wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupDrawer(R.layout.activity_wealth_link_main_page);

        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);
        rvGroups = findViewById(R.id.rvGroups);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        accountPage = navigationView.findViewById(R.id.nav_account);

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        accountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAccount = new Intent(wealthLinkMainPage.this, AccountView.class);
                startActivity(viewAccount);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        List<Group> groups = new ArrayList<>();
        groups.add(new Group("Lively Inc.", "21:21", "$432"));
        groups.add(new Group("Lively Inc.", "21:21", "$432"));

        GroupAdapter groupAdapter = new GroupAdapter(groups);
        rvGroups.setAdapter(groupAdapter);



        wallet = findViewById(R.id.tvWalletAmount);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                        wallet.setText(formattedBalance);
                    } catch (NumberFormatException e) {
                        wallet.setText("Error");
                    }
                } else {
                    wallet.setText("Error");
                }
            } else {
                // Document does not exist
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
        });
    }
}
