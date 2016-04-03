package com.number26.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Transaction implements Serializable {

    @SerializedName("amount") protected double amount;
    @SerializedName("type") protected String type;
    @SerializedName("parent_id") protected Long parentID = null;

    protected transient double childAmount = 0;

    public Transaction(double amount, String type) {
        this.amount = amount;
        this.type = type;
    }

    public Transaction(double amount, String type, long parentID) {
        this.amount = amount;
        this.type = type;
        this.parentID = parentID;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public Long getParentID() {
        return parentID;
    }

    public double getChildAmount() {
        return childAmount;
    }

    public void addChildAmount(double amount) {
        this.childAmount += amount;
    }
}
