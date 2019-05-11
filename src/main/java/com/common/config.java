package com.common;

import org.json.JSONObject;

import java.io.File;

public class config {
    static JSONObject conJo = null;

    public static void init() {
        System.out.println("init config json");
        try {
            conJo = new JSONObject(utils.rlFromF(utils.webPath + "globalConf.json"));
        } catch (Exception e) {
            conJo = new JSONObject();
        }
    }

    public static String get(String key) {
        return conJo.optString(key);
    }

    public static void set(String key, String val) {
        conJo.put(key, val);
        utils.wlToF(utils.webPath + "globalConf.json", conJo.toString());
    }
}