package com.namdin.easycompass.ui.compass;

import android.hardware.SensorEvent;

public interface CompassView {
    void setChangeDirection(SensorEvent sensorEvent);

    void setLocationText(double lat, double lon);

    void showViewMaps();

    void needToCalibrateCompass(int calibrate);

    void showWarning();

    void showAddress();

    void showLocationIcon();
}
