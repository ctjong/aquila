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
        Toolbar toolbar = (Toolbar) findViewById(R.id.shell_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
     * Called when the back button is invoked
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        AppContext.getCurrent().setChildActivity(null);
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_out_left);
    }

    /**
     * Show the error screen
     * @param stringId id of the error msg string
     */
    @Override
    public void showErrorScreen(int stringId){
        onBackPressed();
        AppContext.getCurrent().getActivity().showErrorScreen(stringId);
    }
}
