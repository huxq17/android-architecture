package com.jiechic.android.architecutre.service.servlet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiechic on 15/10/12.
 */
public class ResultHandler {
    public static String Success(Object data) {
        return result(0, "Success", data);
    }

    public static String Fail(int code, String msg) {
        return result(code, msg, null);
    }

    private static String result(int code, String msg, Object data) {
        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("msg", msg);
            if (data != null) {
                object.put("data", data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
