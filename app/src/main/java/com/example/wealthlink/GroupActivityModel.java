package com.example.wealthlink;

import java.util.Date;

public class GroupActivityModel {
    private String title;
    private String byUser;
    private String date;
    private String amount;
    private boolean isWithdrawal;

    public GroupActivityModel(String title, String byUser, String date, String amount, boolean isWithdrawal) {
        this.title = title;
        this.byUser = byUser;
        this.date = date;
        this.amount = amount;
        this.isWithdrawal = isWithdrawal;
    }

    public String getTitle() {
        return title;
    }

    public String getByUser() {
        return byUser;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public boolean isWithdrawal() {
        return isWithdrawal;
    }
}