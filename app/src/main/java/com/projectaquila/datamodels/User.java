package com.projectaquila.datamodels;

public class User extends DataModelBase {
    private String mFirstName;
    private String mLastName;
    private String mToken;

    public User(String id, String firstName, String lastName, String token){
        super(id);
        mFirstName = firstName;
        mLastName = lastName;
        mToken = token;
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
}
