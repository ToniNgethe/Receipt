package com.example.toni.receipt.WelcomeUi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.toni.receipt.Helper.AppConfig;
import com.example.toni.receipt.Helper.MySingleton;
import com.example.toni.receipt.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserSetupActivity extends AppCompatActivity {

    private String updateUrl = "";

    private static final int GALLARY_INTENT = 1;
    private int CHNANGED = 1;
    private static final String TAG = UserSetupActivity.class.getSimpleName();
    private ImageView _profile;
    private EditText _name;
    private ImageButton _changePhoto;

    private Button submit;

    private String username,photoUrl,userId;
    private Uri imageUri = null;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup);

        //views
        setUpViews();
        //get intents
        getPassedExtras();
        //load image and username
        loadItems();
        //getImageFromm gallary
        selectnewImage();
        //submit changes...
        listener();

    }

    private void listener() {

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitChanges();
            }
        });
    }

    private void submitChanges() {

        final String oldName = _name.getText().toString();

        if (CHNANGED != 1 && imageUri != null && oldName != username){

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Updating...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            //user has a new image....submit it..
            StringRequest updateUser = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATEUSER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG,response);
                    try{

                        JSONObject json = new JSONObject(response);

                        boolean error = json.getBoolean("error");

                        if (!error){

                            progressDialog.dismiss();
                            String msg = json.getString("message");
                            Toast.makeText(UserSetupActivity.this,msg,Toast.LENGTH_SHORT).show();

                            //redirect user to main panel...
                            startActivity(new Intent(UserSetupActivity.this, MainActivity.class).putExtra("USER_ID",userId).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                        }else {

                            progressDialog.dismiss();
                            String error_msg = json.getString("message");
                            Toast.makeText(UserSetupActivity.this,error_msg,Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException e){
                        Log.d(TAG,e.getMessage());
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                        Log.d(TAG,error.getMessage());
                }
            })
            {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", oldName);
                    map.put("uuid", userId);
                    map.put("imageUrl", imageUri.toString());

                    return map;
                }
            };

            MySingleton.getInstance(UserSetupActivity.this).addToRequestQueue(updateUser);


        } else {
            //redirect user to main panel...
            startActivity(new Intent(UserSetupActivity.this, MainActivity.class).putExtra("USER_ID",userId).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        }

    }

    private void selectnewImage() {

        _changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,GALLARY_INTENT);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_INTENT && resultCode == RESULT_OK){

            Uri uri = data.getData();

            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();

                _profile.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Cropiiiiiii",error.getMessage());
                // Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadItems() {

        Glide.with(getApplicationContext()).load(photoUrl)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(_profile);

        _name.setText(username);

    }

    private void getPassedExtras() {
        username = getIntent().getExtras().getString("name");
        photoUrl = getIntent().getExtras().getString("photo");

    }

    private void setUpViews() {
        _changePhoto = (ImageButton) findViewById(R.id.imgbtn_usersetup_changePhoto);
        _profile = (ImageView) findViewById(R.id.profile_image);
        _name = (EditText) findViewById(R.id.regUsername);

        submit = (Button) findViewById(R.id.btn_setup_continu);
    }


}
