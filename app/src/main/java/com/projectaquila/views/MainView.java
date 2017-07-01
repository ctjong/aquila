package com.projectaquila.views;

import android.content.Intent;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.projectaquila.R;
import com.projectaquila.AppContext;
import com.projectaquila.models.Callback;
import com.projectaquila.models.CallbackParams;
import com.projectaquila.services.AuthService;

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
        AppContext.getCurrent().getActivity().addActivityResultHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
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
            public void execute(CallbackParams params) {
                setAuthErrorText("");

                // check fb token retrieval response
                if(params == null) {
                    setAuthErrorText(AppContext.getCurrent().getActivity().getString(R.string.main_error_login_failed));
                    return;
                }

                // convert fb token to long term token
                AppContext.getCurrent().getActivity().showLoadingScreen();
                String fbToken = (String)params.get("fbToken");
                authService.convertFbToken(fbToken, new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        if(params == null) {
                            setAuthErrorText(AppContext.getCurrent().getActivity().getString(R.string.main_error_invalid_login));
                            AppContext.getCurrent().getActivity().showContentScreen();
                        }else{
                            AppContext.getCurrent().getNavigationService().navigate(TasksView.class, null);
                        }
                    }
                });
            }
        });

        // show content
        AppContext.getCurrent().getActivity().showContentScreen();
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
