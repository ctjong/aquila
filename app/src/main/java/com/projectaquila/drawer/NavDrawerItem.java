package com.projectaquila.drawer;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;

import java.util.HashMap;

public class NavDrawerItem extends DrawerItem{
    private String mTitle;
    private String mImageUrl;
    private Class mTarget;
    private HashMap<String, Object> mNavParams;

    public NavDrawerItem(int titleStringId, String imageUrl, Class target, HashMap<String, Object> navParams){
        super(R.dimen.draweritem_nav_height, R.color.white, R.color.gray);
        mTitle = AppContext.getCurrent().getActivity().getString(titleStringId);
        mImageUrl = imageUrl;
        mTarget = target;
        mNavParams = navParams;
    }

    @Override
    public String getLine1String() {
        return mTitle;
    }

    @Override
    public String getLine2String() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return mImageUrl;
    }

    @Override
    public void invoke(){
        AppContext.getCurrent().getNavigationService().navigate(mTarget, mNavParams);
    }
}
