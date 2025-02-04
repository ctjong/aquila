package com.planmaster.activities;

import android.content.Intent;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.planmaster.contexts.AppContext;
import com.planmaster.R;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.common.Event;
import com.planmaster.views.LoginView;

public abstract class ShellActivity extends AppCompatActivity {
    private static final String AdMobAppId = "ca-app-pub-5059401263214266~4075331566";
    private static final String TestDeviceModel = "SM-G930F";

    protected View mLoadingScreen;
    private FrameLayout mContentScreen;
    private View mErrorScreen;
    private Event mActivityResultEvent;
    private AdView mAdView;
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

        // this is to resize the activity's height when keyboard is shown
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // init other member variables
        mErrorScreen = findViewById(R.id.shell_error);
        mLoadingScreen = findViewById(R.id.shell_loading);
        mContentScreen = (FrameLayout) findViewById(R.id.shell_content);
        mActivityResultEvent = new Event();

        // init ads
        if(!Build.MODEL.equals(TestDeviceModel)){
            MobileAds.initialize(this, AdMobAppId);
            mAdView = (AdView) findViewById(R.id.shell_adview_prod);
            mAdView.setVisibility(View.VISIBLE);
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.shell_drawerlayout);
            RelativeLayout.LayoutParams drawerLayoutParams = (RelativeLayout.LayoutParams)drawerLayout.getLayoutParams();
            drawerLayoutParams.setMargins(0, 0, 0, mAdView.getHeight());
        }

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
        if(mAdView != null) mAdView.loadAd(new AdRequest.Builder().build());
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
