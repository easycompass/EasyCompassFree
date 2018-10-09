package com.namdin.easycompass.ui.calibrate;

import com.namdin.easycompass.base.BasePresenter;

public class CalibratePresenter extends BasePresenter<CalibrateView> {
    public CalibratePresenter(CalibrateView view) {
        super.attach(view);
    }

    public void setIconInText(String warningStr) {
        mView.addIconInText(warningStr);
    }
}
