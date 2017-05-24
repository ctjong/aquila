package com.projectaquila.activities;

import android.content.Intent;

import com.facebook.login.widget.LoginButton;
import com.projectaquila.R;
import com.projectaquila.common.ShellActivity;
import com.projectaquila.context.AppContext;
import com.projectaquila.common.Callback;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends ShellActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.page_main;
    }

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
                AppContext.current.getAuthHandler().setupFacebookLogin(_this, fbLoginButton, new Callback(){
                    @Override
                    public void execute(HashMap<String, Object> params) {
                        if(params.get("status") == "tokenConversionError"){
                            //TODO display login error
                        } else if(AppContext.current.getAccessToken() != null) {
                            setVisualState(VisualState.LOADED);
                            //TODO display login error
                        }else{
                            navigate(TasksActivity.class, null);
                        }
                    }
                });
                setVisualState(VisualState.LOADED);
            }
        });
    }

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
