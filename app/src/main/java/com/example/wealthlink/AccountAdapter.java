package com.example.wealthlink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<Account> accountList;

    public AccountAdapter(List<Account> accountList) {
        this.accountList = accountList;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.tvBankName.setText(account.getBankName());
        holder.tvAccountHolder.setText(account.getAccountHolder());
        // You can set an actual image later
        holder.ivAccountIcon.setImageResource(R.drawable.profile_pic);
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAccountIcon;
        TextView tvBankName, tvAccountHolder;
        ImageView ivMenu;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAccountIcon = itemView.findViewById(R.id.ivAccountIcon);
            tvBankName = itemView.findViewById(R.id.tvBankName);
            tvAccountHolder = itemView.findViewById(R.id.tvAccountHolder);
            ivMenu = itemView.findViewById(R.id.ivMenu);
        }
    }
}
