package com.hustca.app.util;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by Hamster on 2015/8/7.
 * <p/>
 * This class is to keep drawable consistent between
 * what {@link com.hustca.app.util.networking.AsyncLoader} and TextView holds.
 */
@SuppressWarnings("deprecation")
public class BitmapDrawableContainer extends BitmapDrawable {
    public Drawable mDrawable;

    @Override
    public void draw(Canvas canvas) {
        if (mDrawable != null) {
            mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
            mDrawable.draw(canvas);
        }
    }
}
