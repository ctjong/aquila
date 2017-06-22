package com.projectaquila;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.projectaquila.models.Callback;
import com.projectaquila.models.Event;
import com.projectaquila.views.MainView;
import com.projectaquila.views.TestView;

import java.util.HashMap;

/**
 * Base class for all activities in this project
 */
public class ShellActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout mContentScreen;
    private View mCurrentView;
    private View mLoadingScreen;
    private View mErrorScreen;

    private Event mActivityResultEvent;

    /**
     * Invoked when the activity is created
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init app context if it hasn't been initialized
        if(AppContext.current == null){
            AppContext.initialize(this.getApplicationContext());
        }
        AppContext.current.setShell(this);

        // init layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shell);

        // init toolbar
        mToolBar = (Toolbar) findViewById(R.id.shell_toolbar);
        if(mToolBar == null) return;
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_drawer);

        // init drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.shell);
        mDrawer = (ListView) findViewById(R.id.shell_drawer);
        mDrawer.setAdapter(new ArrayAdapter<>(this, R.layout.control_draweritem, new String[] {"main view", "test view"}));
        mDrawer.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new CustomDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        // init other member variables
        mErrorScreen = findViewById(R.id.shell_error);
        mLoadingScreen = findViewById(R.id.shell_loading);
        mContentScreen = (FrameLayout) findViewById(R.id.shell_content);
        mActivityResultEvent = new Event();

        // navigate to main
        showLoadingScreen();
        AppContext.current.getNavigationService().navigate(new MainView(), null);
    }

    /**
     * Load layout with the specified id
     * @param layoutId layout ID
     */
    public void load(int layoutId){
        System.out.println("[ShellActivity.load] " + layoutId + "");
        mDrawerLayout.closeDrawers();
        mActivityResultEvent.removeAllHandlers();
        LayoutInflater factory = getLayoutInflater();
        mCurrentView = factory.inflate(layoutId, null);
        mContentScreen.removeAllViews();
        mContentScreen.addView(mCurrentView);
    }

    /**
     * Find view with the specified id
     * @param id view ID
     * @return view object
     */
    @Override
    public View findViewById(int id){
        if(mCurrentView == null){
            return super.findViewById(id);
        }
        return mCurrentView.findViewById(id);
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
     * Called whenever we call invalidateOptionsMenu()
     * @param menu menu object
     * @return true on success, false otherwise
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);
        MenuItem menuItem = menu.findItem(R.id.shell_content);
        if(menuItem != null) menuItem.setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Called when a menu item is selected
     * @param item selected menu item
     * @return true on success
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
     */
    public void showErrorScreen(){
        System.out.println("[ShellActivity.showErrorScreen]");
        mErrorScreen.setVisibility(View.VISIBLE);
        mLoadingScreen.setVisibility(View.GONE);
        mContentScreen.setVisibility(View.GONE);
    }

    /**
     * Get current view
     * @return current view object
     */
    public View getCurrentView(){
        return mCurrentView;
    }

    /**
     * Bind a new handler to the activity result event
     */
    public void addActivityResultHandler(Callback cb){
        mActivityResultEvent.addHandler(cb);
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
        HashMap<String, Object> params = new HashMap<>();
        params.put("requestCode", requestCode);
        params.put("resultCode", resultCode);
        params.put("data", data);
        mActivityResultEvent.invoke(params);
    }

    /**
     * Listener for drawer item click event
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position == 0){
                AppContext.current.getNavigationService().navigate(new MainView(), null);
            }else{
                AppContext.current.getNavigationService().navigate(new TestView(), null);
            }
        }
    }

    /**
     * Custom drawer toggle
     */
    private class CustomDrawerToggle extends ActionBarDrawerToggle{
        public CustomDrawerToggle() {
            super(ShellActivity.this, mDrawerLayout, mToolBar, R.string.drawer_open, R.string.drawer_close);
        }

        public void onDrawerClosed(View view) {
            invalidateOptionsMenu();
        }

        public void onDrawerOpened(View drawerView) {
            invalidateOptionsMenu();
        }
    }
}
