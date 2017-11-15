package com.feedback.test.hr_emp_feedback_sys;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by pranjul on 6/11/17.
 */

class MyJSONParser {
    Context context;
    JSONObject jsonObject;
    MyJSONParser(Context context, JSONObject jsonObject){
        this.context = context;
        this.jsonObject = jsonObject;
    }

    boolean authenticated(){
        return true;
    }
}
