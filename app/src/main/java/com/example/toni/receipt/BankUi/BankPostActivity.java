package com.example.toni.receipt.BankUi;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.toni.receipt.Helper.Global;
import com.example.toni.receipt.Helper.MySingleton;
import com.example.toni.receipt.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BankPostActivity extends AppCompatActivity {


    //upload url
    public static final String URL_UPLOADRECEIPT = "http://192.168.43.201:80/Receipts/submit_bank_receipt.php";
    private static final String TAG = BankPostActivity.class.getSimpleName();

    NotificationCompat.Builder notification;
    NotificationManager manager, notificationManager;

    private ImageView get_receipt_image;
    private EditText _name, _number, _quotation,_total;
    private Button submit;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "MyReceipts";

    private Uri fileUri = null; // file url to store image/video

    private Bitmap bitmap;

    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Add receipt");

        EnableRuntimePermission();

        //views
        get_receipt_image = (ImageView) findViewById(R.id.imgbtn_postbankreceipt_camera);
        _name = (EditText) findViewById(R.id.et_postbankrect_name);
        _number = (EditText) findViewById(R.id.et_postbankrect_number);
        _quotation = (EditText) findViewById(R.id.et_postbankrect_desc);
        submit = (Button) findViewById(R.id.btn_postbankrct_submit);
        _total = (EditText) findViewById(R.id.et_postbankrect_total);
        spinner = (Spinner) findViewById(R.id.spinner_bankpost_category);

        get_receipt_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

        uploadDetails();

    }

    private void uploadDetails() {

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.isConnected(getApplicationContext())) {

                    if (fileUri != null) {
                        //check if strings is empty
                        if (!TextUtils.isEmpty(_name.getText())
                                && !(String.valueOf(spinner.getSelectedItem()).equals("Choose category")) && !TextUtils.isEmpty(_number.getText()) && !TextUtils.isEmpty(_quotation.getText())) {


                            uploadReceipt();

                        } else {

                            showToast("Fields cannot be empty");

                        }

                    } else {

                        showToast("Upload your receipt image");
                    }

                } else {

                    Toast toast = Toast.makeText(BankPostActivity.this, "", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setView(getLayoutInflater().inflate(R.layout.net_toast, (ViewGroup) findViewById(R.id.customError)));
                    toast.show();
                }

            }
        });

    }


    private void uploadReceipt() {


        final String name = _name.getText().toString().trim();
        final String number = _number.getText().toString().trim();
        final String desc = _quotation.getText().toString().trim();
        final String user_id = getIntent().getExtras().getString("USER_ID");
        final String total = _total.getText().toString().trim();
        final String cat = String.valueOf(spinner.getSelectedItem());


        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOADRECEIPT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, response);

                        loading.dismiss();
                        // startNotification();
                        sendNotification(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Log.d(TAG, volleyError.getMessage());
                        showToast(volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);


                //Creating parameters
                Map<String, String> params = new HashMap<>();

                //Adding parameters
                params.put("image", image);
                params.put("name", name);
                params.put("number", number);
                params.put("desc", desc);
                params.put("uuid", user_id);
                params.put("category",cat);
                params.put("total",total);

                //returning parameters
                return params;
            }
        };

        MySingleton.getInstance(BankPostActivity.this).addToRequestQueue(stringRequest);
    }

    private void sendNotification(String messageBody) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.bank)
                .setContentTitle("Bank Receipts")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setTicker("Bank receipt added successfully")
                .setSound(defaultSoundUri);


        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int id = 0;
        notificationManager.notify(id, notificationBuilder.build());
        removeNotification(id);
        finish();
    }

    private void removeNotification(final int id) {
        Handler handler = new Handler();
        long delayInMilliseconds = 1000;
        handler.postDelayed(new Runnable() {
            public void run() {
                notificationManager.cancel(id);
            }
        }, delayInMilliseconds);
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showToast(String msg) {
        Toast.makeText(BankPostActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    /*
 * Capturing Camera Image will lauch camera app requrest image capture
 */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri();

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /*
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {


            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            get_receipt_image.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_BANK" + timeStamp + ".jpg");


        return mediaFile;
    }


    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(BankPostActivity.this,
                Manifest.permission.CAMERA)) {

            Toast.makeText(BankPostActivity.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(BankPostActivity.this, new String[]{
                    Manifest.permission.CAMERA}, 10);

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case 10:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(BankPostActivity.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(BankPostActivity.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;

            case 2:
                //If permission is granted
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    //Displaying a toast
                    // Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
                } else {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
                }

                break;
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
