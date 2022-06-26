package com.crypto.Koinex;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface CoinmarketcapApi {


    @GET("coins/markets")
    Call<JsonArray> getCoins (
          @QueryMap Map<String, String> parameters

    );

    @GET("coins/{id}")
    Call<JsonObject> getDetails (
            @Path("id") String id,
            @QueryMap Map<String, String> parameters
    );
}
