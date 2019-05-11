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
import org.json.JSONObject;

@WebServlet({"/api/phoneServer/getWxState"})
public class getWxState extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = utils.getConnection();
            stmt = conn.prepareStatement("select * from sn where sn = ? limit 1");
            stmt.setString(1, request.getParameter("sn"));
            ResultSet res = stmt.executeQuery();
            res.last();
            if (res.getRow() > 0) {
                stmt = conn.prepareStatement("select state from loginWx where wxid = ? limit 1");
                stmt.setString(1, request.getParameter("wxid"));
                res = stmt.executeQuery();
                if (res.next()) {
                    JSONObject wxJo = new JSONObject();
                    wxJo.put("state", res.getString("state"));
                    resJo.put("data", wxJo.toString());
                    resJo.put("res", "success");
                } else {
                    resJo.put("res", "fail");
                    resJo.put("errInfo", "没有这个微信");
                }
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