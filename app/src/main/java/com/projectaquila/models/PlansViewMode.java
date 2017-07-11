package com.projectaquila.models;

public enum PlansViewMode {
    ENROLLED,
    CREATED,
    BROWSE;

    public static PlansViewMode parse(String modeStr){
        if(modeStr.toUpperCase().equals(ENROLLED.toString())){
            return ENROLLED;
        }else if(modeStr.toUpperCase().equals(CREATED.toString())){
            return CREATED;
        }else{
            return BROWSE;
        }
    }
}
