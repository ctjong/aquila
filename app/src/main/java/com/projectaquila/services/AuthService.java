package com.projectaquila.services;

import android.content.Intent;
import android.content.SharedPreferences;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.projectaquila.models.Callback;
import com.projectaquila.AppContext;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.S;

import java.util.Collections;
import java.util.HashMap;

/**
 * Handler for all auth related operations
 */
public class AuthService {
    private CallbackManager mFbCallbackManager;

    /**
     * Instantiate new auth handler
     */
    public AuthService(){
        mFbCallbackManager = CallbackManager.Factory.create();
    }

    /**
     * Check login status
     * @return true if logged in, false otherwise
     */
    public boolean isUserLoggedIn(){
        String token = getAccessToken();
        return token != null;
    }

    /**
     * Invoked on FB activity result event
     * @param requestCode request code
     * @param resultCode result code
     * @param data intent data
     */
    public void onFbActivityResult(int requestCode, int resultCode, Intent data){
        mFbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Setup the given Facebook login button
     * @param fbLoginButton facebook login button
     * @param callback callback to execute when the login completes, with a key-value pair params passed in to it.
     */
    public void setupFbLoginButton(LoginButton fbLoginButton, final Callback callback){
        fbLoginButton.setReadPermissions(Collections.singletonList("email"));
        fbLoginButton.registerCallback(mFbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setAccessToken(null);
                String fbToken = loginResult.getAccessToken().getToken();
                HashMap<String, Object> callbackParams = new HashMap<>();
                callbackParams.put("fbToken", fbToken);
                callback.execute(callbackParams, S.OK);
            }

            @Override
            public void onCancel() {
                logOut();
                System.err.println("[AuthService.setupFbLoginButton] FB token request error");
                callback.execute(null, S.UnknownError);
            }

            @Override
            public void onError(FacebookException error) {
                logOut();
                System.err.println("[AuthService.setupFbLoginButton] FB token request error");
                callback.execute(null, S.UnknownError);
            }
        });
    }

    /**
     * Convert the given Facebook token to Orion token
     * @param fbToken Facebook token to convert
     * @param callback callback function to execute, with a key-value pair params passed in to it.
     */
    public void convertFbToken(String fbToken, final Callback callback){
        String apiUrl = AppContext.getCurrent().getApiBase() + "/auth/token/fb";
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("fbtoken", fbToken);
        AppContext.getCurrent().getDataService().request(ApiTaskMethod.POST, apiUrl, apiParams, new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S S) {
                if(params == null || params.get("token") == null){
                    logOut();
                    callback.execute(null, S.UnknownError);
                }else {
                    setAccessToken((String) params.get("token"));
                    callback.execute(null, S.OK);
                }
            }
        });
    }

    /**
     * Log out Facebook and Orion
     */
    public void logOut(){
        LoginManager.getInstance().logOut();
        setAccessToken(null);
    }

    /**
     * Get the current access token
     * @return access token, or null if no token is stored
     */
    public String getAccessToken(){
        SharedPreferences settings = AppContext.getCurrent().getShell().getPreferences(0);
        return settings.getString("token", null);
}

    /**
     * Set current access token in the local setting
     * @param token access token
     */
    private void setAccessToken(String token){
        SharedPreferences settings = AppContext.getCurrent().getShell().getPreferences(0);
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString("token", token);
        settingsEditor.apply();
    }
}
