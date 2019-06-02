package com.webServer;

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
import org.apache.commons.fileupload.FileItem;
import org.json.JSONObject;

@WebServlet({"/api/webServer/addSn"})
public class addSn extends HttpServlet {
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
        resJo.put("saveCount", 0);
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            String[] sArr = utils.txt2array((FileItem) utils.getFormData(request).get("txtFile"));
            getServletContext().log("sArr length: " + sArr.length);
            conn = utils.getConnection();
            stmt = conn.prepareStatement("select * from sn");
            ResultSet res = stmt.executeQuery();
            res.last();
            int snCount = res.getRow();
            for (int i = 0; i < sArr.length; i++) {
                try {
                    if (!sArr[i].equals("")) {
                        stmt = conn.prepareStatement("insert into sn(sn) value(?)");
                        stmt.setString(1, sArr[i]);
                        stmt.executeUpdate();
                        utils.snHttpTimeMap.put(sArr[i], "");
                    }
                } catch (Exception e) {
                    getServletContext().log("addSn err: " + e.getMessage());
                }
            }
            stmt = conn.prepareStatement("select * from sn");
            res = stmt.executeQuery();
            res.last();
            resJo.put("res", "success");
            resJo.put("saveCount", res.getRow() - snCount);
            res.close();
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
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e4) {
                }
            }
        } catch (Throwable th) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e5) {
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