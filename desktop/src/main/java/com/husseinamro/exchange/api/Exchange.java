package com.husseinamro.exchange.api;

import com.husseinamro.exchange.api.model.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.util.List;
import java.util.Map;

public interface Exchange {
    @POST("/user")
    Call<User> addUser(@Body User user);
    @POST("/authentication")
    Call<Token> authenticate(@Body User user);
    @GET("/exchangeRate")
    Call<ExchangeRates> getExchangeRates();
    @POST("/transaction")
    Call<Object> addTransaction(@Body Transaction transaction,
                                @Header("Authorization") String authorization);
    @GET("/transactionAndr")
    Call<List<Transaction>> getTransactions(@Header("Authorization") String authorization);
    @GET("/graph_sell")
    Call<List<SellData>> getSellData(@Header("Authorization") String authorization);
    @GET("/graph_buy")
    Call<List<BuyData>> getBuyData(@Header("Authorization") String authorization);

    @GET("/graph_overallsell")
    Call<List<OverallSell>> getOverallBuy(@Header("Authorization") String authorization);
    @GET("/graph_overallbuy")
    Call<List<OverallBuy>> getOverallSell(@Header("Authorization") String authorization);

}
