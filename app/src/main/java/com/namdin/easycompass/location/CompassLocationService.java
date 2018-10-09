package com.namdin.easycompass.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.namdin.easycompass.R;
import com.namdin.easycompass.utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CompassLocationService extends IntentService {
    private static final String TAG = CompassLocationService.class.getSimpleName();
    private Location mLocation;
    private ResultReceiver mReceiver;

    public CompassLocationService() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        if (mReceiver == null) {
            Log.d(TAG, "null");
            return;
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";
        mLocation = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(mLocation.getLatitude(),
                    mLocation.getLongitude(),
                    1);
        } catch (IOException e) {
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, e);
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + mLocation.getLatitude() +
                    ", Longitude = " +
                    mLocation.getLongitude(), illegalArgumentException);
        }

        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                {
                    errorMessage = getString(R.string.no_address_found);
                    Log.e(TAG, errorMessage);
                }
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage, errorMessage);
        } else {
            Address address = addresses.get(0);
            String addressStr = "";
            String addressFull = "";
            String locality = address.getLocality();
            String area = address.getAdminArea();
            String countryName = address.getCountryName();
            String featureName = address.getFeatureName();
            String thoroughfare = address.getThoroughfare();
            String subArea = address.getSubAdminArea();
            Log.i(TAG, getString(R.string.address_found));
            if (featureName != null) {
                addressFull += " " + featureName;
            }
            if (thoroughfare != null) {
                addressStr = thoroughfare;
                addressFull += " " + thoroughfare;
            }
            if (locality != null) {
                if (addressStr.length() == 0) {
                    addressStr = locality;
                }
                addressFull += " " + locality;
            }
            if (subArea != null) {
                if (addressStr.length() == 0) {
                    addressStr = subArea;
                }
                addressFull += " " + subArea;
            }
            if (area != null) {
                addressFull += " " + area;
            }
            if (countryName != null) {
                addressFull += " " + countryName;
            }
            Log.d(TAG, addressFull + " a " + address.getSubThoroughfare() + " b " + address.getSubLocality() + " c " + address.getPremises());
            deliverResultToReceiver(Constants.SUCCESS_RESULT, addressStr, addressFull);
        }
    }

    private void deliverResultToReceiver(int resultCode, String address, String addressFull) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY_ADDRESS, address);
        bundle.putString(Constants.RESULT_DATA_KEY_ADDRESS_FULL, addressFull);
        mReceiver.send(resultCode, bundle);
    }
}

