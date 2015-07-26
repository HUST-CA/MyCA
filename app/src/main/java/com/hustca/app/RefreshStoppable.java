package com.hustca.app;

/**
 * Created by Hamster on 2015/7/26.
 * <p/>
 * Implemented by an activity, notify the activity that
 * the fragment has finished refreshing, and it should
 * stop all refresh indicators. See {@link Refreshable}.
 */
public interface RefreshStoppable {
    void onRefreshStopped();
}
