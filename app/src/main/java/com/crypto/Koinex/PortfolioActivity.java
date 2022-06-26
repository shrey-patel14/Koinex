package com.crypto.Koinex;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.crypto.Koinex.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PortfolioActivity extends AppCompatActivity implements PortfolioRecyclerAdapter.OnPortfolioCoinClickListener {

    public static final String EXTRA_NUMBER = "com.monty.cryptoapp.EXTRA_NUMBER";
    private CoinmarketcapApi coinGeckoApi;

    private RecyclerView recyclerView;
    private PortfolioRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    Gson g = new Gson();
    public Float price = 0.00f;
    private Float amount = 0.0f;
    private String coinPriceToSend;
    TextView totalVal1;

    private boolean paused;
    private int count;
    TextView title;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_portfolio);

        title = (TextView) findViewById(R.id.title);
        Button coinviewbtn2 = findViewById(R.id.CoinviewButton2);
        totalVal1 = (TextView) findViewById(R.id.portfolioOverallValue);
        coinviewbtn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                gotorewards();
            }
        });

        title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PortfolioActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        Button PortfolioButton2 = findViewById(R.id.PortfolioButton2);


        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl("http://api.coingecko.com/api/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();




        // makes the interface we have
        coinGeckoApi = retrofit2.create(CoinmarketcapApi.class);

        PortfolioButton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PortfolioActivity.class);
                startActivity(intent);
            }
        });

        String internalData = (read("storage.json"));
        //Toast.makeText(this, internalData, Toast.LENGTH_SHORT).show();
        if (internalData != null & internalData != "[]") {
            Log.d("DE", internalData.toString());
            JsonArray internalJson = new JsonParser().parse(internalData).getAsJsonArray();
            //String data = internalJson.getAsJsonArray().get(0).getAsJsonObject().get("name").toString();

            initPortfolioRecyclerViewData(internalJson);

            //Toast.makeText(this, internalJson.toString(), Toast.LENGTH_LONG).show();

        }

        getIncomingIntent();

    }

    private void gotorewards() {
        Intent intent = new Intent();
       // double num = totalVal1/0.002;


        intent.putExtra("value1",totalVal1.getText().toString());

        intent.setClass(PortfolioActivity.this,Rewards.class);
        startActivity(intent);
    }

    public void showAlertDialog(final String id) {

        AlertDialog.Builder builder = new Builder(PortfolioActivity.this, R.style.MyDialogTheme);


        final View customLayout = getLayoutInflater().inflate(R.layout.custom_alertdialog, null);
        builder.setView(customLayout);
        final EditText priceEnter = customLayout.findViewById(R.id.inputPrice);
        final EditText amountEnter = customLayout.findViewById(R.id.inputAmount);
        priceEnter.requestFocus();
        builder.setTitle("Add Coin To Portfolio?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d("help", "empty " + priceEnter.getText().toString());
                //Toast.makeText(PortfolioActivity.this, price.toString(), Toast.LENGTH_SHORT).show();
                if (!priceEnter.getText().toString().equals("") && !amountEnter.getText().toString().equals("")) {
                    price = Float.parseFloat(priceEnter.getText().toString());
                    amount = Float.parseFloat(amountEnter.getText().toString());
                    writeData(id, price, amount);
                    Toast.makeText(PortfolioActivity.this, "Coin Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PortfolioActivity.this, "Coin Not Added (No details present)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.setCancelable(true);
        dialog.show();
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("id")) {
            String id = getIntent().getStringExtra("id");
            //Toast.makeText(this, id + "e", Toast.LENGTH_SHORT).show();


            showAlertDialog(id);
//                pIDs.add(id);
//                writeData(id);

        }

    }

    public void writeData(String id, float price, float amount) {


        boolean isFilePresent = isFilePresent(getApplicationContext(), "storage.json");

        JSONObject coinBuyData = new JSONObject();
        try {
            coinBuyData.put("bp", price);
            coinBuyData.put("name", id);
            coinBuyData.put("amount", amount);

;
        } catch (JSONException e) {

        }
        boolean same = false;

        if(isFilePresent) {
            String jsonString = read( "storage.json");
            //Toast.makeText(this, jsonString, Toast.LENGTH_SHORT).show();
            Log.d("hello", "what is read form the file stored raw    " + jsonString);
            JsonArray internalJson = new JsonParser().parse(jsonString).getAsJsonArray();
            Log.d("hello", "what is read form the file stored raw    " + internalJson.toString());


            for(int i = 0; i < internalJson.size(); i ++) {
                //Log.d("DE", (internalJson.get(i).getAsJsonObject().get("name").toString()).substring(1,internalJson.get(i).getAsJsonObject().get("name").toString().length() - 1));
                //Toast.makeText(this, (internalJson.get(i).getAsJsonObject().get("name").toString()).substring(1,internalJson.get(i).getAsJsonObject().get("name").toString().length() - 1), Toast.LENGTH_SHORT).show();
                if (id.equals((internalJson.get(i).getAsJsonObject().get("name").toString()).substring(1,internalJson.get(i).getAsJsonObject().get("name").toString().length() - 1))) {
                    same = true;
                    Toast.makeText(this, "someething is the same", Toast.LENGTH_SHORT).show();
                }
            }
            String newToAdd = "";
            if (!(same)) {
                if (jsonString.equals("[]")) {
                    newToAdd = ((jsonString.substring(0,jsonString.length()-1) + coinBuyData.toString() + "]"));
                } else {
                    newToAdd = ((jsonString.substring(0, jsonString.length() - 1) + "," + coinBuyData.toString() + "]"));
                    Log.d("hello", "what will be wrtiten into file    " + newToAdd);
                }

                try {
                    FileOutputStream fos = openFileOutput("storage.json",Context.MODE_PRIVATE);
                    fos.write(newToAdd.toString().getBytes());
                    fos.close();

                    //Toast.makeText(this, "added" + id, Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException fileNotFound) {

                } catch (IOException ioException) {

                }

            }
            //Toast.makeText(this, newToAdd, Toast.LENGTH_SHORT).show();




        } else {
            String FILENAME = "storage.json";
            try {
                FileOutputStream fos = openFileOutput("storage.json",Context.MODE_PRIVATE);
                fos.write(("[" +coinBuyData.toString() + "]").getBytes());
                fos.close();

                //Toast.makeText(this, "added" + id, Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException fileNotFound) {

            } catch (IOException ioException) {

            }
        }

    }

    public boolean isFilePresent(Context context, String fileName) {
        String path = getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

    private String read(String fileName) {
        try {
            FileInputStream fis = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            String ret = sb.toString();
            ret = ret.replace("\\","");
            return ret;
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    private void initPortfolioRecyclerViewData(final JsonArray jsonData) {
        final ArrayList<Float> coinQuantityArray = new ArrayList<>();
        final ArrayList<Float> coinBPArray = new ArrayList<>();
        // Arrays with data needed from api call
        final ArrayList<String> coinImageArray = new ArrayList<>();
        final ArrayList<String> coinCurrPriceArray = new ArrayList<>();
        final ArrayList<String> coinNameArray = new ArrayList<>();
        final ArrayList<String> coinIDArray = new ArrayList<>();

        count = 0;

        for(int i = 0; i < jsonData.size(); i++ ) {
            String currentID = jsonData.getAsJsonArray().get(i).getAsJsonObject().get("name").toString().substring(1,jsonData.getAsJsonArray().get(i).getAsJsonObject().get("name").toString().length() - 1);


            //Toast.makeText(this, coinQuantityArray.get(i).toString(), Toast.LENGTH_SHORT).show();


            // API CALL ////////////////////////////////////////////////////////////////////////////////////////////
            Map<String, String> parameters = new HashMap<>();
            parameters.put("id", currentID);
            parameters.put("localisation", "false");
            parameters.put("market_data", "true");
            parameters.put("community_data", "false");
            parameters.put("developer_data", "false");


            //Toast.makeText(this, currentID + "hello", Toast.LENGTH_SHORT).show();
            Call<JsonObject> call = coinGeckoApi.getDetails(currentID, parameters);


            final int finalI = i;
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(!response.isSuccessful()) {
                        //Toast.makeText(getApplicationContext(), "call has fucking died", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    count++;
                    coinImageArray.add(response.body().getAsJsonObject().get("image").getAsJsonObject().get("large").toString().substring(1,response.body().getAsJsonObject().get("image").getAsJsonObject().get("small").toString().length() - 1));
                    coinCurrPriceArray.add(response.body().getAsJsonObject().get("market_data").getAsJsonObject().get("current_price").getAsJsonObject().get("usd").toString());
                    coinNameArray.add(response.body().getAsJsonObject().get("name").toString());
                    coinIDArray.add(response.body().getAsJsonObject().get("id").toString());
                    coinQuantityArray.add(Float.parseFloat(jsonData.getAsJsonArray().get(finalI).getAsJsonObject().get("amount").toString()));
                    coinBPArray.add(Float.parseFloat(jsonData.getAsJsonArray().get(finalI).getAsJsonObject().get("bp").toString()));


                    if (count == (jsonData.size())) {
                        initPortfolioRecyclerView(coinNameArray, coinQuantityArray, coinBPArray, coinCurrPriceArray, coinImageArray, coinIDArray);
                        //Toast.makeText(PortfolioActivity.this, "hello" + coinCurrPriceArray.get(0), Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                }
            });

        }


    }

    //inits recycler view and adds the other data to the other parts of the portfolio activity
    private void initPortfolioRecyclerView(ArrayList<String> coinNames, ArrayList<Float> coinQuants, ArrayList<Float> coinBPs, ArrayList<String> coinCurrPrice, ArrayList<String> coinImages, ArrayList<String> coinIDs) {
        //OTHER PARTS OF THE ACTIVITY STUFF----------------------------------
        TextView totalVal = findViewById(R.id.portfolioOverallValue);
        TextView totalProfit = findViewById(R.id.portfolioOverallProfit);

        Float totalValv = 0.0f;
        Float origValv = 0.0f;
        for (int i =0; i < coinQuants.size(); i ++) {
            totalValv += (Float.parseFloat(coinCurrPrice.get(i)) * coinQuants.get(i));
            origValv += (coinBPs.get(i) * coinQuants.get(i));
        }

        if(totalValv < 0) {
            totalVal.setText("-$" + String.valueOf(totalValv).substring(1,String.valueOf(totalValv).length() - 1));
        } else {
            totalVal.setText(String.valueOf(totalValv));
        }
        if((totalValv - origValv) < 0) {
            totalProfit.setText("-$" + String.valueOf(totalValv - origValv).substring(1,String.valueOf(totalValv - origValv).length() - 1));
            totalProfit.setTextColor(Color.RED);
        } else {
            int green  = Color.parseColor("#009900");
            totalProfit.setText(String.valueOf(totalValv - origValv));
            totalProfit.setTextColor(green);
        }



        //RECYCLER VIEW STUFF-----------------------------------------------
        recyclerView = findViewById(R.id.CoinsRecyclerView);
        Log.d("help", coinIDs.toString());
        layoutManager = new GridLayoutManager(this, 2);
        mAdapter = new PortfolioRecyclerAdapter(coinNames, coinQuants, coinBPs, coinCurrPrice, coinImages, coinIDs, this, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void OnPortfolioCoinClick(final int position, final ArrayList<String> ids) {
        //Toast.makeText(this, "I have been clicked", Toast.LENGTH_SHORT).show();
        final String id = ids.get(position);



        AlertDialog.Builder builder = new Builder(PortfolioActivity.this, R.style.MyDialogTheme);

        builder.setTitle("Sell Coin From Portfolio?");

        builder.setPositiveButton("SELL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(PortfolioActivity.this, "fucking delete", Toast.LENGTH_SHORT).show();

                String jsondata = read("storage.json");
                JsonArray internalJson = new JsonParser().parse(jsondata).getAsJsonArray();

                ArrayList<String> internalArray = new ArrayList<>();
                for(int i = 0; i < internalJson.size(); i++) {
                    internalArray.add(internalJson.get(i).toString());
                }

                int pos = 0;
                for(int i = 0; i < internalArray.size(); i++) {
                    if(internalArray.get(i).contains(id)) {
                        pos = i;
                    }
                }

                internalArray.remove(pos);
                String fileToSave = internalArray.toString();


                Log.d("help", "array after removal" + internalArray.toString());

                //fileToSave = fileToSave.replace("\\","");


                //Log.d("hello", fileToSave);


                try {
                    FileOutputStream fos = openFileOutput("storage.json",Context.MODE_PRIVATE);
                    fos.write(fileToSave.getBytes());
                    fos.close();

                    Toast.makeText(getApplicationContext(), "Coin Sold", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException fileNotFound) {

                } catch (IOException ioException) {

                }
            }
        });

        builder.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.setCancelable(true);
        dialog.show();
    }

}
