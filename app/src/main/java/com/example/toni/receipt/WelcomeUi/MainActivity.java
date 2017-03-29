package com.example.toni.receipt.WelcomeUi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.toni.receipt.BankUi.BankIntroActivity;
import com.example.toni.receipt.Helper.Global;
import com.example.toni.receipt.HomeUi.HomeIntro;
import com.example.toni.receipt.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.joaquimley.faboptions.FabOptions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private String url, email, username, userID;

    private Toolbar _toolToolbar;
    private ImageView userImage;
    private TextView _username, _userEmail;

    //card view.....
    private CardView bank, shopping, home, business;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Global.isConnected(this)) {

            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .requestId()
                    .build();

            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Toast.makeText(MainActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            userInforFromIntent();
            setUpViews();
        } else {

            Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setView(getLayoutInflater().inflate(R.layout.net_toast, (ViewGroup) findViewById(R.id.customError)));
            toast.show();

        }


    }



    private void setUpViews() {

        //fabolous
        FabOptions fabOptions = (FabOptions) findViewById(R.id.fab_options);
        fabOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.faboptions_account:

                        Toast.makeText(MainActivity.this, "Account clicked", Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.faboptions_other:
                        Toast.makeText(MainActivity.this, "Add category clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });

        _userEmail = (TextView) findViewById(R.id.tv_main_email);
        _username = (TextView) findViewById(R.id.tv_main_username);
        userImage = (ImageView) findViewById(R.id.iv_main_userProfile);
        _toolToolbar = (Toolbar) findViewById(R.id.tb_mainactivity_bar);

        setSupportActionBar(_toolToolbar);
        getSupportActionBar().setTitle("Dashboard");

        _userEmail.setText(email);
        _username.setText(username);


        if (url != null) {
            Glide.with(getApplicationContext()).load(url)
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(userImage);
        }
        //cards...
        bank = (CardView) findViewById(R.id.card_main_bank);
        bank.setOnClickListener(this);
        home = (CardView) findViewById(R.id.card_main_home);
        home.setOnClickListener(this);
        shopping = (CardView) findViewById(R.id.card_main_shopping);
        shopping.setOnClickListener(this);
        business = (CardView) findViewById(R.id.card_main_business);
        business.setOnClickListener(this);

    }

    private void userInforFromIntent() {

        email = getIntent().getExtras().getString("email");
        userID = getIntent().getExtras().getString("userId");
        url = getIntent().getExtras().getString("image");
        username = getIntent().getExtras().getString("name");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout:

                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {

                                startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                            }
                        });


                break;

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card_main_bank:

                Intent intent = new Intent(MainActivity.this, BankIntroActivity.class);
                intent.putExtra("USER_ID", userID);
                startActivity(intent);

                break;

            case R.id.card_main_home:

                Intent home = new Intent(MainActivity.this, HomeIntro.class);
                home.putExtra("USER_ID", userID);
                startActivity(home);

                break;
        }
    }
}
