package com.webServer;

import com.common.config;
import com.common.utils;
import java.io.FileWriter;
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

@WebServlet({"/api/webServer/operateLoginWx"})
public class operateLoginWx extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        HttpSession session = request.getSession();
        if (session.getAttribute("loginPassword") == null || !session.getAttribute("loginPassword").equals(config.get("loginPassword"))) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "请先登录");
            pw.println(resJo);
            return;
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            conn = utils.getConnection();
            String operateType = request.getParameter("operateType");
            if(operateType.equals("del")) {
                stmt = conn.prepareStatement("delete from loginWx where state = ?");
                stmt.setString(1, request.getParameter("wxState"));
                stmt.execute();
            }else {
                stmt = conn.prepareStatement("select wxName, wxPassword from loginWx where state = ?");
                stmt.setString(1, request.getParameter("wxState"));
                res = stmt.executeQuery();
                FileWriter fw = new FileWriter(getServletContext().getRealPath("/") + "loginWx.txt");
                while (res.next()) {
                    fw.append(res.getString("wxName") + "----" + res.getString("wxPassword") + "\r\n");
                    fw.flush();
                }
                fw.flush();
                fw.close();
            }
            resJo.put("res", "success");
        } catch (Exception e2) {
            resJo.put("res", "fail");
            resJo.put("errInfo", e2.getMessage());
            if (res != null) {
                try {
                    res.close();
                } catch (Exception e3) {
                }
            }
        }
        pw.println(resJo);
        pw.close();
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}