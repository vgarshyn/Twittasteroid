package com.vgarshyn.twittasteroid.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vgarshyn.twittasteroid.R;

/**
 * Google maps activity. Show given point on map
 * <p/>
 * Created by v.garshyn on 30.07.15.
 */
public class MapActivity extends FragmentActivity {
    public static final String TAG = MapActivity.class.getSimpleName();
    public static final String EXTRA_LATITUDE = "twittasteroid.extra.coord.PARAM_LATITUDE";
    public static final String EXTRA_LONGITUDE = "twittasteroid.extra.coord.PARAM_LONGITUDE";
    public static final String EXTRA_USERNAME = "twittasteroid.extra.USERNAME";
    public static final String EXTRA_TEXT = "twittasteroid.extra.TEXT";
    private static int DURATION_ZOOM_ANIMATE = 3000;
    private static int DURATION_DELAY_BEFOR_CAMERA_MOVE = 1000;
    private static int ZOOM_FACTOR = 11;
    private GoogleMap mMap;
    private double mLatitude;
    private double mLongitude;
    private String mUsername;
    private String mTextTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (getIntent() != null) {
            mLatitude = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0);
            mLongitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0);
            mTextTweet = getIntent().getStringExtra(EXTRA_TEXT);
            mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
        }
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap(mLatitude, mLongitude, mTextTweet);
            }
        }
    }

    private void setUpMap(double latitude, double longitude, String text) {
        final LatLng coordinates = new LatLng(latitude, longitude);
        MarkerOptions options = new MarkerOptions()
                .position(coordinates)
                .title(mUsername)
                .snippet(text);
        mMap.addMarker(options);
        mMap.setMyLocationEnabled(true);
        findViewById(R.id.map).postDelayed(new Runnable() {
            @Override
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, ZOOM_FACTOR), DURATION_ZOOM_ANIMATE, null);
            }
        }, DURATION_DELAY_BEFOR_CAMERA_MOVE);

    }
}

