package com.namdin.easycompass.sensor;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.namdin.easycompass.utils.CompassUtils;

public class CompassSensorManager {
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Sensor mMagneticSensor;
    private Sensor mOrientationSensor;

    private float[] mGravity;
    private float[] mGeoMagnetic;

    private float mAzimuth;
    private int mOrient;

    private Context mContext;

    public CompassSensorManager(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mOrientationSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    public void registerAccListener(SensorEventListener sensorEventListener) {
        mSensorManager.registerListener(sensorEventListener,
                mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void registerMagneticListener(SensorEventListener sensorEventListener) {
        mSensorManager.registerListener(sensorEventListener, mMagneticSensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    public void registerOrientListener(SensorEventListener sensorEventListener) {
        mSensorManager.registerListener(sensorEventListener,
                mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregisterListener(SensorEventListener sensorEventListener) {
        mSensorManager.unregisterListener(sensorEventListener);
    }

    public float getAzimuth() {
        return mAzimuth;
    }

    public int getOrient() {
        return mOrient;
    }

    public float[] getGravity() {
        return mGravity;
    }

    public void setGravity(float[] gravity) {
        mGravity = gravity;
    }

    public float[] getGeoMagnetic() {
        return mGeoMagnetic;
    }

    public void setGeoMagnetic(float[] geoMagnetic) {
        mGeoMagnetic = geoMagnetic;
    }

    public void updateAzimuth() {
        if (mGravity != null && mGeoMagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                    mGeoMagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                mAzimuth = (float) Math.toDegrees(orientation[0]);
                mAzimuth = convertSensorValue(mAzimuth);
            }
        }
    }

    private float convertSensorValue(float value) {
        // TODO Auto-generated method stub
        if (mContext != null) {
            mOrient = CompassUtils.getScreenOrientation(mContext);
            if (mOrient == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                value += 90;
            } else if (mOrient == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                value -= 90;
            }
        }

        /*if (CompassCommonConfigure.KEY_PREF_INDICATE_NORTH) {
            value -= 11.3;
        }*/

        if (value < 0) {
            value += 360;
        }
        return value;
    }
}

