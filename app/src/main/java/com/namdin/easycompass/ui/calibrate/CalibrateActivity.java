package com.namdin.easycompass.ui.calibrate;

import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.namdin.easycompass.R;
import com.namdin.easycompass.base.BaseActivity;
import com.namdin.easycompass.ui.compass.CompassFragment;

public class CalibrateActivity extends BaseActivity<CalibratePresenter> implements SensorEventListener, CalibrateView {

    private Toolbar mToolbar;
    private TextView mTvMagnetic, mTVDecriptionWarning;
    private int mCaliMagnetic;

    @Override
    protected CalibratePresenter createPresenter() {
        return new CalibratePresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibrate_activity);
        initView();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.calibrate_title);

        mTvMagnetic = findViewById(R.id.tv_magnetic_value);
        mTVDecriptionWarning = findViewById(R.id.tv_decription_warning);
        String warningStr = getString(R.string.text_decription_notifi_warning_1);
        mPresenter.setIconInText(warningStr);
        mCaliMagnetic = getIntent().getIntExtra(CompassFragment.CALIBRATE_MAGENTIC, 0);
        setTextForMagnetic(mCaliMagnetic);
    }

    private void setTextForMagnetic(int magnetic) {
        if (magnetic == 1) {
            mTvMagnetic.setText(R.string.calibrate_magnetic);
            mTvMagnetic.setTextColor(ContextCompat.getColor(this, R.color.color_calibrate_low));
        } else if (magnetic == 2) {
            mTvMagnetic.setText(R.string.calibrate_medium);
            mTvMagnetic.setTextColor(ContextCompat.getColor(this, R.color.color_calibrate_medium));
        } else {
            mTvMagnetic.setText(R.string.calibrate_hight);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        switch (sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                switch (i) {
                    case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                        setTextForMagnetic(SensorManager.SENSOR_STATUS_ACCURACY_LOW);
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                        setTextForMagnetic(SensorManager.SENSOR_STATUS_ACCURACY_LOW);
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                        setTextForMagnetic(SensorManager.SENSOR_STATUS_ACCURACY_LOW);
                        break;

                }
        }
    }

    @Override
    public void addIconInText(String warningStr) {
        SpannableString spanStr = new SpannableString(warningStr);
        int index = spanStr.toString().indexOf("@");
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_warning_yellow);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ImageSpan imageSpan = new ImageSpan(drawable);
        spanStr.setSpan(imageSpan, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mTVDecriptionWarning.setText(spanStr);
    }
}
