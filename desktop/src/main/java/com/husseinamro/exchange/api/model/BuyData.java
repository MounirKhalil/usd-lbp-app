package com.husseinamro.exchange.api.model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

public class BuyData {
    @SerializedName("Buy usd")
    public Float buyUsd;

    @SerializedName("Number")
    public String number;

    @SerializedName("Date")
    public LocalDateTime date;

    // Constructor
    public BuyData(Float buyUsd, String number, LocalDateTime date) {
        this.buyUsd = buyUsd;
        this.number = number;
        this.date = date;
    }

    public Float getBuyUsd() {
        return buyUsd;
    }

    public void setBuyUsd(Float buyUsd) {
        this.buyUsd = buyUsd;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
