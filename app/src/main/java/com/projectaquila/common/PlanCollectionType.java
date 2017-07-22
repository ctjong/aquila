package com.projectaquila.common;

public enum PlanCollectionType {
    ENROLLED,
    CREATED,
    BROWSE;

    public static PlanCollectionType parse(String typeStr){
        if(typeStr.toUpperCase().equals(ENROLLED.toString())){
            return ENROLLED;
        }else if(typeStr.toUpperCase().equals(CREATED.toString())){
            return CREATED;
        }else{
            return BROWSE;
        }
    }
}
