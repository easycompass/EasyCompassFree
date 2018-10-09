package com.namdin.easycompass.utils;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class InterstitialUtils {
    private final String APP_ID = "ca-app-pub-8167507850220592~8105405010";
    private final String INTERSTITIAL_AD_UNIT = "ca-app-pub-8167507850220592/4984417656";
    private InterstitialAd interstitialAd;
    private static InterstitialUtils sharedInstance;
    private AdCloseListener adCloseListener;
    private boolean isReloaded = false;

    public static InterstitialUtils getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new InterstitialUtils();
        }
        return sharedInstance;
    }

    public void init(Context context) {
        MobileAds.initialize(context, APP_ID);
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(INTERSTITIAL_AD_UNIT);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (adCloseListener != null) {
                    adCloseListener.onAdClosed();
                }
                loadInterstitialAd();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (isReloaded == false) {
                    isReloaded = true;
                    loadInterstitialAd();
                }
            }
        });
        loadInterstitialAd();
    }

    private void loadInterstitialAd() {
        if (interstitialAd != null && !interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
        }
    }

    public interface AdCloseListener {
        void onAdClosed();
    }

    public void showInterstitialAd(AdCloseListener adCloseListener) {
        if (canShowInterstitialAd()) {
            isReloaded = false;
            this.adCloseListener = adCloseListener;
            interstitialAd.show();

        } else {
            loadInterstitialAd();
            adCloseListener.onAdClosed();
        }
    }

    private boolean canShowInterstitialAd() {
        return interstitialAd != null && interstitialAd.isLoaded();
    }
}

