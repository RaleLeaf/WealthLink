package com.example.wealthlink;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GroupsPage extends AppCompatActivity {

    private RecyclerView rvGroups;
    private GroupAdapter adapter;
    private List<Group> groupList;
    private List<Group> filteredList;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_page);

        // Initialize views
        rvGroups = findViewById(R.id.rvGroups);
        etSearch = findViewById(R.id.etSearch);

        // Set up group data
        setupGroups();

        // Set up RecyclerView
        adapter = new GroupAdapter(filteredList);
        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        rvGroups.setAdapter(adapter);

    }

    private void setupGroups() {
        // Initialize data lists
        groupList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // groups sample
        groupList.add(new Group("Lively Inc.", "LIV", "$432\n+.21"));
        groupList.add(new Group("Lively Inc.", "LIV", "$432\n+.21"));
        groupList.add(new Group("Lively Inc.", "LIV", "$432\n+.21"));
        groupList.add(new Group("Lively Inc.", "LIV", "$432\n+.21"));
        groupList.add(new Group("Lively Inc.", "LIV", "$432\n+.21"));

        // Initialize filtered list with all groups
        filteredList.addAll(groupList);
    }

}