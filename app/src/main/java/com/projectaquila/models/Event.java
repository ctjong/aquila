package com.projectaquila.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Event{
    private List<Callback> handlers;

    public Event(){
        handlers = new ArrayList<Callback>();
    }

    public void addHandler(Callback cb){
        handlers.add(cb);
    }

    public void removeAllHandlers(){
        handlers.clear();
    }

    public void invoke(HashMap<String, Object> params){
        for(int i=0; i<handlers.size(); i++){
            handlers.get(i).execute(params);
        }
    }
}
