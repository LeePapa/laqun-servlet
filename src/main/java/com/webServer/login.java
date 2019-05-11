package com.webServer;

import com.common.config;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

@WebServlet({"/api/webServer/login"})
public class login extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        getServletContext().log("start login: " + request.getParameter("loginPassword"));
        if (request.getParameter("loginPassword").equals(config.get("loginPassword"))) {
            resJo.put("resInfo", "登录成功");
            HttpSession session = request.getSession();
            session.setAttribute("loginPassword", request.getParameter("loginPassword"));
            session.setMaxInactiveInterval(0);
            getServletContext().log("session password: " + session.getAttribute("loginPassword"));
        } else {
            resJo.put("resInfo", "密码错误");
        }
        pw.println(resJo);
        pw.close();
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}