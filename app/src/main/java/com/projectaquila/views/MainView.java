package com.projectaquila.views;

import android.content.Intent;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.projectaquila.R;
import com.projectaquila.AppContext;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;
import com.projectaquila.services.AuthService;

import java.util.HashMap;

/**
 * Main activity
 */
public class MainView extends ViewBase {
    private TextView mErrorText;

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
        final AuthService authService = AppContext.getCurrent().getAuthService();
        setAuthErrorText("");

        // setup activity result event handler
        AppContext.getCurrent().getShell().addActivityResultHandler(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                int requestCode = (int)params.get("requestCode");
                int resultCode = (int)params.get("resultCode");
                Intent data = (Intent)params.get("data");
                authService.onFbActivityResult(requestCode, resultCode, data);
            }
        });

        // check if logged in on launch
        if(authService.isUserLoggedIn()){
            AppContext.getCurrent().getNavigationService().navigate(TasksView.class, null);
            return;
        }

        // clear any existing login session and setup login button
        AppContext.getCurrent().getAuthService().logOut();
        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.view_main_loginbutton);
        authService.setupFbLoginButton(fbLoginButton, new Callback(){
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                setAuthErrorText("");

                // check fb token retrieval response
                if(s != S.OK) {
                    setAuthErrorText(AppContext.getCurrent().getShell().getString(R.string.main_error_login_failed));
                    return;
                }

                // convert fb token to long term token
                AppContext.getCurrent().getShell().showLoadingScreen();
                String fbToken = (String)params.get("fbToken");
                authService.convertFbToken(fbToken, new Callback() {
                    @Override
                    public void execute(HashMap<String, Object> params, S s) {
                        if(s != S.OK ) {
                            setAuthErrorText(AppContext.getCurrent().getShell().getString(R.string.main_error_invalid_login));
                            AppContext.getCurrent().getShell().showContentScreen();
                        }else{
                            AppContext.getCurrent().getNavigationService().navigate(TasksView.class, null);
                        }
                    }
                });
            }
        });

        // show content
        AppContext.getCurrent().getShell().showContentScreen();
    }

    /**
     * Set auth error text
     * @param errorMsg error message to show
     */
    private void setAuthErrorText(String errorMsg){
        if(mErrorText == null){
            mErrorText = (TextView) findViewById(R.id.view_main_errortext);
        }
        mErrorText.setText(errorMsg);
    }
}
