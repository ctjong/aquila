package com.projectaquila.models;

public class User {
    private String mId;
    private String mFirstName;
    private String mLastName;

    public User(String id, String firstName, String lastName){
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
    }

    public String getId(){
        return mId;
    }

    public String getFirstName(){
        return mFirstName;
    }

    public String getLastName(){
        return mLastName;
    }

    public void setFirstName(String firstName){
        mFirstName = firstName;
    }

    public void setLastName(String lastName){
        mLastName = lastName;
    }
}
