package com.example.toni.receipt.HomeUi;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.toni.receipt.Helper.MySingleton;
import com.example.toni.receipt.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomePostReceipt extends AppCompatActivity {

    public static final String URL_UPLOADRECEIPT = "http://192.168.43.201:80/Receipts/home_receipts/upload_home_receipt.php";
    private static final String TAG = HomePostReceipt.class.getSimpleName();
    private EditText _name, _number, _total, _desc;
    private ImageButton _image;
    private Button post;
    private Spinner cat;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "MyReceipts";

    private Uri fileUri = null; // file url to store image/video

    private Bitmap bitmap;
    private NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_post_receipt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //PERMISSIONS..
        enableRunTimePermissions();

        //views
        setupViews();

        //getCamera
        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }


    }

    private void setupViews() {

        _image = (ImageButton) findViewById(R.id.imgnbtn_homepost_image);
        _name = (EditText) findViewById(R.id.et_homepost_name);
        _number = (EditText) findViewById(R.id.et_homepost_number);
        _total = (EditText) findViewById(R.id.et_homepost_total);
        _desc = (EditText) findViewById(R.id.et_homepost_desc);
        cat = (Spinner) findViewById(R.id.spinner_homepost_category);
        post = (Button) findViewById(R.id.btn_homepost_submit);

        //attach listener
        _image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureimage();
            }


        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadInformation();
            }
        });

    }

    private void uploadInformation() {

        if (fileUri != null) {

            if (!TextUtils.isEmpty(_name.getText()) && !TextUtils.isEmpty(_number.getText()) && !TextUtils.isEmpty(_total.getText())
                    && !TextUtils.isEmpty(_desc.getText())) {


                startSubmiting();


            } else {

                Toast.makeText(HomePostReceipt.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();

            }

        } else {
            Toast.makeText(HomePostReceipt.this, "Add receipt image", Toast.LENGTH_SHORT).show();
        }

    }

    private void startSubmiting() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final String name = _name.getText().toString().trim();
        final String number = _number.getText().toString().trim();
        final String total = _total.getText().toString().trim();
        final String desc = _desc.getText().toString().trim();
        final String category = String.valueOf(cat.getSelectedItem());
        final String userid = getIntent().getExtras().getString("USER_ID");

        StringRequest home_receiptStringRequest = new StringRequest(Request.Method.POST, URL_UPLOADRECEIPT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        Log.d(TAG, response);

                        sendNotification(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Log.d(TAG, error.getMessage());
                Toast.makeText(HomePostReceipt.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();

                map.put("uuid", userid);
                map.put("name", name);
                map.put("number", number);
                map.put("image", getStringImage(bitmap));
                map.put("desc", desc);
                map.put("category", category);
                map.put("total", total);

                return map;
            }
        };

        MySingleton.getInstance(HomePostReceipt.this).addToRequestQueue(home_receiptStringRequest);

    }

    private void sendNotification(String messageBody) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.home)
                .setContentTitle("Home Receipts")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setTicker("Home receipt added successfully")
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

    private void captureimage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri();

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

    }

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

    private void previewCapturedImage() {

        try {
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            _image.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;
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
                + "IMG_HOME" + timeStamp + ".jpg");


        return mediaFile;
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

    private void enableRunTimePermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(HomePostReceipt.this,
                android.Manifest.permission.CAMERA)) {

            Toast.makeText(HomePostReceipt.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(HomePostReceipt.this, new String[]{
                    android.Manifest.permission.CAMERA}, 10);

        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2);

    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case 10:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(BankPostActivity.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(HomePostReceipt.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

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
}
