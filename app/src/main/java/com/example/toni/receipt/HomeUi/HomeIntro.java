package com.example.toni.receipt.HomeUi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.toni.receipt.R;

public class HomeIntro extends AppCompatActivity implements View.OnClickListener {

    private Button add, view;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_intro);

        //get user id
        userID = getIntent().getExtras().getString("USER_ID");

        //views
        add = (Button) findViewById(R.id.btn_homeintro_add);
        view = (Button) findViewById(R.id.btn_homeintro_view);

        //attach listener
        add.setOnClickListener(this);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_homeintro_add:

                //add new home receipt
                startActivity(new Intent(HomeIntro.this, HomePostReceipt.class).putExtra("USER_ID",userID));

                break;
            case R.id.btn_homeintro_view:

                //view old receipts..
                startActivity(new Intent(HomeIntro.this, HomeReceipts.class).putExtra("USER_ID",userID));

                break;

        }
    }
}
