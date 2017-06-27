package com.projectaquila.activities;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.projectaquila.AppContext;
import com.projectaquila.R;

public class ChildActivity extends ShellActivity {
    /**
     * To be executed before the activity is created
     */
    @Override
    protected void onBeforeCreate() {
        AppContext.getCurrent().setChildActivity(this);
    }

    /**
     * To be executed after the activity is created
     */
    @Override
    protected void onAfterCreate() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.shell_toolbar);
        if(toolBar == null) return;
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        AppContext.getCurrent().getNavigationService().reloadView();
    }

    /**
     * To be executed before loading a view
     */
    @Override
    protected void onBeforeViewLoad() {
    }

    /**
     * Called when the activity is destroyed
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        AppContext.getCurrent().setChildActivity(null);
    }
}
