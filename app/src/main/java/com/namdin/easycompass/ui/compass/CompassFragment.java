package com.namdin.easycompass.ui.compass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.namdin.easycompass.R;
import com.namdin.easycompass.base.BaseFragment;
import com.namdin.easycompass.location.CompassLocationService;
import com.namdin.easycompass.sensor.CompassSensorManager;
import com.namdin.easycompass.ui.address.AddressActivity;
import com.namdin.easycompass.ui.calibrate.CalibrateActivity;
import com.namdin.easycompass.ui.maps.MapsActivity;
import com.namdin.easycompass.ui.rate.RateAppDialog;
import com.namdin.easycompass.ui.settings.SettingActivity;
import com.namdin.easycompass.utils.CompassUtils;
import com.namdin.easycompass.utils.Constants;
import com.namdin.easycompass.utils.InterstitialUtils;
import com.namdin.easycompass.view.DirectionImage;


public class CompassFragment extends BaseFragment<CompassPresenter> implements SensorEventListener, CompassView, View.OnClickListener {

    public static final String CALIBRATE_MAGENTIC = "CALIBRATE_MAGENTIC";
    public static final int REQUEST_CHECK_SETTINGS = 100;

    private CompassSensorManager mCompassSensorManager;
    private DirectionImage mDirectionImage;
    private TextView mTvLat, mTvLon, mTvCity, mTvDegreesDirection;
    private ImageView mIvMaps, mIvSettings, mIvStarRate, mIvWarning, mIvAddress;
    private Typeface mTypeface;
    private int mCalibareMagnetic = 0;
    private float mMagnetic;
    private float[] mAccelValues = new float[]{0f, 0f, 9.8f};
    private float[] mMagneticValues = new float[]{0.5f, 0f, 0f};
    private float mAzimuth;

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private AddressResultReceiver mResultReceiver;
    private String mAddressOutput;
    private String mAddressFull;

    private NativeExpressAdView mContainerAd;

    public CompassFragment() {
    }

    public static CompassFragment newInstance() {
        return new CompassFragment();
    }

