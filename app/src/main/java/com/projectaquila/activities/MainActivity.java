package com.projectaquila.activities;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.controls.DrawerItemClickListener;
import com.projectaquila.controls.DrawerToggle;
import com.projectaquila.models.Callback;
import com.projectaquila.models.DrawerItem;
import com.projectaquila.models.S;
import com.projectaquila.views.MainView;
import com.projectaquila.views.TasksView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ShellActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawer;
    private ArrayAdapter<String> mDrawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * To be executed before the activity is created
     */
    @Override
    protected void onBeforeCreate() {
        AppContext.initialize(this.getApplicationContext());
        AppContext.getCurrent().setMainActivity(this);
    }

    /**
     * To be executed after the activity is created
     */
    @Override
    protected void onAfterCreate() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.shell_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);

        // init drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.shell);
        mDrawer = (ListView) findViewById(R.id.shell_drawer);
        mDrawerToggle = new DrawerToggle(mDrawerLayout, toolbar);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        setupDrawer();
        AppContext.getCurrent().getAuthService().addAuthStateChangeHandler(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                setupDrawer();
            }
        });

        showLoadingScreen();
        AppContext.getCurrent().getNavigationService().navigate(MainView.class, null);
    }

    /**
     * To be executed before loading a view
     */
    @Override
    protected void onBeforeViewLoad() {
        mDrawerLayout.closeDrawers();
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
