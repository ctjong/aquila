package com.projectaquila.models;

import com.projectaquila.AppContext;

import java.util.HashMap;

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

    public Callback getHandler(){
        return new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                if(mIsLogoutMenu){
                    AppContext.getCurrent().getAuthService().logOut();
                }
                AppContext.getCurrent().getNavigationService().navigate(mTarget, null);
            }
        };
    }
}
