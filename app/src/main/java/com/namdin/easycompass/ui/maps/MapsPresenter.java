package com.namdin.easycompass.ui.maps;


import com.namdin.easycompass.base.BasePresenter;

public class MapsPresenter extends BasePresenter<MapsView> {
    public MapsPresenter(MapsView mapsView) {
        super.attach(mapsView);
    }

    public void rotateCamera(float mAzimuth) {
        mView.rotateCamera(mAzimuth);
    }
}
