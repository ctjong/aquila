package com.projectaquila.models;

import java.util.HashMap;

public interface Callback {

    void execute(HashMap<String, Object> params, S s);

}
