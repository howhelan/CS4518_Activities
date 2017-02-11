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
import android.widget.ImageView;
import android.media.MediaPlayer;

import android.content.Intent;
import android.content.IntentFilter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionApi;
import com.google.android.gms.wallet.wobs.TimeInterval;

import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    public static final String EXTRA_KEY_IN = "EXTRA_IN";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    public GoogleApiClient mApiClient;

    GoogleMap mMap;
    LocationRequest mLocationRequest;
    Marker currLocation;
    LatLng latLng;

    public TextView myText;
    public ImageView myImage;

    public MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();

        mApiClient.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myText = (TextView) findViewById(R.id.activityText);
        myImage = (ImageView) findViewById(R.id.activityImage);
        mediaPlayer = MediaPlayer.create(this, R.raw.beat_02);
        mediaPlayer.setLooping(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        if (mApiClient == null) {
            mApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(ActivityRecognition.API)
                    .build();
        }

        mApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);

        mHandler = new Handler();
        startRepeatingTask();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
        if (mLastLocation != null) {
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocation = mMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onLocationChanged(Location location) {
        if (currLocation != null) {
            currLocation.remove();
        }

        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocation = mMap.addMarker(markerOptions);

        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(14).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

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
            //TextView t = (TextView)findViewById(R.id.activityText);

            mediaPlayer.start();

            switch(activity){
                case "0":
                    myText.setText("You are driving");
                    myImage.setImageResource(R.mipmap.in_vehicle);
                    break;
                case "1":
                    //t.setText("1");
                    break;
                case "2":
                    myText.setText("You are walking");
                    myImage.setImageResource(R.mipmap.walking);
                    break;
                case "3":
                    myText.setText("You are running");
                    myImage.setImageResource(R.mipmap.running);
                    break;
                case "4":
                    //t.setText("4");
                    break;
                case "5":
                    myText.setText("You are not moving");
                    myImage.setImageResource(R.mipmap.still);
                    mediaPlayer.stop();
                    break;
                case "6":
                    //t.setText("6");
                    break;
                case "7":
                    myText.setText("You are walking");
                    myImage.setImageResource(R.mipmap.walking);
                    break;


            }

        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }


}

