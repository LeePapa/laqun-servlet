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

@WebServlet({"/api/phoneServer/getAddWx"})
public class getAddWx extends HttpServlet {
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
                stmt.execute("lock tables addWx write");
                stmt = conn.prepareStatement("select * from addWx where isUse = 0 order by priority asc limit 1 for update");
                res = stmt.executeQuery();
                if (res.next()) {
                    JSONObject wxJo = new JSONObject();
                    wxJo.put("phone", res.getString("phone"));
                    String customer = res.getString("customer");
                    wxJo.put("customer", customer);
                    resJo.put("data", wxJo.toString());
                    resJo.put("res", "success");
                    stmt = conn.prepareStatement("update addWx set isUse = 1, loginWx = ? where phone = '" + res.getString("phone") + "'");
                    stmt.setString(1, request.getParameter("loginWx"));
                    stmt.execute();
                    stmt.execute("unlock tables");
                    stmt.execute("update customer set addNum = addNum + 1 where name = '" + customer + "'");
                    stmt.execute("update loginWx set addNum = addNum + 1 where wxid = '" + request.getParameter("loginWx") + "'");
                } else {
                    resJo.put("res", "fail");
                    resJo.put("errInfo", "没有可用添加微信");
                }
            } else {
                resJo.put("res", "fail");
                resJo.put("errInfo", "noSn" + request.getParameter("sn"));
            }
            if (stmt != null) {
                try {
                    stmt.execute("unlock tables");
                    stmt.close();
                } catch (Exception e) {
                }
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e2) {
            resJo.put("res", "fail");
            resJo.put("errInfo", e2.getMessage());
            if (stmt != null) {
                try {
                    stmt.execute("unlock tables");
                    stmt.close();
                } catch (Exception e3) {
                }
            }
        } catch (Throwable th) {
            if (stmt != null) {
                try {
                    stmt.execute("unlock tables");
                    stmt.close();
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