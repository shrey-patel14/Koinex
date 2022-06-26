package com.crypto.Koinex;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Rewards extends AppCompatActivity {

    TextView cash;
    TextView total_cashback;
    TextView banner_r;
    Button signout1, home12;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        cash = (TextView) findViewById(R.id.cash);
        total_cashback = (TextView) findViewById(R.id.total_cashback);
        banner_r = (TextView) findViewById(R.id.banner_r);
        signout1 = (Button) findViewById(R.id.signout1);
        home12 = (Button) findViewById(R.id.home12);
        home12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Rewards.this, MainActivity.class);
                startActivity(intent);
            }
        });

        signout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Rewards.this, LogIn.class);
                startActivity(intent);
            }
        });





        String value1 = super.getIntent().getExtras().getString("value1");
        Log.d("value",value1);
        Double d = Double.parseDouble(value1);
        d = ((d*2)/100);
        cash.setText(d.toString());








        /*double d = new Double(value1).doubleValue(); //constructor deprecated in java 9
        //double d = Double.parseDouble(value1);
        d=d/100;
        //value1 = value1/100;
        //String value1 = super.getIntent().getExtras().getString("value1");
        //cash = new Double(value1);
        String value2 = super.getIntent().getExtras().getString("d");
        cash.setText("$:" + value2);*/



    }
}