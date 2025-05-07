package com.example.wealthlink;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GroupSearch extends AppCompatActivity {

    private RecyclerView rvGroups;
    private GroupAdapter adapter;
    private List<Group> groupList;
    private List<Group> filteredList;
    private EditText etSearch;
    private TextView tvFilter;
    private Button btnCreateGroup, btnJoinViaInvite;
    private boolean isFilteringByStocks = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);

        // Initialize views
        rvGroups = findViewById(R.id.rvGroups);
        etSearch = findViewById(R.id.etSearch);
        tvFilter = findViewById(R.id.tvFilter);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnJoinViaInvite = findViewById(R.id.btnJoinViaInvite);

        // Set up group data
        setupGroups();

        // Set up RecyclerView
        filteredList = new ArrayList<>(groupList);
        adapter = new GroupAdapter(filteredList);
        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        rvGroups.setAdapter(adapter);

        // Set up search functionality
        setupSearch();

        // Set up filter by stocks functionality
        setupFilter();

        // Set up button click listeners
        setupButtons();
    }

    private void setupGroups() {
        // Initialize data lists
        groupList = new ArrayList<>();

        // Add sample groups with appropriate fields for the adapter
        groupList.add(new Group("Lively Inc.", "Today", "$432.21"));
        groupList.add(new Group("Ardy Inc.", "Yesterday", "$245.89"));
        groupList.add(new Group("Urdan Inc.", "Today", "$213.22"));
        groupList.add(new Group("Holly Inc.", "Yesterday", "$821.87"));
        groupList.add(new Group("Sicole Inc.", "Today", "$245.32"));
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the list based on search text
                filterGroups(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void setupFilter() {
        tvFilter.setOnClickListener(v -> {
            isFilteringByStocks = !isFilteringByStocks;
            if (isFilteringByStocks) {
                tvFilter.setText("Filter by Stocks (Active)");
                // Sort the list by stock amount
                sortByStockAmount();
            } else {
                tvFilter.setText("Filter by Stocks");
                // Reset to original order
                refreshList();
            }
        });
    }

    private void setupButtons() {
        btnCreateGroup.setOnClickListener(v -> {
            Toast.makeText(this, "Create Group functionality to be implemented", Toast.LENGTH_SHORT).show();
            // Navigate to create group screen
            // Intent intent = new Intent(GroupSearch.this, CreateGroupActivity.class);
            // startActivity(intent);
        });

        btnJoinViaInvite.setOnClickListener(v -> {
            Toast.makeText(this, "Join Via Invite functionality to be implemented", Toast.LENGTH_SHORT).show();
            // Show join via invite dialog
            // showJoinViaInviteDialog();
        });
    }

    private void filterGroups(String query) {
        // Re-create the adapter with filtered data
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(groupList);
        } else {
            String lowercaseQuery = query.toLowerCase();
            for (Group group : groupList) {
                if (group.getName().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(group);
                }
            }
        }

        if (isFilteringByStocks) {
            sortByStockAmount();
        }

        refreshList();
    }

    private void sortByStockAmount() {
        // Simple implementation: sort by amount
        List<Group> sortedList = new ArrayList<>(filteredList);
        sortedList.sort((g1, g2) -> {
            // Remove $ sign and parse as double
            double amount1 = Double.parseDouble(g1.getAmount().substring(1));
            double amount2 = Double.parseDouble(g2.getAmount().substring(1));
            // Sort in descending order (highest amount first)
            return Double.compare(amount2, amount1);
        });

        filteredList.clear();
        filteredList.addAll(sortedList);
    }

    private void refreshList() {
        // Create a new adapter with the updated list
        adapter = new GroupAdapter(filteredList);
        rvGroups.setAdapter(adapter);
    }
}