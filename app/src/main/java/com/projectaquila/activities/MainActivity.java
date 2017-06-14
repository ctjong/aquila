package com.projectaquila.activities;

import android.content.Intent;

import com.facebook.login.widget.LoginButton;
import com.projectaquila.R;
import com.projectaquila.common.ShellActivity;
import com.projectaquila.context.AppContext;
import com.projectaquila.common.Callback;
import com.projectaquila.data.AuthHandler;

import java.util.HashMap;
import java.util.List;

/**
 * Main activity
 */
public class MainActivity extends ShellActivity {

    /**
     * Get layout ID
     * @return ID of the layout to use for the current activity
     */
    @Override
    protected int getLayoutId() {
        return R.layout.page_main;
    }

    /**
     * Initialize the view for the current activity
     */
    @Override
    protected void initializeView(){
        AppContext.current.getAuthHandler().checkLoginStatus(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params) {
                boolean isLoggedIn = (boolean)params.get("isLoggedIn");
                if(isLoggedIn){
                    navigate(TasksActivity.class, null);
                    return;
                }
                LoginButton fbLoginButton = (LoginButton) findViewById(R.id.page_main_loginbutton);
                final AuthHandler authHandler = AppContext.current.getAuthHandler();
                authHandler.setupFbActivityResultEvent(_this);
                authHandler.setupFbLoginButton(_this, fbLoginButton, new Callback(){
                    @Override
                    public void execute(HashMap<String, Object> params) {
                        // check fb token retrieval response
                        String fbToken = (String)params.get("fbToken");
                        if(params.get("status") == "error" || fbToken == null || AppContext.current.getAccessToken() != null) {
                            setVisualState(VisualState.LOADED);
                            //TODO display login error
                            return;
                        }

                        // set state to loading and try to convert fb token
                        // navigate to tasks view on success
                        setVisualState(VisualState.LOADING);
                        authHandler.convertFbToken(fbToken, new Callback() {
                            @Override
                            public void execute(HashMap<String, Object> params) {
                                String token = (String)params.get("token");
                                if(params.get("status") == "error" || token == null) {
                                    //TODO display login error
                                    setVisualState(VisualState.LOADED);
                                    return;
                                }

                                setLocalSetting("token", token);
                                navigate(TasksActivity.class, null);
                            }
                        });
                    }
                });
                setVisualState(VisualState.LOADED);
            }
        });
    }

    /**
     * Invoked on activity result event
     * @param requestCode request code
     * @param resultCode result code
     * @param data event data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Callback> eventHandlers = getEventHandlers("activityResult");
        for(int i=0; i<eventHandlers.size(); i++){
            HashMap<String, Object> params = new HashMap<>();
            params.put("requestCode", requestCode);
            params.put("resultCode", resultCode);
            params.put("data", data);
            eventHandlers.get(i).execute(params);
        }
    }
}
