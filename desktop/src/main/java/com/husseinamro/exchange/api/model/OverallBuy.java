package com.husseinamro.exchange.api.model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

public class OverallBuy {
    @SerializedName("Overall Buy usd")
    public Float OverallBuyUsd;

    @SerializedName("Number")
    public String number;

    @SerializedName("Date")
    public LocalDateTime date;

    public OverallBuy(Float buyUsd, String number, LocalDateTime date) {
        this.OverallBuyUsd = buyUsd;
        this.number = number;
        this.date = date;
    }

    public Float getBuyUsd() {
        return OverallBuyUsd;
    }

    public void setOverallBuyUsd(Float OverallBuyUsd) {
        this.OverallBuyUsd = OverallBuyUsd;
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
