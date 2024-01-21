package com.husseinamro.exchange.api.model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

public class OverallSell {
    @SerializedName("Overall Sell usd")
    public Float OverallSellUsd;

    @SerializedName("Number")
    public String number;

    @SerializedName("Date")
    public LocalDateTime date;

    public OverallSell(Float OverallSellUsd, String number, LocalDateTime date) {
        this.OverallSellUsd = OverallSellUsd;
        this.number = number;
        this.date = date;
    }

    public Float getBuyUsd() {
        return OverallSellUsd;
    }

    public void setOverallBuyUsd(Float OverallBuyUsd) {
        this.OverallSellUsd = OverallBuyUsd;
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
