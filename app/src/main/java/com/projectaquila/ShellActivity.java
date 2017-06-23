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
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.projectaquila.controls.DrawerItemClickListener;
import com.projectaquila.controls.DrawerToggle;
import com.projectaquila.models.Callback;
import com.projectaquila.models.DrawerItem;
import com.projectaquila.models.Event;
import com.projectaquila.models.S;
import com.projectaquila.views.MainView;
import com.projectaquila.views.TasksView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class for all activities in this project
 */
public class ShellActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout mContentScreen;
    private View mCurrentView;
    private View mLoadingScreen;
    private View mErrorScreen;
    private ArrayAdapter<String> mDrawerAdapter;
    private Event mActivityResultEvent;

    /**
     * Invoked when the activity is created
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init app context
        AppContext.initialize(this.getApplicationContext());
        AppContext.getCurrent().setShell(this);

        // init layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shell);

        // init toolbar
        Toolbar toolBar = (Toolbar) findViewById(R.id.shell_toolbar);
        if(toolBar == null) return;
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_drawer);

        // init drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.shell);
        mDrawer = (ListView) findViewById(R.id.shell_drawer);
        mDrawerToggle = new DrawerToggle(mDrawerLayout, toolBar);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        setupDrawer();
        AppContext.getCurrent().getAuthService().addAuthStateChangeHandler(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                setupDrawer();
            }
        });

        // init other member variables
        mErrorScreen = findViewById(R.id.shell_error);
        mLoadingScreen = findViewById(R.id.shell_loading);
        mContentScreen = (FrameLayout) findViewById(R.id.shell_content);
        mActivityResultEvent = new Event();

        // navigate to main
        showLoadingScreen();
        AppContext.getCurrent().getNavigationService().navigate(MainView.class, null);
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
     * Setup the menu items on the drawer
     */
    private void setupDrawer() {
        List<DrawerItem> drawerItems = new ArrayList<>();
        if(AppContext.getCurrent().getAuthService().isUserLoggedIn()){
            drawerItems.add(new DrawerItem(getString(R.string.menu_tasks), TasksView.class, false));
            drawerItems.add(new DrawerItem(getString(R.string.menu_logout), MainView.class, true));
        }else{
            drawerItems.add(new DrawerItem(getString(R.string.menu_login), MainView.class, false));
        }
        if(mDrawerAdapter == null){
            mDrawerAdapter = new ArrayAdapter<>(this, R.layout.control_draweritem);
            mDrawer.setAdapter(mDrawerAdapter);
        }
        mDrawerAdapter.clear();
        List<Callback> handlers = new ArrayList<>();
        for(int i=0; i<drawerItems.size(); i++){
            DrawerItem item = drawerItems.get(i);
            mDrawerAdapter.add(item.getTitle());
            handlers.add(item.getHandler());
        }
        mDrawerAdapter.notifyDataSetChanged();
        mDrawer.setOnItemClickListener(new DrawerItemClickListener(handlers));
    }
}
