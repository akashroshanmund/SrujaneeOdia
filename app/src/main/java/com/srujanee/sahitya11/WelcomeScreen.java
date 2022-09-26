package com.srujanee.sahitya11;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

public class WelcomeScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 700;
    static Context context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        context = this;
        DBHelper myDb = new DBHelper(context);
        /*---------------------------------------------------------*/
        SharedPreferences sp = getSharedPreferences("FILE_NAME", MODE_PRIVATE);

        if (!sp.contains(variable.SP_IS_UPDATE_MANDATORY)) {
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt(variable.SP_IS_UPDATE_MANDATORY, -1);
            edit.apply();
        }
        //todo - add condition to fetch trigger from server and update shared preference
        if (sp.getInt(variable.SP_IS_UPDATE_MANDATORY, -1) > variable.MANDATORY_UPDATE_CODE) {
            //TODO - UPDATE OPTION
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Mandatory Update...");
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.srujanee.sahitya11"));
                    startActivity(viewIntent);
                }
            });
            builder.show();
            // clear all the local database
            


        } else {
            SharedPreferences.Editor edit1 = sp.edit();
            edit1.putInt(variable.SP_IS_UPDATE_MANDATORY, -1);
            edit1.apply();

            /*-----------------------------------------------------------------------*/

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            variable.screenHeight = displayMetrics.heightPixels;
            variable.screenWidth = displayMetrics.widthPixels;


            Log.i("netTest", "onCreate: " + connectivityManager.getActiveNetwork());


            //  NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //Log.i(TAG, "onCreate: "+netInfo.);
            if (false) {
                myDb.insertImageSubFolder();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("test", "run: ");
                    Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }
}