package com.crypto.Koinex;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.crypto.Koinex.R;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoinDetailsActivity extends AppCompatActivity {

    private CoinmarketcapApi coinmarketcapApi;
    String mID = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_details);
        //Toast.makeText(CoinDetailsActivity.this, mID, Toast.LENGTH_SHORT).show();
        Button addBtn = findViewById(R.id.addbtn);

        addBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(CoinDetailsActivity.this, "You want to add? yes", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CoinDetailsActivity.this, PortfolioActivity.class);

                intent.putExtra("id", mID);
                //Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.coingecko.com/api/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        coinmarketcapApi = retrofit.create(CoinmarketcapApi.class);



        getIncomingIntent();
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("id")) {
            String id = getIntent().getStringExtra("id");
            //Toast.makeText(this, id + "e", Toast.LENGTH_SHORT).show();
            mID = id;
            setInfo(id);
        }

    }

    private void setInfo(String id) {

        final TextView priceText = findViewById(R.id.priceContent);
        final TextView topText = findViewById(R.id.title);
        //final TextView priceBTCText = findViewById(R.id.pricebtcContent);
        final TextView marketCapText = findViewById(R.id.marketcapContent);
        final TextView volumeText = findViewById(R.id.volumeContent);
        final TextView percent24 = findViewById(R.id.percent24Content);
        //final TextView percent7d = findViewById(R.id.percent7dContent);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", id);
        parameters.put("localisation", "false");
        parameters.put("market_data", "true");
        parameters.put("community_data", "false");
        parameters.put("developer_data", "false");


        Call<JsonObject> call = coinmarketcapApi.getDetails(id, parameters);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(!response.isSuccessful()) {
                    return;
                }
                topText.setText(response.body().getAsJsonObject().get("name").toString().substring(1,response.body().getAsJsonObject().get("name").toString().length() - 1));


                String name = response.body().getAsJsonObject().get("market_data").getAsJsonObject().get("current_price").getAsJsonObject().get("usd").toString();
                if (name.length() > 6) {
                    name = name.substring(0,6);
                }
                priceText.setText("$" + name);



                String pricebtctext = (response.body().getAsJsonObject().get("market_data").getAsJsonObject().get("current_price").getAsJsonObject().get("btc").toString());
                /*if (pricebtctext.length() > 10) {
                    priceBTCText.setText(pricebtctext.substring(0,10));
                } else {
                    priceBTCText.setText(pricebtctext);
                }*/

                marketCapText.setText(response.body().getAsJsonObject().get("market_data").getAsJsonObject().get("market_cap").getAsJsonObject().get("usd").toString());

                volumeText.setText(response.body().getAsJsonObject().get("market_data").getAsJsonObject().get("total_volume").getAsJsonObject().get("usd").toString());

                percent24.setText(response.body().getAsJsonObject().get("market_data").getAsJsonObject().get("price_change_percentage_24h").toString());

               // percent7d.setText(response.body().getAsJsonObject().get("market_data").getAsJsonObject().get("price_change_percentage_7d").toString());

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });


    }
}
