package com.projectaquila.drawer;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.services.HelperService;
import com.projectaquila.views.LoginView;

public class LogoutDrawerItem extends DrawerItem {
    public LogoutDrawerItem(){
        super(R.dimen.draweritem_logout_height, R.color.white, R.color.gray);
    }

    @Override
    public String getLine1String() {
        return AppContext.getCurrent().getActivity().getString(R.string.menu_logout);
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
    public void invoke() {
        HelperService.showAlert(R.string.prompt_logout_title, R.string.prompt_logout_msg, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                AppContext.getCurrent().getAuthService().logOut();
                AppContext.getCurrent().getNavigationService().navigate(LoginView.class, null);
            }
        }, null);
    }
}
