package com.webServer;

import com.common.config;
import com.common.utils;
import java.io.File;
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
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet({"/api/webServer/getLoginWx"})
public class getLoginWx extends HttpServlet {
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
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            WritableWorkbook wwb = Workbook.createWorkbook(new File(getServletContext().getRealPath("/") + "loginWx.xls"));
            WritableSheet sh = wwb.createSheet("loginWx", 0);
            sh.addCell(new Label(0, 0, "微信帐号"));
            sh.addCell(new Label(1, 0, "密码"));
            sh.addCell(new Label(2, 0, "昵称"));
            sh.addCell(new Label(3, 0, "状态"));
            sh.addCell(new Label(4, 0, "可加好友数量"));
            sh.addCell(new Label(5, 0, "已加好友数量"));
            sh.addCell(new Label(6, 0, "登录手机sn"));
            conn = utils.getConnection();
            stmt = conn.prepareStatement("select * from loginWx where wxName like ? limit ?, ?");
            stmt.setString(1, "%" + request.getParameter("wxName") + "%");
            stmt.setInt(2, (Integer.valueOf(request.getParameter("page"))-1)*10);
            stmt.setInt(3, Integer.valueOf(request.getParameter("pageSize")));
            ResultSet res = stmt.executeQuery();
            JSONArray ja = new JSONArray();
            int i = 0;
            while (res.next()) {
                i++;
                JSONObject jo = new JSONObject();
                jo.put("wxName", res.getString("wxName"));
                jo.put("wxid", res.getString("wxid"));
                sh.addCell(new Label(0, i, res.getString("wxName")));
                jo.put("wxPassword", res.getString("wxPassword"));
                sh.addCell(new Label(1, i, res.getString("wxPassword")));
                jo.put("nick", res.getString("nick"));
                sh.addCell(new Label(2, i, res.getString("nick")));
                jo.put("avatarBase64", res.getString("avatarBase64"));
                jo.put("state", res.getString("state"));
                sh.addCell(new Label(3, i, res.getString("state")));
                jo.put("addNum", res.getString("addNum"));
                sh.addCell(new Label(4, i, res.getString("addNum")));
                jo.put("friendNum", res.getString("friendNum"));
                sh.addCell(new Label(5, i, res.getString("addedNum")));
                jo.put("sn", res.getString("sn"));
                ja.put(jo);
            }
            wwb.write();
            wwb.close();
            resJo.put("res", "success");
            resJo.put("data", ja);
            stmt = conn.prepareStatement("select count(*) as total from loginWx");
            res = stmt.executeQuery();
            if(res.next()) {
                resJo.put("total", res.getInt("total"));
            }
            res.close();
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
            e2.printStackTrace();
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
        pw.println(resJo);
        pw.close();
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}