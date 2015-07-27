package com.hustca.app;

import java.util.ArrayList;

/**
 * Created by Hamster on 2015/7/27.
 * <p/>
 * A special user: myself, and thus has token and admin info.
 */
public class UserMyself extends User {
    private String mToken;
    private ArrayList<Integer> mAdminGroups;
}
