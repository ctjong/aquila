package com.planmaster.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.planmaster.contexts.AppContext;
import com.planmaster.R;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.common.Event;
import com.planmaster.views.LoginView;

public abstract class ShellActivity extends AppCompatActivity {
    private static final String AdMobAppId = "ca-app-pub-9097042281850784~3162685089";

    protected View mLoadingScreen;
    private FrameLayout mContentScreen;
    private View mErrorScreen;
    private Event mActivityResultEvent;
    private View mCurrentView;
    protected Toolbar mToolbar;

    /**
     * Get the toolbar icon resource id
     */
    public abstract int getToolbarIconResId();

    /**
     * To be executed before the activity is created
     */
    public void onBeforeCreate() { }

    /**
     * To be executed after the activity is created
     */
    public void onAfterCreate() { }

    /**
     * To be executed when the view changed on this activity
     */
    public void onViewChange() { }

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

        // init admob
        MobileAds.initialize(this, AdMobAppId);

        // init error screen
        Button reloadBtn = (Button)findViewById(R.id.shell_error_reload_btn);
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.getCurrent().getNavigationService().goToMainActivity();
                AppContext.getCurrent().getNavigationService().navigate(LoginView.class, null);
            }
        });

        // init toolbar
        mToolbar = (Toolbar) findViewById(R.id.shell_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(getToolbarIconResId());

        onAfterCreate();
    }

    /**
     * Load a view with the specified layout id
     * @param layoutId layout ID
     */
    public void loadView(int layoutId){
        System.out.println("[ShellActivity.loadView] changing view on " + this.getLocalClassName());
        onViewChange();
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
    public void hideLoadingScreen(){
        System.out.println("[ShellActivity.hideLoadingScreen]");
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
     * Show/hide the icon on the toolbar
     * @param show true to show the icon, false to hide
     */
    public void toggleToolbarIcon(boolean show){
        if(show) {
            mToolbar.setNavigationIcon(getToolbarIconResId());
        }else{
            mToolbar.setNavigationIcon(null);
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
