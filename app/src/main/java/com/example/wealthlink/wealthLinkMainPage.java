package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.view.Gravity;
import android.widget.LinearLayout;
import com.google.android.material.navigation.NavigationView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wealthlink.R;

import java.util.ArrayList;
import java.util.List;

public class wealthLinkMainPage extends BaseActivity {
    private RecyclerView rvGroups;
    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    LinearLayout accountPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupDrawer(R.layout.activity_wealth_link_main_page);
        setContentView(R.layout.activity_wealth_link_main_page);

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
    }
}
