package com.namdin.easycompass.base;

public class BasePresenter<V> {
    protected V mView;

    public void attach(V view) {
        this.mView = view;
    }

    public void detachView() {
        this.mView = null;
    }
}
