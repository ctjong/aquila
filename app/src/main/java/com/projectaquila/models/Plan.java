package com.projectaquila.models;

import com.projectaquila.AppContext;

import org.json.JSONException;
import org.json.JSONObject;

public class Plan {
    private String mId;
    private String mAuthorId;
    private boolean mIsPublic;
    private String mTitle;
    private String mDescription;
    private String mImageUrl;

    public Plan(String id, String authorId, boolean isPublic, String title, String description, String imageUrl){
        mId = id;
        mAuthorId = authorId;
        mIsPublic = isPublic;
        mTitle = title;
        mDescription = description;
        mImageUrl = imageUrl;
    }

    public static Plan parse(Object object){
        if(!(object instanceof JSONObject)){
            return null;
        }
        JSONObject json = (JSONObject)object;
        try{
            String id = json.getString("id");
            String authorId = json.getString("authorId");
            boolean isPublic = json.getBoolean("ispublic");
            String title = json.getString("title");
            String description = json.getString("description");
            String imageUrl = json.getString("imageurl");
            if(id == null || authorId == null || title == null){
                System.err.println("[Plan.parse] failed to parse plan");
                return null;
            }
            return new Plan(id, authorId, isPublic, title, description, imageUrl);
        }catch(JSONException e){
            System.err.println("[Task.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
    }

    public String getId(){
        return mId;
    }

    public String getAuthorId(){
        return mAuthorId;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public boolean isPublic(){
        return mIsPublic;
    }

    public void setTitle(String title){
        if(AppContext.getCurrent().getActiveUser().getId().equals(mAuthorId)){
            mTitle = title;
        }
    }

    public void setDescription(String description){
        if(AppContext.getCurrent().getActiveUser().getId().equals(mAuthorId)){
            mDescription = description;
        }
    }

    public void setImageUrl(String imageUrl){
        if(AppContext.getCurrent().getActiveUser().getId().equals(mAuthorId)){
            mImageUrl = imageUrl;
        }
    }

    public void setIsPublic(boolean isPublic){
        if(AppContext.getCurrent().getActiveUser().getId().equals(mAuthorId)){
            mIsPublic = isPublic;
        }
    }
}
