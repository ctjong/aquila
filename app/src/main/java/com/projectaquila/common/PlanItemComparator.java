package com.projectaquila.common;

import com.projectaquila.datamodels.PlanItem;

import java.util.Comparator;

public class PlanItemComparator implements Comparator<PlanItem>{
    @Override
    public int compare(PlanItem o1, PlanItem o2) {
        return o1.getDay() - o2.getDay();
    }
}
