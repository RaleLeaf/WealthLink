package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected ImageView ivMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().getDecorView().setOnApplyWindowInsetsListener((v, insets) -> {
            WindowInsetsCompat systemInsets = WindowInsetsCompat.toWindowInsetsCompat(insets);
            v.setPadding(0, systemInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top, 0, 0);
            return insets;
        });
    }

    protected void setupDrawer(int layoutResID) {
        setContentView(layoutResID);

        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);
        navigationView = findViewById(R.id.navigation_view);

        if (ivMenu != null) {
            ivMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        if (navigationView != null) {
            LinearLayout navHome = navigationView.findViewById(R.id.nav_home);
            LinearLayout navGroups = navigationView.findViewById(R.id.nav_groups);
            LinearLayout navNotifications = navigationView.findViewById(R.id.nav_notifications);
            LinearLayout navHistory = navigationView.findViewById(R.id.nav_history);
            LinearLayout navAccount = navigationView.findViewById(R.id.nav_account);

            TextView userName = findViewById(R.id.user_name);
            TextView userEmail = findViewById(R.id.user_email);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            String userId = currentUser.getUid();
            DocumentReference userDocRef = db.collection("users").document(userId);

            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String fname = documentSnapshot.getString("firstName");
                    String mname = documentSnapshot.getString("middleName");
                    String lname = documentSnapshot.getString("lastName");

                    if (fname == null) fname = "";
                    if (lname == null) lname = "";

                    String fullName = fname + (mname != null ? " " + mname : "") + " " + lname;
                    userName.setText(fullName.trim());

                    String email = currentUser.getEmail();
                    userEmail.setText(email.trim());

                } else {
                    // Document does not exist
                }
            }).addOnFailureListener(e -> {
                // Handle any errors
            });

            if (navHome != null) {
                navHome.setOnClickListener(v -> navigateTo(wealthLinkMainPage.class));
            }
            if (navGroups != null) {
                navGroups.setOnClickListener(v -> navigateTo(GroupListActivity.class));
            }
            if (navNotifications != null) {
                navNotifications.setOnClickListener(v -> navigateTo(Notifications.class));
            }
            if (navHistory != null) {
                navHistory.setOnClickListener(v -> navigateTo(TransactionHistory.class));
            }
            if (navAccount != null) {
                navAccount.setOnClickListener(v -> navigateTo(AccountView.class));
            }
        }
    }

    protected void navigateTo(Class<?> targetActivity) {
        if (!this.getClass().equals(targetActivity)) {
            Intent intent = new Intent(this, targetActivity);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}
