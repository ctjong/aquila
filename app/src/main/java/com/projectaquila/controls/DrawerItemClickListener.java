package com.projectaquila.controls;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;

import java.util.List;

/**
 * Listener for drawer item click event
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {
    private List<Callback> mHandlers;

    public DrawerItemClickListener(List<Callback> handlers){
        mHandlers = handlers;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mHandlers.size() < position + 1){
            System.err.println("[DrawerItemClickListener.onItemClick] menu item click handler not found");
            return;
        }

        Callback handler = mHandlers.get(position);
        handler.execute(null, S.OK);
    }
}