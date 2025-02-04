package com.planmaster.views;

import android.content.Intent;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.planmaster.R;
import com.planmaster.contexts.AppContext;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.services.AuthService;

/**
 * Login view
 */
public class LoginView extends ViewBase {
    private TextView mErrorText;

    /**
     * Get layout ID
     * @return ID of the layout to use for the current activity
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_login;
    }

    /**
     * Get the string ID to be shown on the title bar
     * @return string ID
     */
    @Override
    protected int getTitleBarStringId() {
        return R.string.app_name;
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
                System.out.println("[LoginView.initializeView] activity result event received [" + requestCode + "," + resultCode + "]");
                authService.onFbActivityResult(requestCode, resultCode, data);
            }
        });

        // check if logged in on launch
        if(AppContext.getCurrent().getActiveUser() != null){
            AppContext.getCurrent().getNavigationService().navigate(TaskCollectionView.class, null);
            mViewLoadAborted = true;
            return;
        }

        // clear any existing login session and setup login button
        if(AppContext.getCurrent().getActiveUser() != null) {
            AppContext.getCurrent().getAuthService().logOut();
        }
        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.view_login_loginbutton);
        authService.setupFbLoginButton(fbLoginButton, new Callback(){
            @Override
            public void execute(CallbackParams params) {
                setAuthErrorText("");

                // check fb token retrieval response
                if(params == null) {
                    System.out.println("[LoginView.setupFbLoginButton] login response error received from FB.");
                    setAuthErrorText(AppContext.getCurrent().getActivity().getString(R.string.login_error_login_failed));
                    return;
                }

                // convert fb token to long term token
                System.out.println("[LoginView.setupFbLoginButton] login response received from FB. converting it.");
                AppContext.getCurrent().getActivity().showLoadingScreen();
                String fbToken = (String)params.get("fbToken");
                authService.convertFbToken(fbToken, new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        if(params == null) {
                            System.out.println("[LoginView.setupFbLoginButton] failed to convert FB token. null response params.");
                            setAuthErrorText(AppContext.getCurrent().getActivity().getString(R.string.login_error_invalid_login));
                            AppContext.getCurrent().getActivity().hideLoadingScreen();
                        }else{
                            System.out.println("[LoginView.setupFbLoginButton] FB token conversion successful.");
                            AppContext.getCurrent().getNavigationService().navigate(TaskCollectionView.class, null);
                        }
                    }
                });
            }
        });
    }

    /**
     * Set auth error text
     * @param errorMsg error message to show
     */
    private void setAuthErrorText(String errorMsg){
        if(mErrorText == null){
            mErrorText = (TextView) findViewById(R.id.view_login_errortext);
        }
        mErrorText.setText(errorMsg);
    }
}
