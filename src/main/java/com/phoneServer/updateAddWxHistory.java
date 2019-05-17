package com.phoneServer;

import com.common.utils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet({"/api/phoneServer/updateAddWxHistory"})
public class updateAddWxHistory extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            String wxid= request.getParameter("wxid");
            String remark = request.getParameter("remark");
            conn = utils.getConnection();
            stmt = conn.prepareStatement("update sn set lastHttpTime = ? where sn = ?");
            stmt.setString(1, utils.getCurrentTimeStr());
            stmt.setString(2, request.getParameter("sn"));
            if (stmt.executeUpdate() == 1) {
                stmt = conn.prepareStatement("select * from addWxHistory where wxid = ? limit 1");
                stmt.setString(1, wxid);
                res = stmt.executeQuery();
                if(res.next()) {
                    System.out.println("update: " + res.getString("addTime"));
                    stmt = conn.prepareStatement("update addWxHistory set addNum = addNum + 1, goAddTime = ?, remark = ? where wxid = ?");
                    stmt.setString(1, utils.getCurrentTimeStr());
                    stmt.setString(2, remark);
                    stmt.setString(3, wxid);
                }else{
                    System.out.println("insert");
                    stmt = conn.prepareStatement("insert into addWxHistory(wxid, addNum, addTime, goAddTime, remark) values(?, 1, ?, ?, ?)");
                    stmt.setString(1, wxid);
                    stmt.setString(2, utils.getCurrentTimeStr());
                    stmt.setString(3, utils.getCurrentTimeStr());
                    stmt.setString(4, remark);
                }
                stmt.executeUpdate();
                resJo.put("res", "success");
            }else{
                resJo.put("res", "fail");
                resJo.put("errInfo", "noSn" + request.getParameter("sn"));
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
            resJo.put("res", "fail");
            resJo.put("errInfo", utils.getExceptionMsg(e2));
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