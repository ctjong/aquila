package com.projectaquila.drawer;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;

public class UserDrawerItem extends DrawerItem {
    public UserDrawerItem(){
        super(R.dimen.draweritem_user_height, R.color.colorPrimary, R.color.white);
    }

    @Override
    public String getLine1String() {
        String hello = AppContext.getCurrent().getActivity().getString(R.string.menu_userhello);
        return hello.replace("{name}", AppContext.getCurrent().getActiveUser().getFirstName());
    }

    @Override
    public String getLine2String() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public void invoke(){
        // do nothing
    }
}
