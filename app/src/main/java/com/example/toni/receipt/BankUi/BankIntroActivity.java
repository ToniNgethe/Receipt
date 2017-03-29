package com.example.toni.receipt.BankUi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.toni.receipt.R;

public class BankIntroActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = BankIntroActivity.class.getSimpleName();
    private Button add, view;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_intro);

        //setup
        views();
    }

    private void views() {

        user_id = getIntent().getExtras().getString("USER_ID");

        add = (Button) findViewById(R.id.btn_bankintro_add);
        add.setOnClickListener(this);

        view = (Button) findViewById(R.id.btn_bankintro_view);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bankintro_add:

                startActivity(new Intent(BankIntroActivity.this, BankPostActivity.class).putExtra("USER_ID", user_id));

                break;
            case R.id.btn_bankintro_view:
                startActivity(new Intent(BankIntroActivity.this, BankViewReceiptsActivity.class).putExtra("USER_ID", user_id));

                break;

        }
    }

}
