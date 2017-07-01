package com.projectaquila.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.Callback;
import com.projectaquila.models.CallbackParams;
import com.projectaquila.models.Event;
import com.projectaquila.views.MainView;

import java.util.HashMap;

public abstract class ShellActivity extends AppCompatActivity {
    private FrameLayout mContentScreen;
    private View mLoadingScreen;
    private View mErrorScreen;
    private Event mActivityResultEvent;
    private View mCurrentView;

    /**
     * To be executed before the activity is created
     */
    protected abstract void onBeforeCreate();

    /**
     * To be executed after the activity is created
     */
    protected abstract void onAfterCreate();

    /**
     * To be executed before loading a view
     */
    protected abstract void onBeforeViewLoad();

    /**
     * Invoked when the activity is created
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onBeforeCreate();

        // init layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shell);

        // init other member variables
        mErrorScreen = findViewById(R.id.shell_error);
        mLoadingScreen = findViewById(R.id.shell_loading);
        mContentScreen = (FrameLayout) findViewById(R.id.shell_content);
        mActivityResultEvent = new Event();

        // init error screen
        Button reloadBtn = (Button)findViewById(R.id.shell_error_reload_btn);
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.getCurrent().setChildActivity(null);
                AppContext.getCurrent().getNavigationService().navigate(MainView.class, null);
            }
        });

        onAfterCreate();
    }

    /**
     * Load a view with the specified layout id
     * @param layoutId layout ID
     */
    public void loadView(int layoutId){
        System.out.println("[ShellActivity.load] " + layoutId + "");
        onBeforeViewLoad();
        mActivityResultEvent.removeAllHandlers();
        LayoutInflater factory = getLayoutInflater();
        mCurrentView = factory.inflate(layoutId, null);
        mContentScreen.removeAllViews();
        mContentScreen.addView(mCurrentView);
    }

    /**
     * Return the current view
     * @return current view object
     */
    public View getCurrentView(){
        return mCurrentView;
    }

    /**
     * Invoked when menu items are created
     * @param menu menu object
     * @return true on success, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Show the content screen
     */
    public void showContentScreen(){
        System.out.println("[ShellActivity.showContentScreen]");
        mErrorScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);
        mContentScreen.setVisibility(View.VISIBLE);
    }

    /**
     * Show the loading screen
     */
    public void showLoadingScreen(){
        System.out.println("[ShellActivity.showLoadingScreen]");
        mErrorScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.VISIBLE);
        mContentScreen.setVisibility(View.GONE);
    }

    /**
     * Show the error screen
     * @param stringId id of the error msg string
     */
    public void showErrorScreen(int stringId){
        System.out.println("[ShellActivity.showErrorScreen]");
        TextView errorText = (TextView)findViewById(R.id.shell_error_text);
        errorText.setText(getString(stringId));
        mErrorScreen.setVisibility(View.VISIBLE);
        mLoadingScreen.setVisibility(View.GONE);
        mContentScreen.setVisibility(View.GONE);
    }
    /**
     * Bind a new handler to the activity result event
     */
    public void addActivityResultHandler(Callback cb){
        mActivityResultEvent.addHandler(cb);
    }

    /**
     * Set the text shown on the toolbar
     * @param stringId string id of the text to set
     */
    public void setToolbarText(int stringId){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(getString(stringId));
        }
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
        CallbackParams params = new CallbackParams();
        params.set("requestCode", requestCode);
        params.set("resultCode", resultCode);
        params.set("data", data);
        mActivityResultEvent.invoke(params);
    }
}
