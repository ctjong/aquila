package com.planmaster.activities;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.planmaster.contexts.AppContext;
import com.planmaster.R;
import com.planmaster.controls.DrawerToggle;
import com.planmaster.drawer.DrawerAdapter;
import com.planmaster.views.LoginView;

public class MainActivity extends ShellActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * Get the toolbar icon resource id
     */
    @Override
    public int getToolbarIconResId(){
        return R.drawable.ic_drawer;
    }

    /**
     * To be executed before the activity is created
     */
    @Override
    public void onBeforeCreate() {
        AppContext.initialize(this);
    }

    /**
     * To be executed after the activity is created
     */
    @Override
    public void onAfterCreate() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.shell_drawerlayout);
        mDrawer = (ListView) findViewById(R.id.shell_drawer);
        mDrawerToggle = new DrawerToggle(mDrawerLayout, mToolbar);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawer.setAdapter(new DrawerAdapter());

        AppContext.getCurrent().getNavigationService().navigate(LoginView.class, null);
    }

    /**
     * To be executed before loading a view
     */
    @Override
    public void onViewChange() {
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
}
