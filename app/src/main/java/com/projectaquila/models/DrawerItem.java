package com.projectaquila.models;

import com.projectaquila.AppContext;

public class DrawerItem {
    private String mTitle;
    private Class mTarget;
    private boolean mIsLogoutMenu;

    public DrawerItem(String title, Class target, boolean isLogoutMenu){
        mTitle = title;
        mTarget = target;
        mIsLogoutMenu = isLogoutMenu;
    }

    public String getTitle(){
        return mTitle;
    }

    public void invoke(){
        if(mIsLogoutMenu){
            AppContext.getCurrent().getAuthService().logOut();
        }
        AppContext.getCurrent().getNavigationService().navigate(mTarget, null);
    }

    @Override
    public String toString(){
        return mTitle;
    }
}
