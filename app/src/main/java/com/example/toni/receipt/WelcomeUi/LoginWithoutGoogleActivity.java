package com.example.toni.receipt.WelcomeUi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.toni.receipt.R;

public class LoginWithoutGoogleActivity extends AppCompatActivity {


    private EditText _email,_password;
    private Button _continue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_without_google);

        //setup views..
        setUpViews();
    }

    private void setUpViews() {

        _email = (EditText) findViewById(R.id.et_login_email);
        _password = (EditText) findViewById(R.id.et_login_password);
        _continue = (Button) findViewById(R.id.btn_login_continue);
    }
}
