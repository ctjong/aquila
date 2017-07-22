package com.projectaquila.contexts;

import com.projectaquila.common.PlanCollectionType;
import com.projectaquila.datamodels.PlanCollection;
import com.projectaquila.datamodels.TaskCollection;

public class DataContext {
    private TaskCollection mTasks;
    private PlanCollection mEnrolledPlans;
    private PlanCollection mCreatedPlans;

    public DataContext(){
        mTasks = new TaskCollection();
        mEnrolledPlans = new PlanCollection(PlanCollectionType.ENROLLED);
        mCreatedPlans = new PlanCollection(PlanCollectionType.CREATED);
    }

    public TaskCollection getTasks(){
        return mTasks;
    }

    public PlanCollection getEnrolledPlans(){
        return mEnrolledPlans;
    }

    public PlanCollection getCreatedPlans(){
        return mCreatedPlans;
    }
}
