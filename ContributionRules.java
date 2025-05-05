package com.example.wealthlink;

import java.util.Date;

/**
 * Model class for storing group contribution rules
 */
public class ContributionRules {
    private String frequency;
    private String amountType; // "Fixed" or "Flexible"
    private double minimumAmount;
    private Date startDate;
    private Date endDate;
    private String visibility; // "Public", "Private", or "Hidden"
    private String joinMethod; // "Invite-only", "Application", or "Open join"

    // Default constructor
    public ContributionRules() {
        this.frequency = "Biweekly";
        this.amountType = "Fixed";
        this.minimumAmount = 0.0;
        this.startDate = null;
        this.endDate = null;
        this.visibility = "Private";
        this.joinMethod = "Invite-only";
    }

    // Constructor with all fields
    public ContributionRules(String frequency, String amountType, double minimumAmount,
                             Date startDate, Date endDate, String visibility, String joinMethod) {
        this.frequency = frequency;
        this.amountType = amountType;
        this.minimumAmount = minimumAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visibility = visibility;
        this.joinMethod = joinMethod;
    }

    // Getters and setters
    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getAmountType() {
        return amountType;
    }

    public void setAmountType(String amountType) {
        this.amountType = amountType;
    }

    public double getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(double minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getJoinMethod() {
        return joinMethod;
    }

    public void setJoinMethod(String joinMethod) {
        this.joinMethod = joinMethod;
    }

    // Method to format date range as a string
    public String getFormattedDateRange() {
        if (startDate == null && endDate == null) {
            return "-";
        } else if (startDate != null && endDate == null) {
            return "From " + startDate.toString() + " onwards";
        } else if (startDate == null && endDate != null) {
            return "Until " + endDate.toString();
        } else {
            return startDate.toString() + " - " + endDate.toString();
        }
    }

    // Method to format minimum amount as a string
    public String getFormattedMinimumAmount() {
        if (amountType.equals("Fixed") || minimumAmount <= 0) {
            return "-";
        } else {
            return String.format("$%.2f", minimumAmount);
        }
    }
}