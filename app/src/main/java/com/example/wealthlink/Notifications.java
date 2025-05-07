package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends BaseActivity {

    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    private RecyclerView rvNotifications;
    private TextView tvAll, tvUnread, tvMarkAllRead;
    private LinearLayout accountPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupDrawer(R.layout.activity_notifications);

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);
        rvNotifications = findViewById(R.id.rvNotifications);
        tvAll = findViewById(R.id.tvAll);
        tvUnread = findViewById(R.id.tvUnread);
        tvMarkAllRead = findViewById(R.id.tvMarkAllRead);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        accountPage = navigationView.findViewById(R.id.nav_account);

        // Set up menu button click listener
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set up filter tab listeners
        setupFilterTabs();

        // Setup navigation drawer items
        setupNavigationItems(navigationView);

        // Set up notifications list
        setupNotificationsList();

        // Set up mark all as read functionality
        tvMarkAllRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAllAsRead();
            }
        });
    }

    private void setupFilterTabs() {
        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAll.setTextColor(getResources().getColor(android.R.color.black));
                tvUnread.setTextColor(getResources().getColor(android.R.color.darker_gray));
                tvUnread.setTypeface(null);
                // Filter notifications to show all
                // For this demo, we'll just refresh the list
                setupNotificationsList();
            }
        });

        tvUnread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvUnread.setTextColor(getResources().getColor(android.R.color.black));
                tvUnread.setTypeface(tvUnread.getTypeface(), android.graphics.Typeface.BOLD);
                tvAll.setTextColor(getResources().getColor(android.R.color.darker_gray));
                tvAll.setTypeface(null);
                // Filter notifications to show only unread
                // For this demo, we'll just refresh the list with the same data
                setupNotificationsList();
            }
        });
    }

    private void setupNavigationItems(NavigationView navigationView) {
        // Account navigation
        accountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAccount = new Intent(Notifications.this, AccountView.class);
                startActivity(viewAccount);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Home navigation
        LinearLayout navHome = navigationView.findViewById(R.id.nav_home);
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goHome = new Intent(Notifications.this, wealthLinkMainPage.class);
                startActivity(goHome);
                drawerLayout.closeDrawer(GravityCompat.START);
                finish();
            }
        });

        // Add other navigation item click listeners as needed
    }

    private void setupNotificationsList() {
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        // Sample notification data
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification("2:31 AM October 25, 2023",
                "John changed the contribution rules for Liv", false));
        notifications.add(new Notification("2:31 AM October 25, 2023",
                "John changed the contribution rules for Liv", false));
        notifications.add(new Notification("2:31 AM October 25, 2023",
                "John changed the contribution rules for Liv", true));
        notifications.add(new Notification("2:31 AM October 25, 2023",
                "John changed the contribution rules for Liv", true));

        // Set adapter
        NotificationAdapter adapter = new NotificationAdapter(notifications);
        rvNotifications.setAdapter(adapter);
    }

    private void markAllAsRead() {
        NotificationAdapter adapter = (NotificationAdapter) rvNotifications.getAdapter();
        if (adapter != null) {
            adapter.markAllAsRead();
        }
        tvUnread.setText("Unread (0)");
    }

    // Notification data class
    public static class Notification {
        private String timestamp;
        private String content;
        private boolean read;

        public Notification(String timestamp, String content, boolean read) {
            this.timestamp = timestamp;
            this.content = content;
            this.read = read;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getContent() {
            return content;
        }

        public boolean isRead() {
            return read;
        }

        public void setRead(boolean read) {
            this.read = read;
        }
    }

    // Notification adapter
    public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
        private List<Notification> notifications;

        public NotificationAdapter(List<Notification> notifications) {
            this.notifications = notifications;
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            Notification notification = notifications.get(position);
            holder.tvTimestamp.setText(notification.getTimestamp());
            holder.tvNotificationContent.setText(notification.getContent());

            // Change indicator color based on read status
            if (notification.isRead()) {
                holder.notificationIndicator.setAlpha(0.5f); // Make it lighter if read
            } else {
                holder.notificationIndicator.setAlpha(1.0f); // Full opacity if unread
            }

            // Set click listener to mark as read
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        notifications.get(adapterPosition).setRead(true);
                        notifyItemChanged(adapterPosition);
                        updateUnreadCount();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        public void markAllAsRead() {
            for (Notification notification : notifications) {
                notification.setRead(true);
            }
            notifyDataSetChanged();
        }

        private void updateUnreadCount() {
            int unreadCount = 0;
            for (Notification notification : notifications) {
                if (!notification.isRead()) {
                    unreadCount++;
                }
            }
            tvUnread.setText("Unread (" + unreadCount + ")");
        }

        class NotificationViewHolder extends RecyclerView.ViewHolder {
            TextView tvTimestamp;
            TextView tvNotificationContent;
            View notificationIndicator;

            public NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
                tvNotificationContent = itemView.findViewById(R.id.tvNotificationContent);
                notificationIndicator = itemView.findViewById(R.id.notificationIndicator);
            }
        }
    }
}