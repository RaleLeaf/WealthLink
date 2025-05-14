package com.example.wealthlink;

public class Activity {
    private String title;
    private String subtitle;
    private String value;
    private String change;
    private boolean isNegativeChange;

    public Activity(String title, String subtitle, String value, String change, boolean isNegativeChange) {
        this.title = title;
        this.subtitle = subtitle;
        this.value = value;
        this.change = change;
        this.isNegativeChange = isNegativeChange;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getValue() {
        return value;
    }

    public String getChange() {
        return change;
    }

    public boolean isNegativeChange() {
        return isNegativeChange;
    }
}