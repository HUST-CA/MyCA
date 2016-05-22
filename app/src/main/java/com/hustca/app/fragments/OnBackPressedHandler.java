package com.hustca.app.fragments;

/**
 * Created by hamster on 16/5/22.
 * <p/>
 * Make fragments able to handle Activity.onBackPressed
 */
public interface OnBackPressedHandler {
    /**
     * Same as Activity.onBackPressed()
     *
     * @return false if not handled, true if handled
     */
    boolean onBackPressed();
}
