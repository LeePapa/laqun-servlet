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
        String url = request.getRequestURI();
        Map<String, String[]> reqMap = request.getParameterMap();
        JSONObject reqJo = new JSONObject();
        for(String k: reqMap.keySet()) {
            reqJo.put(k, reqMap.get(k));
        }
        try {
            Connection conn = utils.getConnection();
            PreparedStatement stmt = conn.prepareStatement("insert into snHttpLog(sn, httpTime, request, response, httpUrl) values(?, ?, ?, ?, ?)");
            stmt.setString(1, request.getParameter("sn"));
            stmt.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            stmt.setString(3, reqJo.toString());
            stmt.setString(4, resJo.toString());
            stmt.setString(5, url);
            System.out.println(stmt.executeUpdate());
            if (conn != null) conn.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
