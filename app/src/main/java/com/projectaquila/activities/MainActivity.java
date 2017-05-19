package com.projectaquila.activities;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.projectaquila.R;
import com.projectaquila.common.ShellActivity;
import com.projectaquila.context.AppContext;
import com.projectaquila.common.Callback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends ShellActivity {
    private enum MainPageVisualState {LOADING, LOADED, ERROR};

    private ProgressBar mProgressBar;
    private TextView mErrorText;
    private LinearLayout mContentView;

    @Override
    protected int getLayoutId() {
        return R.layout.page_main;
    }

    @Override
    protected void initializeView(){
        mProgressBar = (ProgressBar) findViewById(R.id.page_main_loading);
        mErrorText = (TextView) findViewById(R.id.page_main_errortext);
        mContentView = (LinearLayout) findViewById(R.id.page_main_content);
        setVisualState(MainPageVisualState.LOADING);

        AppContext.current.getAuthHandler().checkLoginStatus(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params) {
                boolean isLoggedIn = (boolean)params.get("isLoggedIn");
                if(isLoggedIn){
                    //TODO navigate to tasks list activity
                    return;
                }
                LoginButton fbLoginButton = (LoginButton) findViewById(R.id.page_main_loginbutton);
                AppContext.current.getAuthHandler().setupFacebookLogin(_this, fbLoginButton, new Callback(){
                    @Override
                    public void execute(HashMap<String, Object> params) {
                        if(AppContext.current.getAccessToken() != null) {
                            //TODO show login error message
                        }else{
                            //TODO navigate to tasks list activity
                        }
                    }
                });
                setVisualState(MainPageVisualState.LOADED);
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

    private void render(JSONObject data){
        if(data == null){
            setVisualState(MainPageVisualState.ERROR);
            return;
        }
    }

    private void setVisualState(MainPageVisualState visualState){
        mErrorText.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mContentView.setVisibility(View.GONE);
        switch(visualState){
            case LOADING:
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case LOADED:
                mContentView.setVisibility(View.VISIBLE);
                break;
            default:
                mErrorText.setVisibility(View.VISIBLE);
                break;
        }
    }
}
