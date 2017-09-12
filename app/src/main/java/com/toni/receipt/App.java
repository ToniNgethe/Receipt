package com.toni.receipt;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by toni on 9/12/17.
 */

public class App extends Application{


    private static App instance;
    public static synchronized App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //  Realm.init(this); //initialize other plugins}
        instance = this;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

}
