package com.example.wealthlink;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;

public class NavBar extends AppCompatActivity {

    // views
    private NavigationView navigationView;

    // menu items
    private LinearLayout navHome;
    private LinearLayout navGroups;
    private LinearLayout navNotifications;
    private LinearLayout navHistory;
    private LinearLayout navAccount;

    // Profile
    private ImageView profileImage;
    private TextView userName;
    private TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_bar);

        //views
        navigationView = findViewById(R.id.navigation_view);

        //menu
        navHome = findViewById(R.id.nav_home);
        navGroups = findViewById(R.id.nav_groups);
        navNotifications = findViewById(R.id.nav_notifications);
        navHistory = findViewById(R.id.nav_history);
        navAccount = findViewById(R.id.nav_account);

        //profile
        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
    }
}