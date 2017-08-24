package com.planmaster.dataadapters;

import android.widget.ArrayAdapter;

import com.planmaster.contexts.AppContext;

/**
 * Adapter for populating data item
 */
public abstract class CollectionAdapter<T> extends ArrayAdapter<T>{
    /**
     * Instantiate a new collection adapter
     * @param layoutId layout ID to view each data item
     */
    public CollectionAdapter(int layoutId){
        super(AppContext.getCurrent().getActivity(), layoutId);
        if(AppContext.getCurrent().getEnrollments() == null){
            // the caller must have already initialized enrollments before constructing this adapter
            throw new UnsupportedOperationException("Enrollments have not been initialized while attempting to construct an adapter");
        }
    }
}
