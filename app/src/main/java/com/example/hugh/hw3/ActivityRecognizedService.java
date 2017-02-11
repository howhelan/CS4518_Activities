package com.example.hugh.hw3;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.ContentValues;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Date;


import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ActivityRecognizedService extends IntentService {

    public static final String ACTION_MyIntentService = "com.example.androidintentservice.RESPONSE";
    public static final String ACTION_MyUpdate = "com.example.androidintentservice.UPDATE";
    public static final String EXTRA_KEY_IN = "EXTRA_IN";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";
    String msgFromActivity;
    String extraOut;

    private DBHelper DbHelper;
    private SQLiteDatabase db;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
            System.out.println("checkpoint");
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        int maxConfidence = 0;
        int maxActivity = probableActivities.get(0).getConfidence();

        DBHelper DbHelper = new DBHelper(getApplicationContext());
        db = DbHelper.getWritableDatabase();



        System.out.println("checkpoint");
        //((MainActivity) mcontext).updateText("bla bla bla");

        for( DetectedActivity activity : probableActivities ){
            int confidence = activity.getConfidence();
            if( confidence > maxConfidence ) {
                maxActivity = activity.getType();
                maxConfidence = activity.getConfidence();
            }
        }

        if(maxConfidence > 75) {
            // Create a new map of values, where column names are the keys
            long timeStamp = (new Date().getTime()) / 1000;

            ContentValues values = new ContentValues();
            values.put(ActivitySchema.Entry.ACTIVITY, maxActivity);
            values.put(ActivitySchema.Entry.TIME, timeStamp);
            values.put(ActivitySchema.Entry.CONFIDENCE, maxConfidence);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(ActivitySchema.Entry.TABLE_NAME, null, values);
        }


    }
}
