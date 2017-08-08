package com.projectaquila.activities;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.controls.DrawerToggle;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.DrawerItem;
import com.projectaquila.common.PlanCollectionType;
import com.projectaquila.services.HelperService;
import com.projectaquila.views.LoginView;
import com.projectaquila.views.PlanCollectionView;
import com.projectaquila.views.TaskCollectionView;

public class MainActivity extends ShellActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawer;
    private ArrayAdapter<DrawerItem> mDrawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

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
            public void execute(CallbackParams params) {
                setupDrawer();
            }
        });

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

    /**
     * Setup the menu items on the drawer
     */
    private void setupDrawer() {
        if(mDrawerAdapter == null){
            mDrawerAdapter = new ArrayAdapter<>(this, R.layout.control_draweritem);
            mDrawer.setAdapter(mDrawerAdapter);
        }
        mDrawerAdapter.clear();
        if(AppContext.getCurrent().getActiveUser() != null){
            String userHello = getString(R.string.menu_userhello).replace("{name}", AppContext.getCurrent().getActiveUser().getFirstName());
            mDrawerAdapter.add(new DrawerItem(userHello, null, false, null));
            mDrawerAdapter.add(new DrawerItem(getString(R.string.menu_tasks), TaskCollectionView.class, false, null));
            mDrawerAdapter.add(new DrawerItem(getString(R.string.menu_enrolled_plans), PlanCollectionView.class, false,
                    HelperService.getSinglePairMap("mode", PlanCollectionType.ENROLLED)));
            mDrawerAdapter.add(new DrawerItem(getString(R.string.menu_browse_plans), PlanCollectionView.class, false,
                    HelperService.getSinglePairMap("mode", PlanCollectionType.BROWSE)));
            mDrawerAdapter.add(new DrawerItem(getString(R.string.menu_created_plans), PlanCollectionView.class, false,
                    HelperService.getSinglePairMap("mode", PlanCollectionType.CREATED)));
            mDrawerAdapter.add(new DrawerItem(getString(R.string.menu_logout), LoginView.class, true, null));
        }else{
            mDrawerAdapter.add(new DrawerItem(getString(R.string.menu_login), LoginView.class, false, null));
        }
        mDrawerAdapter.notifyDataSetChanged();
        mDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DrawerItem item = mDrawerAdapter.getItem(position);
                if(item != null){
                    item.invoke();
                }
            }
        });
    }
}
