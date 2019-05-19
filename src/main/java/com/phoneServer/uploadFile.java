package com.phoneServer;

import com.common.InOutLog;
import com.common.utils;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.json.JSONObject;

@WebServlet({"/api/phoneServer/uploadFile"})
public class uploadFile extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Map map = utils.getFormData(request);
            String fileName = ((FileItem) map.get("fileName")).getString("utf-8");
            FileItem fi = (FileItem) map.get("file");
            System.out.println(fileName);
            fi.write(new File(utils.webPath + "/phoneFile/" + fileName));
            resJo.put("res", "success");
        } catch (Exception e) {
            e.printStackTrace();
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
        }
        pw.println(resJo);
        pw.close();
        InOutLog.logInOut(request, resJo);
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}