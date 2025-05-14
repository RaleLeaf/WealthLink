package com.example.wealthlink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private List<Activity> activities;

    public ActivityAdapter(List<Activity> activities) {
        this.activities = activities;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Activity activity = activities.get(position);
        holder.tvTitle.setText(activity.getTitle());
        holder.tvSubtitle.setText(activity.getSubtitle());
        holder.tvValue.setText(activity.getValue());
        holder.tvChange.setText(activity.getChange());

        // Set text color based on whether change is positive or negative
        if (activity.isNegativeChange()) {
            holder.tvChange.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvChange.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public void updateActivities(List<Activity> newActivities) {
        this.activities = newActivities;
        notifyDataSetChanged();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvValue, tvChange;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvActivityTitle);
            tvSubtitle = itemView.findViewById(R.id.tvActivitySubtitle);
            tvValue = itemView.findViewById(R.id.tvActivityValue);
            tvChange = itemView.findViewById(R.id.tvActivityChange);
        }
    }
}