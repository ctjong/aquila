package com.projectaquila.datamodels;

import com.projectaquila.common.TaskDate;
import com.projectaquila.services.HelperService;

import org.json.JSONException;
import org.json.JSONObject;

public class User extends DataModelBase {
    private String mUserName;
    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private String mToken;
    private TaskDate mRegisteredTime;

    public User(String id, String firstName, String lastName, String token){
        super(id);
        mFirstName = firstName;
        mLastName = lastName;
        mToken = token;
    }

    private User(String id, String userName, String email, String firstName, String lastName, TaskDate registeredTime){
        super(id);
        mFirstName = firstName;
        mLastName = lastName;
        mUserName = userName;
        mEmail = email;
        mFirstName = firstName;
        mLastName = lastName;
        mRegisteredTime = registeredTime;
    }

    /**
     * Parse the given object and try to create a user object
     * @param object input object
     * @return plan task object, or null on failure
     */
    public static User parse(Object object){
        if(!(object instanceof JSONObject)){
            return null;
        }
        JSONObject json = (JSONObject)object;
        try{
            String id = json.getString("id");
            String userName = json.getString("username");
            String email = json.getString("email");
            String firstName = json.getString("firstname");
            String lastName = json.getString("lastname");
            TaskDate registeredTime = TaskDate.parseDateKey(json.getString("createdtime"));
            if(id == null || userName == null){
                HelperService.logError("[User.parse] failed to parse user object");
                return null;
            }
            return new User(id, userName, email, firstName, lastName, registeredTime);
        }catch(JSONException e){
            HelperService.logError("[User.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
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
