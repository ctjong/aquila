package com.projectaquila.models;

public class User {
    private String mId;
    private String mFirstName;
    private String mLastName;
    private String mToken;

    public User(String id, String firstName, String lastName, String token){
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
        mToken = token;
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

    public String getToken(){
        return mToken;
    }

    public void setFirstName(String firstName){
        mFirstName = firstName;
    }

    public void setLastName(String lastName){
        mLastName = lastName;
    }
}
