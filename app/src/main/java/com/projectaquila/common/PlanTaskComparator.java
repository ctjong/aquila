package com.projectaquila.common;

import com.projectaquila.datamodels.PlanTask;

import java.util.Comparator;

/**
 * Comparator to sort plan tasks
 */
public class PlanTaskComparator implements Comparator<PlanTask>{
    @Override
    public int compare(PlanTask o1, PlanTask o2) {
        return o1.getDay() - o2.getDay();
    }
}
