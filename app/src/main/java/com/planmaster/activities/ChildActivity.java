package com.planmaster.activities;

import android.view.View;

import com.planmaster.contexts.AppContext;
import com.planmaster.R;
import com.planmaster.common.Callback;
import com.planmaster.common.Event;

public class ChildActivity extends ShellActivity {
    private Event mBackPressed;

    /**
     * Instantiate a new child activity
     */
    public ChildActivity(){
        mBackPressed = new Event();
    }

    /**
     * Get the toolbar icon resource id
     */
    @Override
    public int getToolbarIconResId(){
        return android.R.drawable.ic_menu_close_clear_cancel;
    }

    /**
     * To be executed after the activity is created
     */
    @Override
    public void onAfterCreate() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_out_right);
        mBackPressed.invoke(null);
    }
}
