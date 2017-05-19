package com.projectaquila.data;

import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.projectaquila.common.Callback;
import com.projectaquila.common.ShellActivity;
import com.projectaquila.context.AppContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AuthHandler {
    public void checkLoginStatus(Callback callback){
        //TODO
        HashMap<String, Object> params = new HashMap<>();
        params.put("isLoggedIn", false);
        callback.execute(params);
    }

    public void setupFacebookLogin(ShellActivity parentActivity, LoginButton fbLoginButton, Callback nextCallback){
        final CallbackManager callbackManager = CallbackManager.Factory.create();
        List<Callback> activityResultEventHandlers = parentActivity.getEventHandlers("activityResult");
        activityResultEventHandlers.add(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params) {
                int requestCode = (int)params.get("requestCode");
                int resultCode = (int)params.get("resultCode");
                Intent data = (Intent)params.get("data");
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        });
        fbLoginButton.setReadPermissions(Arrays.asList("email"));
        fbLoginButton.registerCallback(callbackManager, new FacebookLoginCallback<LoginResult>(parentActivity, nextCallback));
    }

    private class FacebookLoginCallback<T> implements FacebookCallback<T> {
        private ShellActivity mParentActivity;
        private Callback mNextCallback;

        public FacebookLoginCallback(ShellActivity parentActivity, Callback nextCallback){
            mParentActivity = parentActivity;
            mNextCallback = nextCallback;
        }

        @Override
        public void onSuccess(T loginResultObj) {
            if(!(loginResultObj instanceof LoginResult)) return;
            LoginResult loginResult = (LoginResult) loginResultObj;
            String fbToken = loginResult.getAccessToken().getToken();
            String apiUrl = AppContext.current.getApiBase() + "/auth/token/fb";
            HashMap<String, String> apiParams = new HashMap<>();
            apiParams.put("fbtoken", fbToken);
            ApiPostTask task = new ApiPostTask(apiUrl, apiParams, new Callback() {
                @Override
                public void execute(HashMap<String, Object> params) {
                    String token = (String)params.get("token");
                    AppContext.current.setAccessToken(token);
                    mParentActivity.setLocalSetting("token", token);
                    mNextCallback.execute(null);
                }
            });
            task.execute();
        }

        @Override
        public void onCancel() {
            System.err.println("FB login cancelled");
        }

        @Override
        public void onError(FacebookException exception) {
            System.err.println("FB login error");
        }
    }
}
