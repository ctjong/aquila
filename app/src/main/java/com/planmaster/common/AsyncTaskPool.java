package com.planmaster.common;

import java.util.ArrayList;
import java.util.List;

/**
 * A pool of async task to be executed simultaneously with a common final callback
 */
public class AsyncTaskPool {
    private int mNumActive;
    private List<Callback> mTasks;
    private Callback mFinalCallback;
    private boolean mIsFinalCallbackRun;

    /**
     * Create a new async task pool
     * @param finalCallback final callback
     */
    public AsyncTaskPool(Callback finalCallback){
        mNumActive = 0;
        mTasks = new ArrayList<>();
        mFinalCallback = finalCallback;
        mIsFinalCallbackRun = false;
    }

    /**
     * Add task to the pool
     * @param task async task
     */
    public void addTask(Callback task){
        if(mIsFinalCallbackRun) return;
        mTasks.add(task);
    }

    /**
     * Execute all tasks in the pool
     */
    public void execute(){
        if(mTasks.size() == 0){
            System.out.println("[AsyncTaskPool.execute] num tasks = " + mNumActive + ". executing final callback.");
            if(mFinalCallback != null) mFinalCallback.execute(null);
            return;
        }
        for(Callback task : mTasks){
            CallbackParams retval = new CallbackParams();
            Callback cb = getTaskCallback();
            retval.set("cb", cb);
            task.execute(retval);
            mNumActive++;
            System.out.println("[AsyncTaskPool.execute] num tasks = " + mNumActive);
        }
    }

    /**
     * Get callback for each individual task
     * @return task callback
     */
    private Callback getTaskCallback(){
        return new Callback() {
            @Override
            public void execute(CallbackParams params) {
                mNumActive--;
                System.out.println("[AsyncTaskPool.getTaskCallback] active = " + mNumActive);
                if(mNumActive <= 0){
                    System.out.println("[AsyncTaskPool.getTaskCallback] executing final callback");
                    if(mFinalCallback != null && !mIsFinalCallbackRun) {
                        mFinalCallback.execute(null);
                        mIsFinalCallbackRun = true;
                    }
                }
            }
        };
    }
}
