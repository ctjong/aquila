package com.projectaquila.services;

import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.projectaquila.models.Callback;
import com.projectaquila.AppContext;
import com.projectaquila.models.ApiTaskMethod;

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
     * @param callback callback function to execute, with a key-value pair params passed in to it.
     */
    public void checkLoginStatus(Callback callback){
        //TODO
        HashMap<String, Object> params = new HashMap<>();
        params.put("isLoggedIn", false);
        callback.execute(params);
    }

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
                String fbToken = loginResult.getAccessToken().getToken();
                HashMap<String, Object> callbackParams = new HashMap<>();
                callbackParams.put("status", "success");
                callbackParams.put("fbToken", fbToken);
                callback.execute(callbackParams);
            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();
                System.err.println("[AuthService.setupFbLoginButton] FB token request error");
                HashMap<String, Object> callbackParams = new HashMap<>();
                callbackParams.put("status", "error");
                callback.execute(callbackParams);
            }

            @Override
            public void onError(FacebookException error) {
                LoginManager.getInstance().logOut();
                System.err.println("[AuthService.setupFbLoginButton] FB token request error");
                HashMap<String, Object> callbackParams = new HashMap<>();
                callbackParams.put("status", "error");
                callback.execute(callbackParams);
            }
        });
    }

    /**
     * Convert the given Facebook token to Orion token
     * @param fbToken Facebook token to convert
     * @param callback callback function to execute, with a key-value pair params passed in to it.
     */
    public void convertFbToken(String fbToken, final Callback callback){
        String apiUrl = AppContext.current.getApiBase() + "/auth/token/fb";
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("fbtoken", fbToken);
        AppContext.current.getDataService().request(ApiTaskMethod.POST, apiUrl, apiParams, new Callback() {
            @Override
            public void execute(HashMap<String, Object> params) {
                HashMap<String, Object> callbackParams = new HashMap<>();
                if(params == null){
                    LoginManager.getInstance().logOut();
                    callbackParams.put("status", "error");
                }else {
                    String token = (String) params.get("token");
                    AppContext.current.setAccessToken(token);
                    callbackParams.put("status", "success");
                    callbackParams.put("token", token);
                }
                callback.execute(callbackParams);
            }
        });
    }
}
