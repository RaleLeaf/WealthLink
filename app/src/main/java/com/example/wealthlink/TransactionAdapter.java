package com.example.wealthlink;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<transactionItemClass> transactionList;

    public TransactionAdapter(List<transactionItemClass> transactionList) {
        this.transactionList = new ArrayList<>(transactionList);
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        transactionItemClass item = transactionList.get(position);
        holder.textTitle.setText(item.getTitle());
        holder.textDate.setText(item.getDate());
        holder.textAmount.setText(item.getAmount());

        if (item.isIncome()) {
            holder.textAmount.setTextColor(Color.parseColor("#00AA00")); // green
            holder.imageIcon.setImageResource(R.drawable.ic_income);
        } else {
            holder.textAmount.setTextColor(Color.parseColor("#FF0000")); // red
            holder.imageIcon.setImageResource(R.drawable.ic_expense);
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    // Method to update transaction list
    public void updateTransactions(List<transactionItemClass> newTransactions) {
        // Create a new list to avoid modification issues
        this.transactionList = new ArrayList<>(newTransactions);
        notifyDataSetChanged();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDate, textAmount;
        ImageView imageIcon;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDate = itemView.findViewById(R.id.textDate);
            textAmount = itemView.findViewById(R.id.textAmount);
            imageIcon = itemView.findViewById(R.id.imageIcon);
        }
    }
}