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
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

@WebServlet({"/api/phoneServer/getLoginWx"})
public class getLoginWx extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        HttpSession session = request.getSession();
        Connection conn = null;
        PreparedStatement stmt = null;
        String sn = request.getParameter("sn");
        try {
            conn = utils.getConnection();
            getServletContext().log("is auto commit: " + conn.getAutoCommit());
            stmt = conn.prepareStatement("update sn set lastHttpTime = ? where sn = ?");
            stmt.setString(1, utils.getCurrentTimeStr());
            stmt.setString(2, request.getParameter("sn"));
            if (stmt.executeUpdate() == 1) {
                stmt = conn.prepareStatement("select count(*) as loginWxNum from loginWx where sn = ?");
                stmt.setString(1, sn);
                ResultSet res = stmt.executeQuery();
                res.next();
                if (res.getInt("loginWxNum") == Integer.valueOf(config.get("loginWxNum")).intValue()) {
                    resJo.put("res", "fail");
                    resJo.put("errInfo", "本手机登录微信数量已达上限");
                } else {
                    stmt = conn.prepareStatement("select * from loginWx where (state = '正在登录' and sn = ?) or sn='' limit 1");
                    stmt.setString(1, sn);
                    res = stmt.executeQuery();
                    if (res.next()) {
                        JSONObject wxJo = new JSONObject();
                        wxJo.put("wxName", res.getString("wxName"));
                        wxJo.put("wxPassword", res.getString("wxPassword"));
                        wxJo.put("yjInfo", res.getString("yjInfo"));
                        wxJo.put("wxid", res.getString("wxid"));
                        stmt = conn.prepareStatement("update loginWx set state='正在登录', sn=?, lastGetTime = ? where wxid = ? and wxName='" + res.getString("wxName") + "'");
                        stmt.setString(1, sn);
                        stmt.setInt(2, (int) (System.currentTimeMillis() / 1000));
                        stmt.setString(3, res.getString("wxid"));
                        stmt.execute();
                        resJo.put("res", "success");
                        resJo.put("data", wxJo);
                    } else {
                        resJo.put("res", "fail");
                        resJo.put("errInfo", "没有可用登录微信");
                    }
                }
            } else {
                resJo.put("res", "fail");
                resJo.put("errInfo", "sn不存在" + sn);
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
            resJo.put("errInfo", e2.getMessage() + sn);
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
        pw.println(resJo);
        pw.close();
        InOutLog.logInOut(request, resJo);
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}