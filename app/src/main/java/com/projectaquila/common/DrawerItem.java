package com.projectaquila.common;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.services.HelperService;

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
            HelperService.showAlert(R.string.prompt_logout_title, R.string.prompt_logout_msg, new Callback() {
                @Override
                public void execute(CallbackParams params) {
                    AppContext.getCurrent().getAuthService().logOut();
                }
            }, null);
        }else if(mTarget != null) {
            AppContext.getCurrent().getNavigationService().navigate(mTarget, mNavParams);
        }
    }

    @Override
    public String toString(){
        return mTitle;
    }
}
