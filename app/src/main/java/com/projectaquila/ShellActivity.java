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

import java.util.Iterator;
import java.util.Map;

public abstract class ShellActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mCurentView;

    protected abstract int getLayoutId();

    protected abstract void initializeView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shell);


        // init toolbar
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        if(mToolBar == null) return;
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_drawer);

        // init drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = (ListView) findViewById(R.id.drawer);
        mDrawer.setAdapter(new ArrayAdapter<>(this, R.layout.widget_drawer, new String[] {"main view", "test view"}));
        mDrawer.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new CustomDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        // init content frame
        FrameLayout contentFrame = (FrameLayout) findViewById(R.id.content_frame);
        if(contentFrame == null){
            throw new IllegalStateException("failed to retrieve content frame");
        }
        LayoutInflater factory = getLayoutInflater();
        int layoutId = getLayoutId();
        mCurentView = factory.inflate(layoutId, null);
        contentFrame.removeAllViews();
        contentFrame.addView(mCurentView);

        // init view
        initializeView();
    }

    @Override
    public View findViewById(int id){
        if(mCurentView == null){
            return super.findViewById(id);
        }
        return mCurentView.findViewById(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);
        MenuItem menuItem = menu.findItem(R.id.content_frame);
        if(menuItem != null) menuItem.setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void navigate(Class newActivity, Map<String, String> parameters){
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
        startActivity(intent);
    }

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
