package com.husseinamro.exchange.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class ExchangeService {
    static String API_URL = "http://localhost:5000";
    public static Exchange exchangeApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(Exchange.class);
    }
}