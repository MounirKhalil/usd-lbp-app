package com.husseinamro.exchange.api.model;

import com.google.gson.annotations.SerializedName;
public class Transaction {
    @SerializedName("usd_amount")
    public Float usdAmount;
    @SerializedName("lbp_amount")
    public Float lbpAmount;
    @SerializedName("usd_to_lbp")
    public Boolean usdToLbp;
    @SerializedName("id")
    public Integer id;
    @SerializedName("added_date")
    public String addedDate;

    public Transaction(Float usdAmount, Float lbpAmount, Boolean usdToLbp)
    {
        this.usdAmount = usdAmount;
        this.lbpAmount = lbpAmount;
        this.usdToLbp = usdToLbp;
    }

    public Float getUsdAmount() {
        return usdAmount;
    }

    public Float getLbpAmount() {
        return lbpAmount;
    }

    public Boolean getUsdToLbp() {
        return usdToLbp;
    }

    public Integer getId() {
        return id;
    }

    public String getAddedDate() { return addedDate; }
}