package com.example.wealthlink;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class wealthLinkMainPage extends AppCompatActivity {
    private GroupAdapter groupAdapter;
    private RecyclerView rvGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wealth_link_main_page);

        // Configure the logo to display properly (if needed)
        ImageView logoImageView = findViewById(R.id.ivLogo);
        // Make sure the PNG is properly scaled to fit while maintaining aspect ratio
        logoImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Initialize RecyclerView
        rvGroups = findViewById(R.id.rvGroups);
        rvGroups.setLayoutManager(new LinearLayoutManager(this));

        // Sample data for demo purposes
        List<Group> groups = new ArrayList<>();
        groups.add(new Group("Lively Inc.", "21:21", "$432"));
        groups.add(new Group("Lively Inc.", "21:21", "$432"));
        // Add more sample data as needed

        // Set up adapter
        groupAdapter = new GroupAdapter(groups);
        rvGroups.setAdapter(groupAdapter);
    }
}