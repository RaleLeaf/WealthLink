package com.example.wealthlink;

public class SimpleGroupActivityModel {
    private String title;
    private String userName;
    private String date;
    private String amount;
    private boolean isWithdrawal;

    public SimpleGroupActivityModel(String title, String userName, String date, String amount, boolean isWithdrawal) {
        this.title = title;
        this.userName = userName;
        this.date = date;
        this.amount = amount;
        this.isWithdrawal = isWithdrawal;
    }

    public String getTitle() {
        return title;
    }

    public String getUserName() {
        return userName;
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