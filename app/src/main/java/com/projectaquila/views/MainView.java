package com.projectaquila.views;

import android.content.Intent;

import com.facebook.login.widget.LoginButton;
import com.projectaquila.R;
import com.projectaquila.AppContext;
import com.projectaquila.models.Callback;
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

        // setup activity result event handler
        AppContext.current.getShell().addActivityResultHandler(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params) {
                int requestCode = (int)params.get("requestCode");
                int resultCode = (int)params.get("resultCode");
                Intent data = (Intent)params.get("data");
                authService.onFbActivityResult(requestCode, resultCode, data);
            }
        });

        // check if logged in on launch
        if(authService.isUserLoggedIn()){
            AppContext.current.getNavigationService().navigate(new TasksView(), null);
            return;
        }

        // clear any existing login session and setup login button
        AppContext.current.getAuthService().logOut();
        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.page_main_loginbutton);
        authService.setupFbLoginButton(fbLoginButton, new Callback(){
            @Override
            public void execute(HashMap<String, Object> params) {
                // check fb token retrieval response
                String fbToken = (String)params.get("fbToken");
                if(params.get("status") == "error" || fbToken == null) {
                    AppContext.current.getShell().showContentScreen();
                    //TODO display login error
                    return;
                }

                // show loading screen
                AppContext.current.getShell().showLoadingScreen();

                // convert fb token to long term token
                authService.convertFbToken(fbToken, new Callback() {
                    @Override
                    public void execute(HashMap<String, Object> params) {
                        String token = (String)params.get("token");
                        if(params.get("status") == "error" || token == null) {
                            //TODO display login error
                            AppContext.current.getShell().showContentScreen();
                            return;
                        }
                        AppContext.current.getNavigationService().navigate(new TasksView(), null);
                    }
                });
            }
        });

        // show content
        AppContext.current.getShell().showContentScreen();
    }
}
