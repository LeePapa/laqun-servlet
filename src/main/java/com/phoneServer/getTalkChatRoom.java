package com.phoneServer;

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

@WebServlet({"/api/phoneServer/getTalkChatRoom"})
public class getTalkChatRoom extends HttpServlet {
    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00df A:{SYNTHETIC, Splitter:B:32:0x00df} */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00e4 A:{Catch:{ Exception -> 0x00e8 }} */
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
        String sn = request.getParameter("sn");
        try {
            conn = utils.getConnection();
            stmt = conn.prepareStatement("select * from sn where sn = ? limit 1");
            stmt.setString(1, sn);
            ResultSet res = stmt.executeQuery();
            res.last();
            if (res.getRow() > 0) {
                stmt = conn.prepareStatement("select * from talkChatRoom where friendNum < 35 limit 1");
                res = stmt.executeQuery();
                if (res.next()) {
                    JSONObject wxJo2 = new JSONObject();
                    try {
                        wxJo2.put("qunQr", res.getString("qunQr"));
                        resJo.put("res", "群获取成功");
                        resJo.put("data", wxJo2);
                        stmt = conn.prepareStatement("update talkChatRoom set friendNum = friendNum + 1 where qunQr = ?");
                        stmt.setString(1, res.getString("qunQr"));
                        stmt.executeUpdate();
                        wxJo = wxJo2;
                    } catch (Exception e2) {
                        e = e2;
                        wxJo = wxJo2;
                        try {
                            resJo.put("res", "fail");
                            resJo.put("errInfo", e.getMessage());
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
                }
                resJo.put("res", "没有可用互聊群");
            } else {
                resJo.put("res", "fail");
                resJo.put("errInfo", "sn不存在服务器： " + request.getParameter("sn"));
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
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}