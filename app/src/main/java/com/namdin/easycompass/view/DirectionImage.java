package com.namdin.easycompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class DirectionImage extends AppCompatImageView {

    private float mDegress = 0;
    private int mCx, mCy;
    private boolean isMainDrag = false;

    public DirectionImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDegress(float degress) {
        mDegress = degress;
    }

    public void setMainDrag(boolean isMainDrag) {
        this.isMainDrag = isMainDrag;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!isMainDrag) {
            int w = canvas.getWidth();
            int h = canvas.getHeight();
            mCx = w / 2;
            mCy = h / 2;
        }
        canvas.rotate(mDegress, mCx, mCy);
        super.onDraw(canvas);
    }
}
