package com.example.wealthlink;

public class Group {
    private String name;
    private String time;
    private String amount;

    public Group(String name, String time, String amount) {
        this.name = name;
        this.time = time;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}