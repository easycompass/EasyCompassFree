package com.namdin.easycompass.ui.address;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.namdin.easycompass.R;
import com.namdin.easycompass.base.BaseActivity;
import com.namdin.easycompass.utils.Constants;

public class AddressActivity extends BaseActivity<AddressPresenter> implements AddressView, OnMapReadyCallback {
    private Toolbar mToolbar;
    private TextView mTvAddress, mTvLatitude, mTvLongitude;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private NativeExpressAdView mContainerAd;
    private Location mLocation;
    private double mLatitude, mLongitude;
    private String mAddressFull;
    private float DEFAULT_ZOOM = 15f;

    @Override
    protected AddressPresenter createPresenter() {
        return new AddressPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        initView();
        adsUnit();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Location");

        mTvAddress = findViewById(R.id.tv_info_address);
        mTvLatitude = findViewById(R.id.tv_latitude);
        mTvLongitude = findViewById(R.id.tv_longitude);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.address_map_view);
        mapFragment.getMapAsync(this);

        mContainerAd = findViewById(R.id.ads_banner_address);
        mLocation = getIntent().getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        mAddressFull = getIntent().getStringExtra(Constants.LOCATION_DATA_STRING_EXTRA);
        mLatitude = mLocation.getLatitude();
        mLongitude = mLocation.getLongitude();


        mPresenter.setTextAll(mAddressFull, mLatitude, mLongitude);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void setTextAll(String addressFull, double latitude, double longitude) {
        mTvAddress.setText(addressFull);
        mTvLatitude.setText(String.valueOf(latitude));
        mTvLongitude.setText(String.valueOf(longitude));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setCompassEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude,
                mLongitude), DEFAULT_ZOOM));
    }

    private void adsUnit() {
        final NativeExpressAdView mAdView = new NativeExpressAdView(this);
        final AdRequest request = new AdRequest.Builder().build();
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId("ca-app-pub-8167507850220592/1491609793");
        mContainerAd.addView(mAdView);
        mAdView.loadAd(request);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();

            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

            }
        });
    }
}

