package com.namdin.easycompass.ui.settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.namdin.easycompass.R;
import com.namdin.easycompass.base.BaseActivity;
import com.namdin.easycompass.base.BasePresenter;


public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private LinearLayout llAbout;
    private LinearLayout llRate;
    private LinearLayout llShare;
    private NativeExpressAdView mContainerAd;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.setting);

        llAbout = findViewById(R.id.ll_about_us);
        llRate = findViewById(R.id.ll_rate_us);
        llShare = findViewById(R.id.ll_share);
        mContainerAd = findViewById(R.id.ads_banner_setting);

        llAbout.setOnClickListener(this);
        llRate.setOnClickListener(this);
        llShare.setOnClickListener(this);

        adsUnit();


    }

    private void adsUnit() {
        final NativeExpressAdView mAdView = new NativeExpressAdView(this);
        final AdRequest request = new AdRequest.Builder().build();
        mAdView.setAdSize(new AdSize(AdSize.FULL_WIDTH, 350));
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_about_us:
                Intent intent = new Intent(this, ActivityAboutUs.class);
                startActivity(intent);
                break;
            case R.id.ll_rate_us:
                Uri uri = Uri.parse("market://details?id=" + getApplication().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getApplication().getPackageName())));

                }
                break;
            case R.id.ll_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + getApplication().getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

        }
    }
}
