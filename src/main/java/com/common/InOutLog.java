package com.common;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class InOutLog {
    public static void logInOut(HttpServletRequest request, JSONObject resJo) {
        if (resJo.optString("res").equals("fail")) {
            String url = request.getRequestURI();
            Map<String, String[]> reqMap = request.getParameterMap();
            JSONObject reqJo = new JSONObject();
            for(String k: reqMap.keySet()) {
                reqJo.put(k, reqMap.get(k));
            }
            request.getServletContext().log("=========================");
            request.getServletContext().log(url);
            request.getServletContext().log(reqJo.toString());
            request.getServletContext().log(resJo.toString());
            request.getServletContext().log("=========================");
        }

    }
}
