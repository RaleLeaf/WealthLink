package com.example.wealthlink;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class GroupOverview extends AppCompatActivity {

    private ImageButton btnMore;
    private CardView cardDropdown;
    private RecyclerView rvExpenses;
    private ExpenseAdapter expenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_overview);

        // Initialize views
        btnMore = findViewById(R.id.btnMore);
        cardDropdown = findViewById(R.id.cardDropdown);
        rvExpenses = findViewById(R.id.rvExpenses);

        // Set up dropdown menu
        setupDropdownMenu();

        // Set up back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Setup chart
        setupChart();

        // Setup expenses recycler view
        setupExpensesRecyclerView();
    }

    private void setupDropdownMenu() {
        // Toggle dropdown visibility on button click
        btnMore.setOnClickListener(v -> {
            if (cardDropdown.getVisibility() == View.VISIBLE) {
                cardDropdown.setVisibility(View.GONE);
            } else {
                cardDropdown.setVisibility(View.VISIBLE);
            }
        });

        // Set click listeners for dropdown items
        TextView tvSettings = findViewById(R.id.tvSettings);
        TextView tvReportIssue = findViewById(R.id.tvReportIssue);
        TextView tvLeaveGroup = findViewById(R.id.tvLeaveGroup);

        tvSettings.setOnClickListener(v -> {
            // Handle settings click
            cardDropdown.setVisibility(View.GONE);
            // Navigate to settings or show settings dialog
        });

        tvReportIssue.setOnClickListener(v -> {
            // Handle report issue click
            cardDropdown.setVisibility(View.GONE);
            // Navigate to report issue screen or show report dialog
        });

        tvLeaveGroup.setOnClickListener(v -> {
            // Handle leave group click
            cardDropdown.setVisibility(View.GONE);
            // Show leave group confirmation dialog
        });
    }

    private void setupChart() {
        // For the placeholder version, we'll simply use an ImageView
        // The actual graph implementation would need a proper charting library
        // This is just a placeholder that would be replaced with actual chart in production

        // Note: This assumes you have a placeholder chart drawable
        // You would need to add a chart_placeholder.xml in your drawable resources
        ImageView chartPlaceholder = findViewById(R.id.lineChart);
        chartPlaceholder.setImageResource(R.drawable.chart_placeholder);
    }

    private void setupExpensesRecyclerView() {
        // Create sample expenses data
        List<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense("Price", "Per", "$872.75", "-12.14", false));
        expenses.add(new Expense("Price", "Per", "$982.98", "-32.89", false));
        expenses.add(new Expense("Dividend", "Quarterly", "$14.25", "+2.35", true));
        expenses.add(new Expense("Commission", "Trade", "$2.99", "", false));
        expenses.add(new Expense("Fee", "Management", "$5.50", "", false));

        // Set up RecyclerView
        expenseAdapter = new ExpenseAdapter(expenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        rvExpenses.setAdapter(expenseAdapter);
    }

    // Model class for expenses
    public static class Expense {
        private String type;
        private String description;
        private String amount;
        private String change;
        private boolean isPositive;

        public Expense(String type, String description, String amount, String change, boolean isPositive) {
            this.type = type;
            this.description = description;
            this.amount = amount;
            this.change = change;
            this.isPositive = isPositive;
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public String getAmount() {
            return amount;
        }

        public String getChange() {
            return change;
        }

        public boolean isPositive() {
            return isPositive;
        }
    }

    // Adapter for the expenses RecyclerView
    private static class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
        private List<Expense> expenses;

        public ExpenseAdapter(List<Expense> expenses) {
            this.expenses = expenses;
        }

        @Override
        public ExpenseViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_expense, parent, false);
            return new ExpenseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ExpenseViewHolder holder, int position) {
            Expense expense = expenses.get(position);
            holder.tvExpenseType.setText(expense.getType());
            holder.tvExpenseDescription.setText(expense.getDescription());
            holder.tvExpenseAmount.setText(expense.getAmount());

            if (!expense.getChange().isEmpty()) {
                holder.tvExpenseChange.setText(expense.getChange());
                holder.tvExpenseChange.setTextColor(expense.isPositive() ?
                        Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));
            } else {
                holder.tvExpenseChange.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return expenses.size();
        }

        static class ExpenseViewHolder extends RecyclerView.ViewHolder {
            TextView tvExpenseType;
            TextView tvExpenseDescription;
            TextView tvExpenseAmount;
            TextView tvExpenseChange;

            public ExpenseViewHolder(View itemView) {
                super(itemView);
                tvExpenseType = itemView.findViewById(R.id.tvExpenseType);
                tvExpenseDescription = itemView.findViewById(R.id.tvExpenseDescription);
                tvExpenseAmount = itemView.findViewById(R.id.tvExpenseAmount);
                tvExpenseChange = itemView.findViewById(R.id.tvExpenseChange);
            }
        }
    }
}