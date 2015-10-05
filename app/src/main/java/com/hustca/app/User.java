package com.hustca.app;

/**
 * Created by Hamster on 2015/7/27.
 * <p/>
 * A class representing an user.
 */
public class User {
    // Note: avatar is not added here. Load it with UserUtil
    public static final int INVALID_USER_ID = -1;
    private int mId;
    private String mName;

    public User(String name, int id) {
        mName = name;
        mId = id;
        // TODO: Warn about invalid IDs
    }

    public User() {
    }
}
