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
import org.json.JSONObject;

@WebServlet({"/api/webServer/addLaQun"})
public class addLaQun extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        resJo.put("saveCount", 0);
        String customer = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        HttpSession session = request.getSession();
        if (session.getAttribute("loginPassword") == null || !session.getAttribute("loginPassword").equals(config.get("loginPassword"))) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "请先登录");
            pw.println(resJo);
            return;
        }
        try {
            Map map = utils.getFormData(request);
            customer = ((FileItem) map.get("customer")).getString("utf-8");
            String priority = ((FileItem) map.get("priority")).getString("utf-8");
            String[] sArr = utils.txt2array((FileItem) map.get("txtFile"));
            conn = utils.getConnection();
            stmt = conn.prepareStatement("select * from addQun");
            ResultSet res = stmt.executeQuery();
            res.last();
            int saveQunidCount = res.getRow();
            int qunUseTimeout = Integer.valueOf(config.get("qunUseTimeout")).intValue();
            System.out.println("qun use timeout: " + qunUseTimeout);
            int lastGetTime = (int) ((System.currentTimeMillis() / 1000) - ((long) (qunUseTimeout * 60)));
            System.out.println("qunlastgettime: " + lastGetTime);
            for (int i = 0; i < sArr.length; i++) {
                try {
                    if (!sArr[i].equals("")) {
                        String[] qunArr = sArr[i].split("----");
                        stmt = conn.prepareStatement("insert into addQun(qunQr, priority, customer, laNum, lastGetTime, remark) value(?, ?, ?, ?, ?, ?)");
                        stmt.setString(1, qunArr[0]);
                        stmt.setInt(2, Integer.valueOf(priority).intValue());
                        stmt.setString(3, customer);
                        stmt.setInt(4, Integer.valueOf(qunArr[1]).intValue());
                        stmt.setInt(5, lastGetTime);
                        stmt.setString(6, "");
                        stmt.execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    getServletContext().log("add laQun err: " + e.getMessage());
                }
            }
            res = stmt.executeQuery("select * from addQun");
            res.last();
            resJo.put("saveCount", res.getRow() - saveQunidCount);
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
            resJo.put("errInfo", utils.getExceptionMsg(e3));
            resJo.put("res", "fail");
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