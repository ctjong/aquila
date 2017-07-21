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
import com.projectaquila.models.CallbackParams;
import com.projectaquila.models.Event;
import com.projectaquila.models.User;

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
                callback.execute(new CallbackParams("fbToken", fbToken));
            }

            @Override
            public void onCancel() {
                logOut();
                System.err.println("[AuthService.setupFbLoginButton] FB login cancelled");
                callback.execute(null);
            }

            @Override
            public void onError(FacebookException error) {
                logOut();
                System.err.println("[AuthService.setupFbLoginButton] FB token request error");
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
            public void execute(CallbackParams params) {
                try {
                    JSONObject userObj = params.getApiResult().getObject();
                    setAccessToken(userObj.getString("token"));
                    User activeUser = new User(userObj.getString("id"), userObj.getString("firstname"), userObj.getString("lastname"));
                    AppContext.getCurrent().setActiveUser(activeUser);
                    callback.execute(params);
                } catch (JSONException e) {
                    System.err.println("[AuthService.convertFbToken] exception");
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
        setAccessToken(null);
        AppContext.getCurrent().setActiveUser(null);
    }

    /**
     * Add new handler to the auth state change event
     * @param handler event handler
     */
    public void addAuthStateChangeHandler(Callback handler){
        mAuthStateChange.addHandler(handler);
    }

    /**
     * Get the current access token
     * @return access token, or null if no token is stored
     */
    public String getAccessToken(){
        SharedPreferences settings = AppContext.getCurrent().getLocalSettings();
        return settings.getString("token", null);
    }

    /**
     * Set current access token in the local setting
     * @param token access token
     */
    private void setAccessToken(String token){
        SharedPreferences settings = AppContext.getCurrent().getLocalSettings();
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString("token", token);
        settingsEditor.apply();
        mAuthStateChange.invoke(null);
    }
}
