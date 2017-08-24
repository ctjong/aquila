package com.planmaster.services;

import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.planmaster.common.Callback;
import com.planmaster.contexts.AppContext;
import com.planmaster.common.ApiTaskMethod;
import com.planmaster.common.CallbackParams;
import com.planmaster.common.Event;
import com.planmaster.datamodels.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;

/**
 * Handler for all auth related operations
 */
public class AuthService {
    private CallbackManager mFbCallbackManager;
    private Event mAuthStateChange;

    /**
     * Instantiate new auth handler
     */
    public AuthService(){
        mFbCallbackManager = CallbackManager.Factory.create();
        mAuthStateChange = new Event();
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
                String fbToken = loginResult.getAccessToken().getToken();
                callback.execute(new CallbackParams("fbToken", fbToken));
            }

            @Override
            public void onCancel() {
                logOut();
                HelperService.logError("[AuthService.setupFbLoginButton] FB login cancelled");
                callback.execute(null);
            }

            @Override
            public void onError(FacebookException error) {
                logOut();
                HelperService.logError("[AuthService.setupFbLoginButton] FB token request error");
                callback.execute(null);
            }
        });
    }

    /**
     * Convert the given Facebook token to Orion token
     * @param fbToken Facebook token to convert
     * @param callback callback function to execute, with a key-value pair params passed in to it.
     */
    public void convertFbToken(String fbToken, final Callback callback){
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("fbtoken", fbToken);
        AppContext.getCurrent().getDataService().request(ApiTaskMethod.POST, "/auth/token/fb", apiParams, new Callback() {
            @Override
            public void execute(final CallbackParams params) {
                JSONObject userObj = params.getApiResult().getObject();
                try {
                    User user = new User(userObj.getString("id"), userObj.getString("firstname"),
                            userObj.getString("lastname"), userObj.getString("token"));
                    AppContext.getCurrent().setActiveUser(user);
                    mAuthStateChange.invoke(null);
                    // pass back an empty callback params, as a sign that the conversion succeeded
                    callback.execute(new CallbackParams());
                } catch (JSONException e) {
                    HelperService.logError("[AuthService.convertFbToken] exception " + e.getMessage());
                    e.printStackTrace();
                    logOut();
                    callback.execute(null);
                }
            }
        });
    }

    /**
     * Log out Facebook and Orion
     */
    public void logOut(){
        LoginManager.getInstance().logOut();
        AppContext.getCurrent().setActiveUser(null);
        mAuthStateChange.invoke(null);
    }

    /**
     * Add new handler to the auth state change event
     * @param handler event handler
     */
    public void addAuthStateChangeHandler(Callback handler){
        mAuthStateChange.addHandler(handler);
    }
}
