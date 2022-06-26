package com.crypto.Koinex;

import android.content.Context;
import android.content.Intent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.crypto.Koinex.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnCoinListener, OnRefreshListener {

    // create an instance of the API interface
    private CoinmarketcapApi coinmarketcapApi;

    private SwipeRefreshLayout swipeRefreshLayout;

    // initiate all the array that hold the information for the list
    private ArrayList<String> mCoinPercentChange = new ArrayList<>();
    private ArrayList<String> mCoins = new ArrayList<>();
    private ArrayList<String> mCoinPrices = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mCoinRanks = new ArrayList<>();
    private ArrayList<String> mIDs = new ArrayList<>();
    private ArrayList<String> mSymbols = new ArrayList<>();

    private ArrayList<String> mCoinPercentChange2 = new ArrayList<>();
    private ArrayList<String> mCoins2 = new ArrayList<>();
    private ArrayList<String> mCoinPrices2 = new ArrayList<>();
    private ArrayList<String> mImageUrls2 = new ArrayList<>();
    private ArrayList<String> mCoinRanks2 = new ArrayList<>();
    private ArrayList<String> mIDs2 = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe);

        swipeRefreshLayout.setOnRefreshListener(this);
        final EditText searchinput = findViewById(R.id.searchinput);
        final TextView title = findViewById(R.id.title);


        final Button searchbtn =findViewById(R.id.searchbutton);
        Button portfoliobtn = findViewById(R.id.PortfolioButton);
        Button coinViewButton12 = findViewById(R.id.CoinviewButton);
        Button sign_out = findViewById(R.id.sign_out);

        sign_out.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,LogIn.class));
            }
        });

        searchbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchinput.setVisibility(View.VISIBLE);
                searchbtn.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                searchinput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchinput, InputMethodManager.SHOW_IMPLICIT);


            }
        });
        // Click listener for search button on keyboard -------------------------------------------------------------------------
        searchinput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //performSearch();
                    //Toast.makeText(MainActivity.this, searchinput.getText(), Toast.LENGTH_SHORT).show();
                    String searchfor = searchinput.getText().toString().toLowerCase();
                    ArrayList<Integer> searchfound = new ArrayList<>();
                    boolean found = false;
                    for(int i = 0; i < mSymbols.size(); i ++) {
                        if (mSymbols.get(i).contains('"' +searchfor +'"')) {
                            //Log.d("mytag", mIDs.get(i));
                            searchfound.add(i);
                            Intent intent = new Intent(MainActivity.this, CoinDetailsActivity.class);
                            String id = mIDs.get(searchfound.get(0)).substring(1,mIDs.get(searchfound.get(0)).length() - 1);
                            intent.putExtra("id", id);
                            //Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
                            found =true;
                            startActivity(intent);

                        }
                    }
                    if(!found) {
                        Toast.makeText(MainActivity.this, "Coin Not Found", Toast.LENGTH_SHORT).show();
                    }


                    //boolean found = mSymbols.contains('"' +searchfor +'"');

                    //Toast.makeText(MainActivity.this, searchfound.toString(), Toast.LENGTH_SHORT).show();

                    return true;
                }
                return false;
            }
        });


        portfoliobtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PortfolioActivity.class);
                startActivity(intent);

            }
        });

        coinViewButton12.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        Retrofit retrofit = new Builder()
                .baseUrl("http://api.coingecko.com/api/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // makes the interface we have
        coinmarketcapApi = retrofit.create(CoinmarketcapApi.class);


        //getCoins();
        //getCoinData();
        getPosts(1);

        //initCoins();

    }

    private void getPosts(final int page) {


        Map<String, String> parameters = new HashMap<>();
        parameters.put("vs_currency", "usd");
        parameters.put("order", "market_cap_desc");
        parameters.put("per_page", "250");
        parameters.put("page", Integer.toString(page));
        //parameters.put("_order", "desc");

        Call<JsonArray> call = coinmarketcapApi.getCoins(parameters);

        // runs it in a background thread
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                // checks if the http response is not one of those 404 ones
                if (!response.isSuccessful()) {
                    // textView.setText("Code:" + response.code());
                    return;
                }

                for (int i = 0; i < 250; i++) {
                    String change = (response.body().get(i).getAsJsonObject().get("price_change_percentage_24h").toString());

                    mCoinPercentChange.add(change);
                    /*if (change.indexOf('-') != -1) {
                        mCoinPercentChange.add(change + "%");
                    } else {
                        mCoinPercentChange.add(change.substring(0, 4) + "%");
                    }*/

                    // ranks


                    // Name of coins
                    String coinname = (response.body().get(i).getAsJsonObject().get("name").toString());
                    coinname = coinname.substring(1, coinname.length() - 1);
                    mCoins.add(coinname);


                    String price = (response.body().get(i).getAsJsonObject().get("current_price").toString());
                    try {
                        mCoinPrices.add("$" + price.substring(0, 6));
                    } catch (Exception e) {
                        mCoinPrices.add("$" + price);
                    }

                    // urls
                    String url = (response.body().get(i).getAsJsonObject().get("image").toString());
                    url = url.substring(1, url.length() - 1);
                    mImageUrls.add(url);

                    //IDs'
                    mIDs.add(response.body().get(i).getAsJsonObject().get("id").toString());

                    //Symbols
                    mSymbols.add(response.body().get(i).getAsJsonObject().get("symbol").toString());

                }



                if (page == 1) {
                    getPosts(2);
                    initRecyclerView();
                } else if (page == 2) {
                    getPosts(3);
                    initRecyclerView();
                } else if (page == 3) {
                    getPosts(4);
                    initRecyclerView();
                }

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }


            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                // textView.setText(t.getMessage());
            }
        });

    }

    public void Get250CoinData() {

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mCoins, this, mCoinPrices, mImageUrls, mCoinPercentChange, mIDs, mSymbols, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void OnCoinClick(int position) {

        Intent intent = new Intent(this, CoinDetailsActivity.class);
        String id = mIDs.get(position).substring(1,mIDs.get(position).length() - 1);
        intent.putExtra("id", id);
        //Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        //Toast.makeText(this, "Hello u have refreshed", Toast.LENGTH_SHORT).show();
        getPosts(1);
    }
}
