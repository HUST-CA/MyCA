package com.hustca.app;

/**
 * Created by Hamster on 2015/7/26.
 * <p/>
 * Implemented by an activity, notify the activity that
 * the fragment has finished/started refreshing, and it should
 * stop/start all refresh indicators. See {@link Refreshable}.
 */
public interface RefreshIndicator {
    /**
     * {@link RefreshIndicator}
     */
    void onRefreshStopped();

    /**
     * {@link RefreshIndicator}
     */
    void onRefreshStarted();
}
