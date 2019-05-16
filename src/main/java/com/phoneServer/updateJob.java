package com.phoneServer;

import com.common.InOutLog;
import com.common.utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet({"/api/phoneServer/updateJob"})
public class updateJob extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            getServletContext().log(request.getParameter("jobName"));
            System.out.println(request.getParameter("jobName"));
            conn = utils.getConnection();
            stmt = conn.prepareStatement("update sn set lastHttpTime = ? where sn = ?");
            stmt.setString(1, utils.getCurrentTimeStr());
            stmt.setString(2, request.getParameter("sn"));
            if (stmt.executeUpdate() == 1) {
                System.out.println(request.getParameter("jobName"));
                stmt = conn.prepareStatement("update sn set jobName = ?, stopContent = ? where sn = ?");
                stmt.setString(1, request.getParameter("jobName"));
                stmt.setString(2, request.getParameter("stopContent") == null ? "" : request.getParameter("stopContent"));
                stmt.setString(3, request.getParameter("sn"));
                stmt.execute();
                if (request.getParameter("jobName").equals("任务已停止")) {
                    stmt = conn.prepareStatement("insert into jobStopLog (sn, jobName, stopContent) value(?, ?, ?)");
                    stmt.setString(1, request.getParameter("sn"));
                    stmt.setString(2, request.getParameter("jobName"));
                    stmt.setString(3, request.getParameter("stopContent"));
                    stmt.execute();
                }
                resJo.put("res", "success");
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
        InOutLog.logInOut(request, resJo);
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}