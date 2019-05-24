package com.phoneServer;

import com.common.DbUtils;
import com.common.InOutLog;
import com.common.config;
import com.common.utils;
import org.json.JSONArray;
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

@WebServlet({"/api/phoneServer/getOkLoginWx"})
public class getOkLoginWx extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        getServletContext().log("start get oklogin ");
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        HttpSession session = request.getSession();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        String sn = request.getParameter("sn");
        try {
            getServletContext().log("start get oklogin ");
            conn = utils.getConnection();
            stmt = conn.prepareStatement("update sn set lastHttpTime = ? where sn = ? limit 1");
            stmt.setString(1, utils.getCurrentTimeStr());
            stmt.setString(2, request.getParameter("sn"));
            if (stmt.executeUpdate() == 1) {
                getServletContext().log("start get oklogin ");
                stmt = conn.prepareStatement("select * from loginWx where sn = ? and state = '正常'");
                stmt.setString(1, sn);
                res = stmt.executeQuery();
                JSONArray dataJa = new JSONArray();
                while (res.next()) {
                    JSONObject jo = new JSONObject();
                    getServletContext().log(res.getString("wxName"));
                    jo.put("wxName", res.getString("wxName"));
                    jo.put("wxPassword", res.getString("wxPassword"));
                    jo.put("wxPasswordbak", res.getString("wxPasswordbak"));
                    jo.put("wxid", res.getString("wxid"));
                    jo.put("yjInfo", res.getString("yjInfo"));
                    dataJa.put(jo);
                }
                resJo.put("data", dataJa);
                resJo.put("res", "success");
            } else {
                resJo.put("errInfo", "sn不存在" + sn);
                resJo.put("res", "fail");
            }

        } catch (Exception e2) {
            resJo.put("errInfo", utils.getExceptionMsg(e2));
            resJo.put("res", "fail");
        }finally {
            DbUtils.closeResultRes(res);
            DbUtils.closePreStmt(stmt);
            DbUtils.closeConnect(conn);
            pw.println(resJo);
            pw.close();
            InOutLog.logInOut(request, resJo);
        }
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}