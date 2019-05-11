package com.webServer;

import com.common.config;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

@WebServlet({"/api/webServer/getGlobalConf"})
public class getGlobalConf extends HttpServlet {
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
        JSONObject dataJson = new JSONObject();
        Iterator keyIt = new JSONObject(request.getParameter("key")).keys();
        String k = "";
        while (keyIt.hasNext()) {
            k = (String) keyIt.next();
            dataJson.put(k, config.get(k));
        }
        resJo.put("res", "success");
        resJo.put("data", dataJson);
        pw.println(resJo.toString());
        pw.close();
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}