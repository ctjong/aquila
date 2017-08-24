package com.planmaster.controls;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.planmaster.contexts.AppContext;
import com.planmaster.R;

/**
 * Drawer toggle button control
 */
public class DrawerToggle extends ActionBarDrawerToggle {
    public DrawerToggle(DrawerLayout drawerLayout, Toolbar toolbar) {
        super(AppContext.getCurrent().getActivity(), drawerLayout, toolbar, R.string.shell_drawer_open, R.string.shell_drawer_close);
    }

    public void onDrawerClosed(View view) {
        AppContext.getCurrent().getActivity().invalidateOptionsMenu();
    }

    public void onDrawerOpened(View drawerView) {
        AppContext.getCurrent().getActivity().invalidateOptionsMenu();
    }
}
