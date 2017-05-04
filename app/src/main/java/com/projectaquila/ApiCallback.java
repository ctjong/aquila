package com.projectaquila;

import org.json.JSONObject;

interface ApiCallback {

    void execute(JSONObject response);

}
