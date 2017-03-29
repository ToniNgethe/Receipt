package com.example.toni.receipt.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by toni on 3/5/17.
 */

public class Global {

    //check connection...
    public static boolean isConnected(Context context){

        boolean connected = false;

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()){
            connected = true;
        }

        return connected;
    }

}
