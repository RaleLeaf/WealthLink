package com.example.wealthlink;

public class transactionItemClass {
    private String title;
    private String date;
    private String amount;
    private boolean isIncome;

    public transactionItemClass(String title, String date, String amount, boolean isIncome) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.isIncome = isIncome;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getAmount() { return amount; }
    public boolean isIncome() { return isIncome; }
}

