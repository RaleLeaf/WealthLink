package com.example.wealthlink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groups;

    public GroupAdapter(List<Group> groups) {
        this.groups = groups;
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGroupIcon;
        TextView tvGroupName;
        TextView tvGroupTime;
        TextView tvGroupAmount;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGroupIcon = itemView.findViewById(R.id.ivGroupIcon);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvGroupTime = itemView.findViewById(R.id.tvGroupTime);
            tvGroupAmount = itemView.findViewById(R.id.tvGroupAmount);
        }
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.tvGroupName.setText(group.getName());
        holder.tvGroupTime.setText(group.getTime());
        holder.tvGroupAmount.setText(group.getAmount());
        // You can set an icon here if needed
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
}