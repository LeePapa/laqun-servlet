package com.phoneServer;

import com.common.utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        try {
            conn = utils.getConnection();
            stmt = conn.prepareStatement("select * from sn where sn = ? limit 1");
            stmt.setString(1, request.getParameter("sn"));
            ResultSet res = stmt.executeQuery();
            res.last();
            if (res.getRow() > 0) {
                stmt = conn.prepareStatement("select * from " + request.getParameter("resourcesType") + " order by rand() limit ?");
                stmt.setInt(1, resourcesNum);
                res = stmt.executeQuery();
                JSONArray resJa = new JSONArray();
                while (res.next()) {
                    resJa.put(res.getString("val"));
                }
                resJo.put("res", "success");
                resJo.put("data", resJa);
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
            resJo.put("res", "fail");
            resJo.put("errInfo", e2.getMessage());
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
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}