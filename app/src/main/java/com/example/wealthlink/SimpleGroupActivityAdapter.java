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

public class SimpleGroupActivityAdapter extends RecyclerView.Adapter<SimpleGroupActivityAdapter.ViewHolder> {
    private List<SimpleGroupActivityModel> activities;

    public SimpleGroupActivityAdapter() {
        this.activities = new ArrayList<>();
    }

    public void setActivities(List<SimpleGroupActivityModel> activities) {
        this.activities = activities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimpleGroupActivityModel activity = activities.get(position);

        holder.textTitle.setText(activity.getTitle());
        holder.textByUser.setText("By: " + activity.getUserName());
        holder.textDate.setText(activity.getDate());
        holder.textAmount.setText(activity.getAmount());

        // Set text color based on transaction type
        if (activity.isWithdrawal()) {
            holder.textAmount.setTextColor(Color.parseColor("#F44336")); // Red for withdrawals
        } else {
            holder.textAmount.setTextColor(Color.parseColor("#4CAF50")); // Green for deposits
        }

        // Set icon based on transaction type
        if (activity.isWithdrawal()) {
            holder.imageIcon.setImageResource(R.drawable.ic_withdraw);
        } else {
            holder.imageIcon.setImageResource(R.drawable.ic_deposit);
        }
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIcon;
        TextView textTitle;
        TextView textByUser;
        TextView textDate;
        TextView textAmount;

        ViewHolder(View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.imageIcon);
            textTitle = itemView.findViewById(R.id.textTitle);
            textByUser = itemView.findViewById(R.id.textByUser);
            textDate = itemView.findViewById(R.id.textDate);
            textAmount = itemView.findViewById(R.id.textAmount);
        }
    }
}