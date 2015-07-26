package com.hustca.app;

/**
 * Created by Hamster on 2015/7/26.
 * <p/>
 * Implemented by a fragment, to notify the fragment that
 * it should refresh its data now. To notify the activity
 * to stop refreshing indicators, see {@link Refreshable}
 */
public interface Refreshable {
    void refresh();
}
