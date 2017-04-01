package com.blindstick.el213_grp3.sticklocater;

import android.app.AlertDialog;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onurciner.toastox.ToastOX;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TrackingIdDialogFragment.OnDataPass {

    private GoogleMap mMap;
    private double latitude,longitude;
    private long time;
    private LatLng currentLocation;
    private Marker mCurrentMarker;
    private String trackingId=null;
    private AlertDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference user;
    Button btn_trackAnotherStick;
    private static int INTERVAL = 1000*60;
    Handler mHandler;
    Runnable mHandlerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressDialog = new SpotsDialog(this, R.style.Custom2);
        progressDialog.setCancelable(false);
        TrackingIdDialogFragment trackingIdDialog = new TrackingIdDialogFragment();
        trackingIdDialog.show(getSupportFragmentManager(),"tracking id dialog");

        btn_trackAnotherStick = (Button) findViewById(R.id.btn_trackAnotherStick);

        btn_trackAnotherStick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_trackAnotherStick.setVisibility(View.INVISIBLE);
                TrackingIdDialogFragment trackingIdDialog = new TrackingIdDialogFragment();
                trackingIdDialog.show(getSupportFragmentManager(),"tracking id dialog");
            }
        });

        mHandler = new Handler();

        mHandlerTask = new Runnable() {
            @Override
            public void run() {
                setTitle();
                mHandler.postDelayed(mHandlerTask,INTERVAL);
            }
        };

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void showLocationMarkerOnMap() {
        currentLocation = new LatLng(latitude,longitude);
        if(mCurrentMarker!=null) {
            mCurrentMarker.remove();
        }
        INTERVAL = 1000*60;
        mCurrentMarker = mMap.addMarker(new MarkerOptions().position(currentLocation));
        mHandlerTask.run();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        mCurrentMarker.setSnippet("Last updated at " + date.toString());
        mCurrentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLocation, 15);
        mMap.animateCamera(update);
    }

    public void setTitle() {
        long timeDiff = (new Date().getTime() - time)/(1000*60);
        String plural = "s";
        String unit = "minute";
        if(timeDiff>=60) {
            timeDiff = timeDiff/60;
            unit = "hour";
            INTERVAL = INTERVAL * 60;
            if(timeDiff>=24) {
                timeDiff = timeDiff/24;
                unit = "day";
                INTERVAL = INTERVAL * 24;
                if(timeDiff>=30) {
                    long temp = timeDiff;
                    timeDiff = timeDiff/30;
                    unit = "month";
                    INTERVAL = INTERVAL * 30;
                    if(temp>=365) {
                        timeDiff = temp/365;
                        unit = "year";
                        INTERVAL = INTERVAL * 365;
                    }
                }
            }
        }
        if(timeDiff==1) {
            plural = "";
        }
        mCurrentMarker.setTitle("Updated " + timeDiff + " " + unit + plural + " ago.");
    }

    @Override
    public void onDataPass(String data) {
      trackingId=data;
    }
    public void getLocation(){
        progressDialog.show();

        firebaseDatabase = FirebaseDatabase.getInstance();
        user = firebaseDatabase.getReference(trackingId);

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Object> map = (HashMap)dataSnapshot.getValue();
                latitude = (Double)map.get("Latitude");
                longitude = (Double)map.get("Longitude");
                time = (Long) map.get("Time");
                progressDialog.dismiss();
                //Toast.makeText(MapsActivity.this, "", Toast.LENGTH_SHORT).show();
                ToastOX.info(getApplicationContext(), "Locating...");
                showLocationMarkerOnMap();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
