package com.example.hugh.hw3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.TextView;
import android.os.Handler;

import android.content.Intent;
import android.content.IntentFilter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionApi;
import com.google.android.gms.wallet.wobs.TimeInterval;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static final String EXTRA_KEY_IN = "EXTRA_IN";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    public GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);

        mHandler = new Handler();
        startRepeatingTask();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            System.out.println("GOING.....");
            DBHelper DbHelper = new DBHelper(getApplicationContext());
            SQLiteDatabase db = DbHelper.getReadableDatabase();

            String[] projection = {
                    ActivitySchema.Entry.ACTIVITY,
                    ActivitySchema.Entry.TIME,
                    ActivitySchema.Entry.CONFIDENCE
            };


            long cutOff = ((new Date().getTime())/1000) - (24 * 60 * 60);
            String selection = ActivitySchema.Entry.TIME + " > ?";
            String[] selectionArgs = { Long.toString(cutOff) };

            String sortOrder =  ActivitySchema.Entry.TIME + " DESC";

            long from = 0;
            long to = 0;

            Cursor c = db.query(
                    ActivitySchema.Entry.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            c.moveToFirst();

            String activity = c.getString(0);
            TextView t = (TextView)findViewById(R.id.activityText);

            switch(activity){
                case "0":
                    t.setText("0");
                    break;
                case "1":
                    t.setText("1");
                    break;
                case "2":
                    t.setText("2");
                    break;
                case "3":
                    t.setText("3");
                    break;
                case "4":
                    t.setText("4");
                    break;
                case "5":
                    t.setText("5");
                    break;
                case "6":
                    t.setText("6");
                    break;
                case "7":
                    t.setText("7");
                    break;


            }

        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }


}

