package com.example.toni.receipt.HomeUi;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.toni.receipt.Helper.Global;
import com.example.toni.receipt.Model.HomeReceiptsModel;
import com.example.toni.receipt.R;

import java.io.File;

public class HomeReceiptSingleView extends AppCompatActivity {

    private HomeReceiptsModel bankReceipts;
    private ImageView imageView;
    private TextView _name, _number, _total, _desc, _date, _cat;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_receipt_single_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);

        bankReceipts = (HomeReceiptsModel) getIntent().getSerializableExtra("RECEIPT");

        getSupportActionBar().setTitle(bankReceipts.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupSnackBar(view);
            }
        });

        //gettingviews
        views();
        //initiate
        initiate();
    }

    private void setupSnackBar(View view) {

        // Create the Snackbar
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG);
        // Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        // Hide the text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        // Inflate our custom view

        View snackView = LayoutInflater.from(this).inflate(R.layout.my_snackbar, null);
        // Configure the view
        Button pdf = (Button) snackView.findViewById(R.id.btn_singlebank_pdf);
        Button image = (Button) snackView.findViewById(R.id.btn_singlebank_image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.isConnected(getApplication())) {

                    downloadFile(bankReceipts.getImage());

                } else {

                    Toast toast = Toast.makeText(HomeReceiptSingleView.this, "", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setView(getLayoutInflater().inflate(R.layout.net_toast, (ViewGroup) findViewById(R.id.customError)));
                    toast.show();
                }
            }
        });

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast t = Toast.makeText(HomeReceiptSingleView.this,"Feature coming soon",Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER,0,0);
                t.show();
            }
        });


        // Add the view to the Snackbar's layout
        layout.addView(snackView, 0);
        // Show the Snackbar
        snackbar.show();

    }

    public void downloadFile(String uRl) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Downloading image...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/MyReceipts");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Downloading receipt")
                .setDescription("Saving image to /MyReceipts")
                .setDestinationInExternalPublicDir("/MyReceipts", bankReceipts.getName() + "_" + bankReceipts.getUuid() + ".png");

        Toast t = Toast.makeText(this, "Receipt downloaded to ./MyReceipts", Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();


        mgr.enqueue(request);
        progressDialog.dismiss();
    }

    private void initiate() {

        //get image
        Glide.with(this).load(bankReceipts.getImage())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(imageView);

        //others
        _name.setText(bankReceipts.getName());
        _number.setText(bankReceipts.getNumber());
        _total.setText(String.valueOf(bankReceipts.getTotal()));
        _desc.setText(bankReceipts.getDesc());
        _date.setText(bankReceipts.getDate());
        _cat.setText(bankReceipts.getCategory());

    }

    private void views() {

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.layout_homesingle);
        imageView = (ImageView) findViewById(R.id.iv_singlehome_image);
        _name = (TextView) findViewById(R.id.tv_homesingleview_name);
        _number = (TextView) findViewById(R.id.tv_homesingleview_number);
        _total = (TextView) findViewById(R.id.tv_homesingleview_total);
        _desc = (TextView) findViewById(R.id.tv_homesingleview_desc);
        _date = (TextView) findViewById(R.id.tv_homesingleview_date);
        _cat = (TextView) findViewById(R.id.tv_homesingleview_cat);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
