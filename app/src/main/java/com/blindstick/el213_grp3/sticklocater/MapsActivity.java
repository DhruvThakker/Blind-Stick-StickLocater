package com.blindstick.el213_grp3.sticklocater;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TrackingIdDialogFragment.OnDataPass {

    private GoogleMap mMap;
    private double latitude,longitude;
    private long time;
    private LatLng currentLocation;
    private Marker mCurrentMarker;
    private String trackingId=null;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference root,user;
    Boolean proceed;
    Button btn_trackAnotherStick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Getting Location");
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
        mCurrentMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Updated " + ((new Date().getTime() - time)/(1000*60)) + " minutes ago."));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        mCurrentMarker.setSnippet("Last updated at " + date.toString());
        mCurrentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLocation, 15);
        mMap.animateCamera(update);
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
                Toast.makeText(MapsActivity.this, "Locating...", Toast.LENGTH_SHORT).show();
                showLocationMarkerOnMap();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
