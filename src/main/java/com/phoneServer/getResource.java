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
        getServletContext().log("start put info");
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        int resourcesNum = Integer.valueOf(request.getParameter("resourcesNum")).intValue();
        String resourceType = request.getParameter("resourcesType");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        getServletContext().log("start put info");
        try {
            conn = utils.getConnection();
            stmt = conn.prepareStatement("update sn set lastHttpTime = ? where sn = ?");
            stmt.setString(1, utils.getCurrentTimeStr());
            stmt.setString(2, request.getParameter("sn"));
            if (stmt.executeUpdate() == 1) {
                stmt = conn.prepareStatement("select * from resource where type = ? order by rand() limit ?");
                stmt.setString(1, resourceType);
                stmt.setInt(2, resourcesNum);
                ResultSet res = stmt.executeQuery();
                JSONArray resJa = new JSONArray();
                getServletContext().log("start put info");
                while (res.next()) {
                    if (resourceType.equals("avatar") || resourceType.equals("backImg") || resourceType.equals("momentsImg")){
                        resJa.put("http://" + resourceType.toLowerCase() + utils.tpUriPre +  res.getString("val"));
                    }else{
                        resJa.put(res.getString("val"));
                    }

                }
                resJo.put("res", "success");
                resJo.put("data", resJa);
                //更新手机的最后连接时间
                stmt = conn.prepareStatement("update sn set lastHttpTime = ? where sn = ?");
                stmt.setString(1, sdf.format(new Date()));
                stmt.setString(2, request.getParameter("sn"));
                stmt.executeUpdate();
            } else {
                resJo.put("res", "fail");
                resJo.put("errInfo", "noSn" + request.getParameter("sn"));
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
            resJo.put("res", "fail");
            resJo.put("errInfo", utils.getExceptionMsg(e2));
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