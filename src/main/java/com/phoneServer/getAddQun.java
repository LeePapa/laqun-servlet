package com.phoneServer;

import com.common.InOutLog;
import com.common.config;
import com.common.utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

@WebServlet({"/api/phoneServer/getAddQun"})
public class getAddQun extends HttpServlet {
    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0190 A:{SYNTHETIC, Splitter:B:34:0x0190} */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0195 A:{Catch:{ Exception -> 0x01cc }} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Exception e;
        Throwable th;
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        JSONObject wxJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
//        String customer = request.getParameter("customer");
        String sn = request.getParameter("sn");
        int qunUseTimeout = Integer.valueOf(config.get("qunUseTimeout")).intValue() * 60;
//        String customerInParam = StringUtils.join(request.getParameter("customerArr").split(","), "','");
        try {
            conn = utils.getConnection();
            if (utils.snHttpTimeMap.containsKey(sn)) {
                utils.snHttpTimeMap.put(sn, utils.getCurrentTimeStr());
//                stmt = conn.prepareStatement("select * from addQun where isBad = 0 and customer in ('" + customerInParam + "') and laNum > laedNum and (isUse = 0 or unix_timestamp(now())-lastGetTime>?) limit 1");
                stmt = conn.prepareStatement("select * from addQun where isBad = 0 and  laNum > laedNum and (isUse = 0 or unix_timestamp(now())-lastGetTime>?) limit 1");
                stmt.setInt(1, qunUseTimeout);
                ResultSet res = stmt.executeQuery();
                if (res.next()) {
                    JSONObject wxJo2 = new JSONObject();
                    try {
                        String qunQr = res.getString("qunQr");
                        wxJo2.put("qunQr", qunQr);
//                        wxJo2.put("customer", res.getString("customer"));
                        wxJo2.put("canLaNum", res.getInt("laNum") - res.getInt("laedNum"));
                        stmt = conn.prepareStatement("update addQun set sn = ?, isUse = 1, lastGetTime = unix_timestamp(now()) where qunQr = ?");
                        stmt.setString(1, sn);
                        stmt.setString(2, qunQr);
                        stmt.executeUpdate();
                        resJo.put("res", "群获取成功");
                        resJo.put("data", wxJo2);
                        wxJo = wxJo2;
                    } catch (Exception e2) {
                        e = e2;
                        wxJo = wxJo2;
                        try {
                            resJo.put("errInfo", utils.getExceptionMsg(e2));
                            resJo.put("res", "fail");
                            if (conn != null) {
                                try {
                                    conn.close();
                                } catch (Exception e3) {
                                }
                            }
                            if (stmt != null) {
                                stmt.close();
                            }
                            pw.println(resJo.toString());
                            pw.close();
                        } catch (Throwable th2) {
                            th = th2;
                            if (conn != null) {
                            }
                            if (stmt != null) {
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        wxJo = wxJo2;
                        if (conn != null) {
                            try {
                                conn.close();
                            } catch (Exception e4) {
                            }
                        }
                        if (stmt != null) {
                            stmt.close();
                        }
                    }
                }else{
                    //                stmt = conn.prepareStatement("select * from addQun where customer in ('" + customerInParam + "') and laNum > laedNum limit 1");
                    stmt = conn.prepareStatement("select * from addQun where laNum > laedNum limit 1");
                    if (stmt.executeQuery().next()) {
                        resJo.put("res", "请等待群被使用完毕");
                    }else {
                        resJo.put("res", "全部群被拉满");
                    }
                }

            } else {
                resJo.put("errInfo", "sn不存在服务器： " + request.getParameter("sn"));
                resJo.put("res", "fail");
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e5) {
                }
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e6) {
            e = e6;
        }
        pw.println(resJo.toString());
        pw.close();
        InOutLog.logInOut(request, resJo);
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}