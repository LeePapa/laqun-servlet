package com.webServer;

import com.common.config;
import com.common.utils;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.auth.COSSigner;
import com.qcloud.cos.http.HttpMethodName;
import org.apache.commons.fileupload.FileItem;
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
import java.util.Date;

@WebServlet({"/api/webServer/getImgAuth"})
public class getImgAuth extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
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
            String bucketName = "laqun-1258004048";
            COSCredentials cred = new BasicCOSCredentials("AKIDpRq9D1nI50jLQLeiKhdAXLF8ORoG5FWa", " BD4NSL3ZtztJYYMy1sGXZOyMAoEON6bq");
            COSSigner signer = new COSSigner();
            Date expiredTime = new Date(System.currentTimeMillis() + 3600L * 1000L);
            String sign = signer.buildAuthorizationStr(HttpMethodName.PUT, request.getParameter("key"), cred, expiredTime);
            resJo.put("res", "success");
            resJo.put("sign", sign);
        } catch (Exception e) {
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
        }
        pw.println(resJo);
        pw.close();
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}