package com.example.hugh.hw3;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ActivityRecognizedService extends IntentService {


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
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        int maxConfidence = 0;
        int maxActivity = 0;

        for( DetectedActivity activity : probableActivities ){
            int confidence = activity.getConfidence();
            if( confidence > maxConfidence ) {
                maxActivity = activity.getType();
                maxConfidence = activity.getConfidence();
            }
        }


        switch( maxActivity ){
            case 0: //IN_VEHICLE
                break;
            case 1: //ON_BICYCLE
                break;
            case 2: //ON_FOOT
                break;
            case 3: //RUNNING
                break;
            case 4: //STILL
                break;
            case 5: //TILTING
                break;
            case 6: //UNKNOWN
                break;
            case 7: //WALKING
                break;
            default:
                break;

        }
    }
}
