package com.example.wealthlink;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private static final String TAG = "GroupAdapter";
    private List<Group> groups;
    private List<Group> filteredGroups; // For filtered search results
    private Map<String, String> groupIdsMap; // To store groupName -> groupId mapping
    private Context context;

    public GroupAdapter(List<Group> groups) {
        this.groups = groups;
        this.filteredGroups = new ArrayList<>(groups);
        this.groupIdsMap = new HashMap<>();
    }

    public GroupAdapter(List<Group> groups, Map<String, String> groupIdsMap, Context context) {
        this.groups = groups;
        this.filteredGroups = new ArrayList<>(groups);
        this.groupIdsMap = groupIdsMap != null ? groupIdsMap : new HashMap<>();
        this.context = context;
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGroupIcon;
        TextView tvGroupName;
        TextView tvGroupTime;
        TextView tvGroupAmount;
        View itemView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivGroupIcon = itemView.findViewById(R.id.ivGroupIcon);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvGroupTime = itemView.findViewById(R.id.tvGroupTime);
            tvGroupAmount = itemView.findViewById(R.id.tvGroupAmount);
        }
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = filteredGroups.get(position);
        holder.tvGroupName.setText(group.getName());
        holder.tvGroupTime.setText(group.getTime());
        holder.tvGroupAmount.setText(group.getAmount());

        // Get the groupId from the map using the group name
        final String groupId = groupIdsMap.get(group.getName());

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupId != null) {
                    checkMembershipAndNavigate(groupId);
                } else {
                    Log.e(TAG, "Group ID not found for group: " + group.getName());
                    // Show a toast message to the user
                    Toast.makeText(context,
                            "Could not load group details. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredGroups.size();
    }

    /**
     * Filter the list of groups based on search query
     * @param query The search query entered by the user
     */
    public void filter(String query) {
        filteredGroups.clear();

        if (query.isEmpty()) {
            // If query is empty, show all groups
            filteredGroups.addAll(groups);
        } else {
            // Convert query to lowercase for case-insensitive search
            String lowerCaseQuery = query.toLowerCase();

            // Filter groups that contain the query string in their name
            for (Group group : groups) {
                if (group.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredGroups.add(group);
                }
            }
        }

        // Notify adapter that data has changed to refresh the RecyclerView
        notifyDataSetChanged();
    }

    private void checkMembershipAndNavigate(final String groupId) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e(TAG, "No current user found");
            Toast.makeText(context, "Please sign in to view group details", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the groupMemberships collection to check if user is a member
        db.collection("groupMemberships")
                .whereEqualTo("userID", currentUserId)
                .whereEqualTo("groupID", groupId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isMember = !task.getResult().isEmpty();

                            try {
                                if (isMember) {
                                    // User is a member, navigate to GroupDetails
                                    Intent intent = new Intent(context, GroupDetails.class);
                                    intent.putExtra("groupID", groupId);
                                    context.startActivity(intent);
                                    Log.d(TAG, "Navigating to GroupDetails for groupID: " + groupId);
                                } else {
                                    // User is not a member, navigate to GroupActivityLog
                                    Intent intent = new Intent(context, GroupActivityLog.class);
                                    intent.putExtra("groupID", groupId);
                                    context.startActivity(intent);
                                    Log.d(TAG, "Navigating to GroupActivityLog for groupID: " + groupId);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error starting activity", e);
                                Toast.makeText(context,
                                        "Error opening group. Please try again.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "Error checking membership: ", task.getException());
                            Toast.makeText(context,
                                    "Error connecting to server. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}