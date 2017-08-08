package com.projectaquila.drawer;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;

public class UserDrawerItem extends DrawerItem {
    public UserDrawerItem(){
        super(80, R.color.colorPrimary, R.color.white);
    }

    @Override
    public String getLine1String() {
        String hello = AppContext.getCurrent().getActivity().getString(R.string.menu_userhello);
        String userHello = hello.replace("{name}", AppContext.getCurrent().getActiveUser().getFirstName());
        return userHello;
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
