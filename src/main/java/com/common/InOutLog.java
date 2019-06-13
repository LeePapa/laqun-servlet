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
        if (resJo.optString("res").equals("fail") && !resJo.optString("errInfo").equals("时间未到期") && !resJo.optString("errInfo").startsWith("noSn")) {
            String url = request.getRequestURI();
            //遍历出手机提交的全部参数
            Map<String, String[]> reqMap = request.getParameterMap();
            JSONObject reqJo = new JSONObject();
            for(String k: reqMap.keySet()) {
                reqJo.put(k, reqMap.get(k));
            }
            StringBuilder sb = new StringBuilder();
            sb.append("=============================================================================================");
            sb.append(url);
            sb.append(reqJo.toString());
            sb.append(resJo.toString());
            request.getServletContext().log(sb.toString());
        }
    }
}
