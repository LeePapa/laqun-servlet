package com.phoneServer;

import com.common.InOutLog;
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
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

@WebServlet({"/api/phoneServer/updateAddQun"})
public class updateAddQun extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        String qunid = request.getParameter("qunid");
        String nick = request.getParameter("nick");
        int laedNum = Integer.valueOf(request.getParameter("laedNum")).intValue();
        int isUse = Integer.valueOf(request.getParameter("isUse"));
        int friendNum = Integer.valueOf(request.getParameter("friendNum"));
        String[] laWxidArr = request.getParameter("laWxidArr").split(",");
        String inParamStr = "'" + StringUtils.join(request.getParameter("laWxidArr").split(","), "','") + "'";
        try {
            conn = utils.getConnection();
            conn.nativeSQL("set names utf8mb4");
            if (utils.snHttpTimeMap.containsKey(request.getParameter("sn"))) {
                if (friendNum > 0) {
                    utils.snHttpTimeMap.put(request.getParameter("sn"), utils.getCurrentTimeStr());
                    stmt = conn.prepareStatement("update addQun set nick = ?, laedNum = laedNum + ?, qunid = ?, isUse = ?, friendNum = ?, isBad = ? where qunQr = ?");
                    stmt.setString(1, nick);
                    stmt.setInt(2, laedNum);
                    stmt.setString(3, qunid);
                    stmt.setInt(4, Integer.valueOf(request.getParameter("isUse")).intValue());
                    stmt.setInt(5, friendNum);
                    stmt.setInt(6, Integer.valueOf(request.getParameter("isBad")).intValue());
                    stmt.setString(7, request.getParameter("qunQr"));
                    resJo.put("updateQunNum", stmt.executeUpdate());

//                    stmt = conn.prepareStatement("update addWx set isLa = 1, laTime = ?, laQunId = ? where wxid in (" + inParamStr + ")");
//                    stmt.setInt(1, (int) (System.currentTimeMillis() / 1000));
//                    stmt.setString(2, request.getParameter("qunid"));
//                    resJo.put("updateWxNum", stmt.executeUpdate());

//                //先查找出全部主键id，根据id来进行update，防止死锁，试验性
//                stmt = conn.prepareStatement("select id from addWx where wxid in (" + inParamStr + ")");
//                ResultSet res = stmt.executeQuery();
//                while (res.next()) {
//                    stmt = conn.prepareStatement("update addWx set isLa = 1, laTime = ?, laQunId = ? where id = ? limit 1");
//                    stmt.setInt(1, (int) (System.currentTimeMillis() / 1000));
//                    stmt.setString(2, request.getParameter("qunid"));
//                    stmt.setInt(3, res.getInt("id"));
//                    stmt.executeUpdate();
//                }
                    resJo.put("inParamStr", inParamStr);
                    resJo.put("res", "success");
                }else{
                    resJo.put("res", "fail");
                    resJo.put("errInfo", "friendNum不能小于0，本次上传的值是：" + friendNum);
                }
            } else {
                resJo.put("errInfo", "noSn" + request.getParameter("sn"));
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