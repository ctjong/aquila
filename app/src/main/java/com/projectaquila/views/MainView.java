package com.projectaquila.views;

import android.content.Intent;

import com.facebook.login.widget.LoginButton;
import com.projectaquila.R;
import com.projectaquila.AppContext;
import com.projectaquila.Callback;
import com.projectaquila.services.AuthService;

import java.util.HashMap;

/**
 * Main activity
 */
public class MainView extends ViewBase {
    /**
     * Get layout ID
     * @return ID of the layout to use for the current activity
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_main;
    }

    /**
     * Initialize the view for the current activity
     */
    @Override
    protected void initializeView(){
        final AuthService authService = AppContext.current.getAuthService();
        AppContext.current.getShell().addActivityResultHandler(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params) {
                int requestCode = (int)params.get("requestCode");
                int resultCode = (int)params.get("resultCode");
                Intent data = (Intent)params.get("data");
                authService.onFbActivityResult(requestCode, resultCode, data);
            }
        });
        AppContext.current.getAuthService().checkLoginStatus(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params) {
                boolean isLoggedIn = (boolean)params.get("isLoggedIn");
                if(isLoggedIn){
                    AppContext.current.getNavigationService().navigate(new TasksView(), null);
                    return;
                }
                LoginButton fbLoginButton = (LoginButton) findViewById(R.id.page_main_loginbutton);
                authService.setupFbLoginButton(fbLoginButton, new Callback(){
                    @Override
                    public void execute(HashMap<String, Object> params) {
                        // check fb token retrieval response
                        String fbToken = (String)params.get("fbToken");
                        if(params.get("status") == "error" || fbToken == null || AppContext.current.getAccessToken() != null) {
                            AppContext.current.getShell().showContentScreen();
                            //TODO display login error
                            return;
                        }

                        // set state to loading and try to convert fb token
                        // navigate to tasks view on success
                        AppContext.current.getShell().showLoadingScreen();
                        authService.convertFbToken(fbToken, new Callback() {
                            @Override
                            public void execute(HashMap<String, Object> params) {
                                String token = (String)params.get("token");
                                if(params.get("status") == "error" || token == null) {
                                    //TODO display login error
                                    AppContext.current.getShell().showContentScreen();
                                    return;
                                }

                                setLocalSetting("token", token);
                                AppContext.current.getNavigationService().navigate(new TasksView(), null);
                            }
                        });
                    }
                });
                AppContext.current.getShell().showContentScreen();
            }
        });
    }
}
