package com.namdin.easycompass.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.Surface;

public class CompassUtils {
    private static final float ALPHA = 0.1f;

    public static int getScreenOrientation(Context context) {
        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay()
                .getRotation();
        int orientation = context.getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (rotation == Surface.ROTATION_0
                    || rotation == Surface.ROTATION_270) {
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            } else {
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            }
        }

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (rotation == Surface.ROTATION_0
                    || rotation == Surface.ROTATION_90) {
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            } else {
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
        }

        return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public static float[] lowPass(float[] input, float[] output) {
        if (output == null) {
            return input;
        }

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    public static String displayCurrentDirection(float azimuth) {
        String direct = "";
        if (azimuth <= 22.5 || azimuth >= 337.5) {
            direct += "N";
        } else if (azimuth > 22.5 && azimuth < 67.5) {
            direct += "NE";
        } else if (azimuth >= 67.5 && azimuth <= 112.5) {
            direct += "E";
        } else if (azimuth > 112.5 && azimuth < 157.5) {
            direct += "SE";
        } else if (azimuth >= 157.5 && azimuth <= 202.5) {
            direct += "S";
        } else if (azimuth > 202.5 && azimuth < 247.5) {
            direct += "SW";
        } else if (azimuth >= 247.5 && azimuth <= 292.5) {
            direct += "W";
        } else if (azimuth > 292.5 && azimuth < 337.5) {
            direct += "NW";
        }
        return direct;
    }

    public static String decimalToDMS(double coordinator) {
        String degree, minute, second;

        int intPart = (int) coordinator;
        degree = String.valueOf(intPart);

        double mod = coordinator % 1;
        coordinator = mod * 60;
        intPart = (int) coordinator;
        if (Math.abs(intPart) < 10) {
            minute = "0" + intPart;
        } else {
            minute = String.valueOf(intPart);
        }

        mod = coordinator % 1;
        coordinator = mod * 60;
        intPart = (int) coordinator;
        if (Math.abs(intPart) < 10) {
            second = "0" + intPart;
        } else {
            second = String.valueOf(intPart);
        }

        return degree + "\u00b0" + minute + "'" + second + "\"";
    }

    public static String getLatSymbol(double coordinator, boolean isLat) {
        if (isLat) {
            return coordinator < 0.0d ? "S" : "N";
        } else {
            return coordinator < 0.0d ? "W" : "E";
        }
    }

}
