package com.webServer;

import com.common.config;
import com.common.utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet({"/api/webServer/addAddWx"})
public class addAddWx extends HttpServlet {
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
        JSONObject snJo = new JSONObject();
        snJo.put("sn", "1");
        new JSONArray().put(snJo);
        try {
            Map map = utils.getFormData(request);
            String customer = ((FileItem) map.get("customer")).getString("utf-8");
            String priority = ((FileItem) map.get("priority")).getString("utf-8");
            String[] sArr = utils.txt2array((FileItem) map.get("txtFile"));
            conn = utils.getConnection();
            stmt = conn.prepareStatement("select * from addWx");
            ResultSet res = stmt.executeQuery();
            res.last();
            int savePhoneCount = res.getRow();
            for (int i = 0; i < sArr.length; i++) {
                try {
                    System.out.println(sArr[i]);
                    if (!sArr[i].equals("")) {
                        stmt = conn.prepareStatement("insert into addWx(phone, priority, customer) value(?, ?, ?)");
                        stmt.setString(1, sArr[i]);
                        stmt.setInt(2, Integer.valueOf(priority).intValue());
                        stmt.setString(3, customer);
                        stmt.execute();
                    }
                } catch (Exception e) {
                    getServletContext().log("add addWx err: " + e.getMessage());
                }
            }
            res = stmt.executeQuery("select * from addWx");
            res.last();
            savePhoneCount = res.getRow() - savePhoneCount;
            stmt.execute("update customer set oddNum = oddNum + " + savePhoneCount + " where name = '" + customer + "'");
            resJo.put("saveCount", savePhoneCount);
            resJo.put("res", "success");
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
            e3.printStackTrace();
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