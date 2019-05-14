package com.phoneServer;

import com.common.InOutLog;
import com.common.config;
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

@WebServlet({"/api/phoneServer/checkWxUseTime"})
public class checkWxUseTime extends HttpServlet {
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
                stmt = conn.prepareStatement("select sn from loginWx where wxid = ? and sn = ? limit 1");
                stmt.setString(1, request.getParameter("wxid"));
                stmt.setString(2, request.getParameter("sn"));
                if (stmt.executeQuery().next()) {
                    stmt = conn.prepareStatement("select lastGetTime from loginWx where wxid = ? limit 1");
                    stmt.setString(1, request.getParameter("wxid"));
                    res = stmt.executeQuery();
                    if (res.next()) {
                        JSONObject wxJo = new JSONObject();
                        if ((System.currentTimeMillis() / 1000) - ((long) (Integer.valueOf(config.get("loginWxUseTime")).intValue() * 60)) > ((long) res.getInt("lastGetTime"))) {
                            stmt = conn.prepareStatement("update loginWx set lastGetTime = ? where wxid = ?");
                            stmt.setInt(1, (int) (System.currentTimeMillis() / 1000));
                            stmt.setString(2, request.getParameter("wxid"));
                            stmt.execute();
                            resJo.put("res", "success");
                        } else {
                            resJo.put("res", "fail");
                            resJo.put("errInfo", "时间未超过设置的登录微信使用时间");
                        }
                    } else {
                        resJo.put("res", "fail");
                        resJo.put("errInfo", "没有这个微信");
                    }
                } else {
                    resJo.put("res", "fail");
                    resJo.put("errInfo", "此微信号不在本手机");
                }
            } else {
                resJo.put("res", "fail");
                resJo.put("errInfo", "sn不存在： " + request.getParameter("sn"));
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
        InOutLog.logInOut(request, resJo);
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}