    @Override
    protected CompassPresenter createPresenter() {
        return new CompassPresenter(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTypeface = ResourcesCompat.getFont(getActivity(), R.font.roboto_bold);
        mCompassSensorManager = new CompassSensorManager(getActivity());
        mResultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        createLocationRequest();
        locationSetting();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_compass, container, false);
        RateAppDialog.showDialog(getActivity(), getFragmentManager());
        mIvMaps = view.findViewById(R.id.iv_map);
        mIvSettings = view.findViewById(R.id.iv_settings);
        mIvStarRate = view.findViewById(R.id.iv_star_rate);
        mIvWarning = view.findViewById(R.id.iv_warning);
        mIvAddress = view.findViewById(R.id.iv_address);

        mIvMaps.setOnClickListener(this);
        mIvSettings.setOnClickListener(this);
        mIvStarRate.setOnClickListener(this);
        mIvWarning.setOnClickListener(this);
        mIvAddress.setOnClickListener(this);

        mDirectionImage = view.findViewById(R.id.iv_compass);
        mTvLat = view.findViewById(R.id.tv_latitude);
        mTvLon = view.findViewById(R.id.tv_longitude);
        mTvCity = view.findViewById(R.id.tv_location_city);
        mTvDegreesDirection = view.findViewById(R.id.tv_degrees_direction);
        mTvCity.setTypeface(mTypeface);
        mTvDegreesDirection.setTypeface(mTypeface);
        mContainerAd = view.findViewById(R.id.ads_banner_home);

        adsUnit();
        getLastLocation();


        return view;
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                mPresenter.setLocationText(location.getLatitude(), location.getLongitude());
                mLastLocation = location;
                getAddress();
            }

        }
    };

    private void adsUnit() {
        final NativeExpressAdView mAdView = new NativeExpressAdView(getContext());
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

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLastLocation = location;
                            mPresenter.setLocationText(location.getLatitude(), location.getLongitude());
                            getAddress();
                        } else {
                        }
                    }
                });
    }

    private void getAddress() {
        if (!Geocoder.isPresent()) {
            Toast.makeText(getActivity(),
                    "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        startIntentService();
    }

    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), CompassLocationService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCompassSensorManager.registerAccListener(this);
        mCompassSensorManager.registerMagneticListener(this);
        mCompassSensorManager.registerOrientListener(this);
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCompassSensorManager.unregisterListener(this);
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        mPresenter.changeDirection(sensorEvent);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        switch (sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                switch (i) {
                    case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                        mPresenter.calibrateCompass(SensorManager.SENSOR_STATUS_ACCURACY_LOW);
                        mCalibareMagnetic = SensorManager.SENSOR_STATUS_ACCURACY_LOW;
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                        mPresenter.calibrateCompass(SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM);
                        mCalibareMagnetic = SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                        mPresenter.calibrateCompass(SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
                        mCalibareMagnetic = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
                        break;

                }
        }
    }


    @Override
    public void setChangeDirection(SensorEvent sensorEvent) {
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
            mDirectionImage.setDegress(-mAzimuth);
            mDirectionImage.invalidate();
            int degrees = Math.round(mAzimuth);
            String direction = CompassUtils.displayCurrentDirection(mAzimuth);
            mTvDegreesDirection.setText(getString(R.string.text_degrees_direction, degrees, direction));
        }
    }

    @Override
    public void setLocationText(double lat, double lon) {
        String latitude = CompassUtils.decimalToDMS(lat) + CompassUtils.getLatSymbol(lat, true);
        String longitude = CompassUtils.decimalToDMS(lon) + CompassUtils.getLatSymbol(lon, false);
        mTvLat.setText(latitude);
        mTvLon.setText(longitude);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_settings:
//                InterstitialUtils.getSharedInstance().showInterstitialAd(new InterstitialUtils.AdCloseListener() {
//                    @Override
//                    public void onAdClosed() {
//                        Intent intent = new Intent(getActivity(), SettingActivity.class);
//                        startActivity(intent);
//                    }
//                });
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);

                break;
            case R.id.iv_map:
                InterstitialUtils.getSharedInstance().showInterstitialAd(new InterstitialUtils.AdCloseListener() {
                    @Override
                    public void onAdClosed() {
                        mPresenter.openViewMaps();
                    }
                });
                break;
            case R.id.iv_star_rate:
                new RateAppDialog().show(getFragmentManager(), null);
                break;
            case R.id.iv_warning:
                mPresenter.openWarning();
                break;
            case R.id.iv_address:
//                InterstitialUtils.getSharedInstance().showInterstitialAd(new InterstitialUtils.AdCloseListener() {
//                    @Override
//                    public void onAdClosed() {
//                        mPresenter.openAddress();
//                    }
//                });
                mPresenter.openAddress();
                break;

        }
    }

    @Override
    public void showViewMaps() {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void showAddress() {
        Intent intent = new Intent(getActivity(), AddressActivity.class);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        intent.putExtra(Constants.LOCATION_DATA_STRING_EXTRA, mAddressFull);
        startActivity(intent);
    }

    @Override
    public void showLocationIcon() {
        if (mIvAddress != null) {
            mIvAddress.setVisibility(View.VISIBLE);
            mIvMaps.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void needToCalibrateCompass(int calibrate) {
        if (1 == calibrate || 2 == calibrate) {
            mIvWarning.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_warning_calibrate));
        } else {
            mIvWarning.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimaryWhite));
        }
    }

    @Override
    public void showWarning() {
        Intent intent = new Intent(getActivity(), CalibrateActivity.class);
        intent.putExtra(CALIBRATE_MAGENTIC, mCalibareMagnetic);
        startActivity(intent);
    }

    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY_ADDRESS);
            mAddressFull = resultData.getString(Constants.RESULT_DATA_KEY_ADDRESS_FULL);
            if (mAddressOutput == null) {
                mAddressOutput = "";
            }
            if (mAddressFull == null) {
                mAddressFull = "";
            }
            displayAddressOutput(mAddressOutput);
            if (!mAddressOutput.isEmpty() || mAddressOutput.length() > 0) {
                mPresenter.showLocationIcon();
            }
        }
    }

    private void displayAddressOutput(String currentAddress) {
        mTvCity.setText(currentAddress);
    }

    private void locationSetting() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                            } catch (ClassCastException e) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }

                }
            }
        });

    }


}
