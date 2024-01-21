package com.husseinamro.exchange.api.model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

public class SellData {
    @SerializedName("Sell usd")
    public Float sellUsd;

    @SerializedName("Number")
    public String number;

    @SerializedName("Date")
    public LocalDateTime date;

    public SellData(Float sellUsd, String number, LocalDateTime date) {
        this.sellUsd = sellUsd;
        this.number = number;
        this.date = date;
    }

    public Float getBuyUsd() {
        return sellUsd;
    }

    public void setSellUsd(Float sellUsd) {
        this.sellUsd = sellUsd;
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
