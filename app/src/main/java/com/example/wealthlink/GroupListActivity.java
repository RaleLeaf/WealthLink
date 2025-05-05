package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class GroupListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupDrawer(R.layout.activity_group_list);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        LinearLayout accountPage = navigationView.findViewById(R.id.nav_account);

        accountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAccount = new Intent(GroupListActivity.this, AccountView.class);
                startActivity(viewAccount);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }
}
