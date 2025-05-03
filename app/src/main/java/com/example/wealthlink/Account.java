package com.example.wealthlink;

public class Account {
    private String bankName;
    private String accountHolder;

    public Account(String bankName, String accountHolder) {
        this.bankName = bankName;
        this.accountHolder = accountHolder;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountHolder() {
        return accountHolder;
    }
}
