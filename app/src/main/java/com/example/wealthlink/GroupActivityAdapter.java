package com.example.wealthlink;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupActivityAdapter extends RecyclerView.Adapter<GroupActivityAdapter.ActivityViewHolder> {

    private List<GroupActivityModel> activityList;

    public GroupActivityAdapter(List<GroupActivityModel> activityList) {
        this.activityList = activityList;
    }

    public void updateActivities(List<GroupActivityModel> newActivities) {
        this.activityList = newActivities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_log, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        GroupActivityModel activity = activityList.get(position);

        holder.textTitle.setText(activity.getTitle());
        holder.textByUser.setText("By: " + activity.getByUser());
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
            holder.imageIcon.setImageResource(R.drawable.ic_withdraw); // Assuming you have this drawable
        } else {
            holder.imageIcon.setImageResource(R.drawable.ic_deposit); // Assuming you have this drawable
        }
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIcon;
        TextView textTitle;
        TextView textByUser;
        TextView textDate;
        TextView textAmount;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.imageIcon);
            textTitle = itemView.findViewById(R.id.textTitle);
            textByUser = itemView.findViewById(R.id.textByUser);
            textDate = itemView.findViewById(R.id.textDate);
            textAmount = itemView.findViewById(R.id.textAmount);
        }
    }
}