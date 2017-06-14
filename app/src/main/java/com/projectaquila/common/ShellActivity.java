package com.projectaquila.common;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.activities.MainActivity;
import com.projectaquila.activities.TestActivity;
import com.projectaquila.context.AppContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Base class for all activities in this project
 */
public abstract class ShellActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout mContentFrame;
    private View mCurentView;
    private View mLoadingView;
    private View mErrorView;
    private HashMap<String, List<Callback>> mEventHandlers;

    protected enum VisualState {LOADING, LOADED, ERROR};

    protected final ShellActivity _this = this;

    /**
     * Get layout ID
     * @return ID of the layout to use for the current activity
     */
    protected abstract int getLayoutId();

    /**
     * Initialize the view for the current activity
     */
    protected abstract void initializeView();

    /**
     * Invoked when the activity is created
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init app context if it hasn't been initialized
        AppContext.initialize(this.getApplicationContext());

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
        mDrawer.setAdapter(new ArrayAdapter<>(this, R.layout.view_draweritem, new String[] {"main view", "test view"}));
        mDrawer.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new CustomDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        // get error and loading UIs
        mErrorView = findViewById(R.id.shell_error);
        mLoadingView = findViewById(R.id.shell_loading);

        // init content frame
        mContentFrame = (FrameLayout) findViewById(R.id.shell_content);
        if(mContentFrame == null){
            throw new IllegalStateException("failed to retrieve content frame");
        }
        LayoutInflater factory = getLayoutInflater();
        int layoutId = getLayoutId();
        mCurentView = factory.inflate(layoutId, null);
        mContentFrame.removeAllViews();
        mContentFrame.addView(mCurentView);
        mEventHandlers = new HashMap<>();

        // init view
        setVisualState(VisualState.LOADING);
        initializeView();
    }

    /**
     * Find view with the specified id
     * @param id view ID
     * @return view object
     */
    @Override
    public View findViewById(int id){
        if(mCurentView == null){
            return super.findViewById(id);
        }
        return mCurentView.findViewById(id);
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
     * Get event handlers for the event with the specified name
     * @param eventName event name
     * @return a list of event handlers
     */
    public List<Callback> getEventHandlers(String eventName){
        List<Callback> list;
        if(!mEventHandlers.containsKey(eventName)){
            list = new ArrayList<Callback>();
            mEventHandlers.put(eventName, list);
        }else{
            list = mEventHandlers.get(eventName);
        }
        return list;
    }

    /**
     * Get page parameter with the given key
     * @param key parameter key
     * @return parameter value
     */
    public String getPageParameter(String key){
        return getIntent().getStringExtra(key);
    }

    /**
     * Get local setting with the given key
     * @param key local setting key
     * @return local settng value
     */
    public String getLocalSetting(String key){
        SharedPreferences settings = getPreferences(0);
        return settings.getString(key, null);
    }

    /**
     * Set a key value pair to local setting
     * @param key local setting key
     * @param value local setting value
     */
    public void setLocalSetting(String key, String value){
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString(key, value);
        settingsEditor.commit();
    }

    /**
     * Navigate to the specified activity
     * @param newActivity activity to navigate to
     * @param parameters navigation parameters
     */
    protected void navigate(Class newActivity, Map<String, String> parameters){
        Intent intent = new Intent(this, newActivity);
        if(parameters != null){
            Iterator it = parameters.entrySet().iterator();
            while (it.hasNext()) {
                Object pairRaw = it.next();
                if(!(pairRaw instanceof Map.Entry)) continue;
                Map.Entry pair = (Map.Entry) it.next();
                Object key = pair.getKey();
                Object value = pair.getValue();
                if(!(key instanceof String) || !(value instanceof String)) continue;
                intent.putExtra((String) key, (String) value);
            }
        }
        mDrawerLayout.closeDrawers();
        startActivity(intent);
        finish();
    }

    /**
     * Set UI state to the given visual state
     * @param visualState new visual state
     */
    protected void setVisualState(VisualState visualState){
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mContentFrame.setVisibility(View.GONE);
        System.out.println("setVisualState " + visualState.name());
        switch(visualState){
            case LOADING:
                mLoadingView.setVisibility(View.VISIBLE);
                break;
            case LOADED:
                mContentFrame.setVisibility(View.VISIBLE);
                break;
            case ERROR:
            default:
                mErrorView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Listener for drawer item click event
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position == 0){
                navigate(MainActivity.class, null);
            }else{
                navigate(TestActivity.class, null);
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
