package com.webServer;

import com.common.config;
import com.common.utils;
import org.apache.commons.fileupload.FileItem;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet({"/api/webServer/setSnRemark"})
public class setSnRemark extends HttpServlet {
    /* Access modifiers changed, original: protected */
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
        resJo.put("saveSnCount", 0);
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = utils.getConnection();
            stmt = conn.prepareStatement("update sn set remark = ? where sn = ?");
            stmt.setString(1, request.getParameter("remark"));
            stmt.setString(2, request.getParameter("sn"));
            if (stmt.executeUpdate() == 1) {
                resJo.put("res", "success");
            }else{
                resJo.put("res", "fail");
                resJo.put("errInfo", "备注设置失败");
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e2) {
                }
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e3) {
            resJo.put("res", "fail");
            resJo.put("errInfo", e3.getMessage());
        }
        pw.println(resJo);
        pw.close();
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}