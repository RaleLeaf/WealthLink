package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected ImageView ivMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

            if (navHome != null) {
                navHome.setOnClickListener(v -> navigateTo(wealthLinkMainPage.class));
            }
            if (navGroups != null) {
                navGroups.setOnClickListener(v -> showToast("Groups clicked"));
            }
            if (navNotifications != null) {
                navNotifications.setOnClickListener(v -> showToast("Notifications clicked"));
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
