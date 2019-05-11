package com.phoneServer;

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

@WebServlet({"/api/phoneServer/getIp"})
public class getIp extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        String ipAddr = "";
        if (request.getParameter("isPostIp").equals("1")) {
            ipAddr = request.getParameter("ipAddr");
        } else {
            ipAddr = request.getRemoteAddr();
        }
        try {
            conn = utils.getConnection();
            stmt = conn.prepareStatement("select * from ipConf where ipAddr = ? limit 1");
            stmt.setString(1, ipAddr);
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                if ((System.currentTimeMillis() / 1000) - ((long) res.getInt("lastUseTime")) > ((long) (Integer.valueOf(config.get("ipUseTime")).intValue() * 60))) {
                    resJo.put("res", "success");
                    stmt = conn.prepareStatement("update ipConf set useNum = useNum + 1, lastUseTime = unix_timestamp(now()) where ipAddr = ?");
                    stmt.setString(1, ipAddr);
                    stmt.execute();
                } else {
                    resJo.put("res", "fail");
                    resJo.put("errInfo", "时间未到期");
                }
            } else {
                stmt = conn.prepareStatement("insert into ipConf(ipAddr, lastUseTime) value(?, ?)");
                stmt.setString(1, ipAddr);
                stmt.setInt(2, (int) (System.currentTimeMillis() / 1000));
                stmt.execute();
                resJo.put("res", "success");
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
            resJo.put("errInfo", e2.getMessage() + (System.currentTimeMillis() / 1000));
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
}