package com.namdin.easycompass.ui.maps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.namdin.easycompass.R;
import com.namdin.easycompass.base.BaseActivity;
import com.namdin.easycompass.sensor.CompassSensorManager;
import com.namdin.easycompass.utils.CompassUtils;
import com.namdin.easycompass.view.DirectionImage;
import com.namdin.easycompass.view.LineView;

import java.util.List;



public class MapsActivity extends BaseActivity<MapsPresenter> implements OnMapReadyCallback, SensorEventListener, GoogleMap.OnMyLocationButtonClickListener, MapsView, View.OnClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final float DEFAULT_ZOOM = 15f;
    private DirectionImage mIvCompassMap;
    private ImageView mIvAround;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private Marker mMarker;
    private CompassSensorManager mCompassSensorManager;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private float[] mAccelValues = new float[]{0f, 0f, 9.8f};
    private float[] mMagneticValues = new float[]{0.5f, 0f, 0f};
    private float mAzimuth;
    private Location mLastKnownLocation;
    private ImageView ivBackMap;
    private ImageButton mIbMyLocation, mIbRotateMap, mIbCompassMap;
    private FrameLayout mLayoutCompass;
    private EditText edtSearch;
    private LineView mLineView;

    private boolean mIsTurnOnRotate, mIsTurnOnCompass;

    @Override
    protected MapsPresenter createPresenter() {
        return new MapsPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_maps);

        init();

        mIvAround = findViewById(R.id.iv_around_compass);
        mLayoutCompass = findViewById(R.id.frameLayout);
        mIvCompassMap = findViewById(R.id.iv_compass_map);
        ivBackMap = findViewById(R.id.iv_back_map);
        edtSearch = findViewById(R.id.edt_search_map);
        mIbRotateMap = findViewById(R.id.ib_rotate_map);
        mIbMyLocation = findViewById(R.id.ib_my_location);
        mIbCompassMap = findViewById(R.id.ib_compass_map);
        mLineView = findViewById(R.id.line_view);

        ivBackMap.setOnClickListener(this);
        edtSearch.setOnClickListener(this);
        mIbRotateMap.setOnClickListener(this);
        mIbMyLocation.setOnClickListener(this);
        mIbCompassMap.setOnClickListener(this);

        mCompassSensorManager = new CompassSensorManager(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void init() {
        mIsTurnOnRotate = true;
        mIsTurnOnCompass = true;
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(2 * 1000);
        mLocationRequest.setFastestInterval(2 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mMap = googleMap;
        createLocationRequest();
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setCompassEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setZoomControlsEnabled(true);
        getDeviceLocation();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }

            List<Location> locationList = locationResult.getLocations();
            Log.d(TAG, "mLocationCallback()");
            for (Location location : locationList) {
                mLastKnownLocation = location;
                if (mMarker != null) {
                    mMarker.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location));
                markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                markerOptions.anchor(0.5f, 0.5f);
                markerOptions.flat(true);
                mMarker = mMap.addMarker(markerOptions);

                /*mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                        location.getLongitude()), DEFAULT_ZOOM));*/
            }
        }

    };


    @Override
    protected void onResume() {
        super.onResume();
        mCompassSensorManager.registerAccListener(this);
        mCompassSensorManager.registerMagneticListener(this);
        mCompassSensorManager.registerOrientListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCompassSensorManager.unregisterListener(this);
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelValues = CompassUtils.lowPass(sensorEvent.values, mAccelValues);
            mCompassSensorManager.setGravity(mAccelValues);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagneticValues = CompassUtils.lowPass(sensorEvent.values,
                    mMagneticValues);
            mCompassSensorManager.setGeoMagnetic(mMagneticValues);
        }

        mCompassSensorManager.updateAzimuth();
        float newAzimuth = mCompassSensorManager.getAzimuth();
        if (mAzimuth != newAzimuth) {
            mAzimuth = newAzimuth;
            if (mMarker != null) {
                mMarker.setRotation(mAzimuth);
            }
            if (mIsTurnOnRotate) {
                if (mIsTurnOnCompass) {
                    mIvCompassMap.setDegress(-mAzimuth);
                }
                mPresenter.rotateCamera(mAzimuth);
            } else {
                mIvCompassMap.setDegress(0);
            }
            mIvCompassMap.invalidate();
        }

    }

    @Override
    public void rotateCamera(float azimuth) {
        if (mMap == null || mLastKnownLocation == null) {
            return;
        }
        Log.d(TAG, "rotateCamera()");
        CameraPosition oldPos = mMap.getCameraPosition();
        CameraPosition pos = CameraPosition.builder(oldPos).target(new LatLng(mLastKnownLocation.getLatitude(),
                mLastKnownLocation.getLongitude()))
                .bearing(azimuth)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onMyLocationButtonClick() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation()");
        Task<Location> locationResult = mFusedLocationClient.getLastLocation();
        locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    mLastKnownLocation = task.getResult();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.");
                    Log.e(TAG, "Exception: %s", task.getException());
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_map:
                finish();
                break;
            case R.id.edt_search_map:
                findPlace();
                break;
            case R.id.ib_rotate_map:
                if (mIsTurnOnRotate) {
                    turnOffRotateMapAndCompass();
                } else {
                    turnOnRotateMapAndCompass();
                }
                break;
            case R.id.ib_my_location:
                getDeviceLocation();
                break;
            case R.id.ib_compass_map:
                if (mIsTurnOnCompass) {
                    turnOffCompass();
                } else {
                    turnOnCompass();
                }
                break;

        }
    }

    private void turnOffCompass() {
        mIsTurnOnCompass = false;
        mLayoutCompass.setVisibility(View.INVISIBLE);
        mLineView.setVisibility(View.INVISIBLE);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mIbCompassMap.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_compass_map_off));
    }

    private void turnOnCompass() {
        mIsTurnOnCompass = true;
        mLayoutCompass.setVisibility(View.VISIBLE);
        mLineView.setVisibility(View.VISIBLE);
        mIbCompassMap.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_compass_map_on));
    }

    private void turnOnRotateMapAndCompass() {
        mIsTurnOnRotate = true;
        mIbRotateMap.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_auto_rotate_on));
    }

    private void turnOffRotateMapAndCompass() {
        mIsTurnOnRotate = false;
        mPresenter.rotateCamera(0);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mIbRotateMap.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_auto_rotate_off));
    }

    public void findPlace() {
        try {
            startActivityForResult(new PlaceAutocomplete.IntentBuilder(2).build(this), 1);
        } catch (GooglePlayServicesRepairableException e) {
        } catch (GooglePlayServicesNotAvailableException e2) {
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1) {
            return;
        }
        if (resultCode == -1) {
            this.mMap.clear();
            Place place = PlaceAutocomplete.getPlace(this, data);
            LatLng latNeed = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            this.edtSearch.setText(place.getName());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latNeed, 15.0f);
            this.mMap.addMarker(new MarkerOptions().title(place.getName() + "").snippet(place.getAddress() + "").position(latNeed));
            this.mMap.animateCamera(cameraUpdate);
            //route(this.latCurrent, latNeed);
        } else if (resultCode == 2) {
            Log.e(TAG, PlaceAutocomplete.getStatus(this, data).getStatusMessage());
        } else if (resultCode != 0) {
        }
    }

}
