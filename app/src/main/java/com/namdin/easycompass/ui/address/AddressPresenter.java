package com.namdin.easycompass.ui.address;

import com.namdin.easycompass.base.BasePresenter;

public class AddressPresenter extends BasePresenter<AddressView> {

    public AddressPresenter(AddressView view) {
        super.attach(view);
    }

    public void setTextAll(String addressFull, double latitude, double longitude) {
        mView.setTextAll(addressFull, latitude, longitude);
    }
}
