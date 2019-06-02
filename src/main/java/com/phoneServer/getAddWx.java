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
import org.json.JSONObject;

@WebServlet({"/api/phoneServer/getAddWx"})
public class getAddWx extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        String sn = request.getParameter("sn");
        try {
            conn = utils.getConnection();
            if (utils.snHttpTimeMap.containsKey(sn)) {
                utils.snHttpTimeMap.put(sn, utils.getCurrentTimeStr());
                //先判断是否还有addWx
                if (utils.addWxList.isEmpty()) {
                    if (utils.isGetAddWx.get()) {
                        resJo.put("res", "fail");
                        resJo.put("errInfo", "正在缓存添加微信...");
                    }else if (!utils.hasAddWx.get()) {
                        resJo.put("errInfo", "没有可用添加微信");
                        resJo.put("res", "fail");
                    }
                }else{
                    String phone = utils.addWxList.remove(0);
                    JSONObject wxJo = new JSONObject();
                    wxJo.put("phone", phone);
                    resJo.put("data", wxJo.toString());
                    resJo.put("res", "success");
                    stmt = conn.prepareStatement("update addWx set isUse = 1, loginWx = ? where phone = ? limit 1");
                    stmt.setString(1, request.getParameter("loginWx"));
                    stmt.setString(2, phone);
                    stmt.executeUpdate();
//                    stmt.execute("update customer set addNum = addNum + 1 where name = '" + customer + "'");
                    stmt.executeUpdate("update loginWx set addNum = addNum + 1 where wxid = '" + request.getParameter("loginWx") + "'");
                }

                //如果添加微信的列表数量少于500，并且数据库有数据，并且其他线程没有在从数据库获取，则从数据库获取到换缓存
                if (utils.addWxList.size() < 500 && utils.hasAddWx.get() && !utils.isGetAddWx.get()) {
                    utils.isGetAddWx.set(true);
                    stmt = conn.prepareStatement("select * from addWx where isUse = 0 order by priority asc limit 0, 10000");
                    ResultSet res = stmt.executeQuery();
                    int tempAddWxNum = 0;
                    if (res.next()) {
                        do{
                            if (!utils.addWxList.contains(res.getString("phone"))) {
                                utils.addWxList.add(res.getString("phone"));
                                tempAddWxNum++;
                            }
                        }while (res.next());
                    }else {
                        utils.hasAddWx.set(false);
                    }
                    utils.isGetAddWx.set(false);
                    getServletContext().log("缓存了" + tempAddWxNum + "条微信");
                }
            } else {
                resJo.put("errInfo", "noSn" + request.getParameter("sn"));
                resJo.put("res", "fail");
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                }
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e2) {
            resJo.put("errInfo", utils.getExceptionMsg(e2));
            resJo.put("res", "fail");
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e3) {
                }
            }
        } catch (Throwable th) {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e4) {
                }
            }
        }

        pw.println(resJo.toString());
        pw.close();
        InOutLog.logInOut(request, resJo);
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}