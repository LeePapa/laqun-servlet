package com.webServer;

import com.common.InOutLog;
import com.common.config;
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
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

@WebServlet({"/api/webServer/addCustomer"})
public class addCustomer extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        String customerName = request.getParameter("customerName");
        getServletContext().log("add customer: " + customerName);
        Connection conn = null;
        PreparedStatement stmt = null;
        HttpSession session = request.getSession();
        if (session.getAttribute("loginPassword") == null || !session.getAttribute("loginPassword").equals(config.get("loginPassword"))) {
            resJo.put("errInfo", "请先登录");
            resJo.put("res", "fail");
            pw.println(resJo);
            return;
        }
        try {
            conn = utils.getConnection();
            stmt = conn.prepareStatement("insert into customer(name) value(?)");
            stmt.setString(1, customerName);
            if (stmt.executeUpdate() == 1) {
                resJo.put("res", "success");
            } else {
                resJo.put("errInfo", "insert db fail");
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
        pw.println(resJo);
        pw.close();
        InOutLog.logInOut(request, resJo);
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}