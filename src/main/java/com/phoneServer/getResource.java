package com.phoneServer;

import com.common.InOutLog;
import com.common.utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet({"/api/phoneServer/getResource"})
public class getResource extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        int resourcesNum = Integer.valueOf(request.getParameter("resourcesNum")).intValue();
        String resourceType = request.getParameter("resourcesType");
        try {
            conn = utils.getConnection();
            if (utils.snHttpTimeMap.containsKey(request.getParameter("sn"))) {
                utils.snHttpTimeMap.put(request.getParameter("sn"), utils.getCurrentTimeStr());
                stmt = conn.prepareStatement("select * from resource where type = ? order by rand() limit ?");
                stmt.setString(1, resourceType);
                stmt.setInt(2, resourcesNum);
                ResultSet res = stmt.executeQuery();
                JSONArray resJa = new JSONArray();
                while (res.next()) {
                    if (resourceType.equals("avatar") || resourceType.equals("backImg") || resourceType.equals("momentsImg")){
                        resJa.put("http://" + resourceType.toLowerCase() + utils.tpUriPre +  res.getString("val"));
                    }else{
                        resJa.put(res.getString("val"));
                    }

                }
                resJo.put("res", "success");
                resJo.put("data", resJa);
            } else {
                resJo.put("errInfo", "noSn" + request.getParameter("sn"));
                resJo.put("res", "fail");
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                }
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            resJo.put("errInfo", utils.getExceptionMsg(e2));
            resJo.put("res", "fail");
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e3) {
                }
            }
        } catch (Throwable th) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e4) {
                }
            }
        }
        pw.println(resJo.toString());
        pw.close();
        InOutLog.logInOut(request, resJo);
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}