package com.projectaquila.common;

import com.projectaquila.contexts.AppContext;

import java.util.HashMap;

public class DrawerItem {
    private String mTitle;
    private Class mTarget;
    private boolean mIsLogoutMenu;
    private HashMap<String, Object> mNavParams;

    public DrawerItem(String title, Class target, boolean isLogoutMenu, HashMap<String, Object> navParams){
        mTitle = title;
        mTarget = target;
        mIsLogoutMenu = isLogoutMenu;
        mNavParams = navParams;
    }

    public String getTitle(){
        return mTitle;
    }

    public HashMap<String, Object> getNavParams(){
        return mNavParams;
    }

    public void invoke(){
        if(mIsLogoutMenu){
            AppContext.getCurrent().getAuthService().logOut();
        }
        AppContext.getCurrent().getNavigationService().navigate(mTarget, mNavParams);
    }

    @Override
    public String toString(){
        return mTitle;
    }
}
