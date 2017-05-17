package com.projectaquila.data;

import org.json.JSONObject;

interface ApiCallback {

    void execute(JSONObject response);

}
