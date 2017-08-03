package com.projectaquila.activities;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.Event;

public class ChildActivity extends ShellActivity {
    private Event mBackPressed;

    /**
     * Instantiate a new child activity
     */
    public ChildActivity(){
        mBackPressed = new Event();
    }

    /**
     * To be executed after the activity is created
     */
    @Override
    public void onAfterCreate() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.shell_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        AppContext.getCurrent().getNavigationService().onChildActivityLoad(this);
    }

    /**
     * Show the error screen
     * @param stringId id of the error msg string
     */
    @Override
    public void showErrorScreen(int stringId){
        AppContext.getCurrent().getNavigationService().goToMainActivity();
        AppContext.getCurrent().getActivity().showErrorScreen(stringId);
    }

    /**
     * Add a new handler for the back pressed event
     * @param handler event handler
     */
    public void addBackPressedHandler(Callback handler){
        mBackPressed.addHandler(handler);
    }

    /**
     * To be executed when the back button is pressed
     */
    @Override
    public void onBackPressed(){
        if(mLoadingScreen.getVisibility() == View.VISIBLE) return;
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_out_left);
        mBackPressed.invoke(null);
    }
}
