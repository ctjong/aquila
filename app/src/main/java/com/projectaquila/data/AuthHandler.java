package com.projectaquila.data;

import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.projectaquila.common.Callback;
import com.projectaquila.common.ShellActivity;
import com.projectaquila.context.AppContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Handler for all auth related operations
 */
public class AuthHandler {
    private CallbackManager mFbCallbackManager;

    /**
     * Instantiate new auth handler
     */
    public AuthHandler(){
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

    /**
     * Setup the ActivityResult event handler on the given activity for Facebook auth
     * @param parentActivity activity where the event will fire from
     */
    public void setupFbActivityResultEvent(ShellActivity parentActivity){
        List<Callback> activityResultEventHandlers = parentActivity.getEventHandlers("activityResult");
        activityResultEventHandlers.add(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params) {
                int requestCode = (int)params.get("requestCode");
                int resultCode = (int)params.get("resultCode");
                Intent data = (Intent)params.get("data");
                mFbCallbackManager.onActivityResult(requestCode, resultCode, data);
            }
        });
    }

    /**
     * Setup the given Facebook login button
     * @param parentActivity activity where the login button is in
     * @param fbLoginButton facebook login button
     * @param callback callback to execute when the login completes, with a key-value pair params passed in to it.
     */
    public void setupFbLoginButton(ShellActivity parentActivity, LoginButton fbLoginButton, final Callback callback){
        fbLoginButton.setReadPermissions(Arrays.asList("email"));
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
                System.err.println("FB token request error");
                HashMap<String, Object> callbackParams = new HashMap<>();
                callbackParams.put("status", "error");
                callback.execute(callbackParams);
            }

            @Override
            public void onError(FacebookException error) {
                LoginManager.getInstance().logOut();
                System.err.println("FB token request error");
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
        ApiPostTask.execute(apiUrl, apiParams, new Callback() {
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
                    callbackParams.put("token", "token");
                }
                callback.execute(callbackParams);
            }
        });
    }
}
