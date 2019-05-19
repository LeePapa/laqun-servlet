package com.webServer;

import com.common.Base64;
import com.common.config;
import com.common.utils;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jxl.CellView;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.json.JSONObject;

@WebServlet({"/api/webServer/generalExcel"})
public class generalExcel extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Exception e;
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        CellView cellView = new CellView();
        ResultSet res = null;
        ResultSet qunRes = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss");
        String excelFileName = simpleDateFormat.format(new Date()) + "_qun.xls";
        String sexName = "未知";
        HttpSession session = request.getSession();
        if (session.getAttribute("loginPassword") != null) {
            if (session.getAttribute("loginPassword").equals(config.get("loginPassword"))) {
                String qunWhere = request.getParameter("excelType").equals("已拉完的群") ? "laNum = laedNum and isGeneral = 0" : "laNum > laedNum";
                try {
                    WritableWorkbook wwb = Workbook.createWorkbook(new File(utils.webPath + excelFileName));
                    cellView.setAutosize(true);
                    conn = utils.getConnection();
                        stmt = conn.prepareStatement("select * from addQun where  " + qunWhere);
                        WritableSheet sheet = wwb.createSheet("0", 0);
                        for (int j = 0; j < 7; j++) {
                            sheet.setColumnView(j, cellView);
                        }
                        res = stmt.executeQuery();
                        int sheetRow = 0;
                        while (res.next()) {
                            if (!res.getString("qunid").equals("")) {
                                int sheetRow2 = sheetRow + 1;
                                sheet.addCell(new Label(0, sheetRow, "群名称：" + res.getString("nick")));
                                stmt = conn.prepareStatement("select * from addWx where laQunId = ?");
                                stmt.setString(1, res.getString("qunid"));
                                qunRes = stmt.executeQuery();
                                int wxidIndex = 0;
                                while (qunRes.next()) {
                                    int colIndex;
                                    int i2;
                                    String string = qunRes.getString("sex");
                                    Object obj = -1;
                                    switch (string.hashCode()) {
                                        case 48:
                                            if (string.equals("0")) {
                                                obj = null;
                                            }
                                        case 49:
                                            if (string.equals("0")) {
                                                sexName = "未知";
                                            }else if(string.equals("1")) {
                                                sexName = "男";
                                            }else {
                                                sexName = "女";
                                            }
                                            colIndex = 0 + 1;
                                            wxidIndex++;
                                            sheet.addCell(new Label(0, sheetRow2, wxidIndex + ""));
                                            i2 = colIndex + 1;
                                            sheet.addCell(new Label(colIndex, sheetRow2, qunRes.getString("nick")));
                                            colIndex = i2 + 1;
                                            sheet.addCell(new Label(i2, sheetRow2, qunRes.getString("phone")));
                                            i2 = colIndex + 1;
                                            sheet.addCell(new Label(colIndex, sheetRow2, qunRes.getString("wxid")));
                                            colIndex = i2 + 1;
                                            sheet.addCell(new Label(i2, sheetRow2, sexName));
                                            i2 = colIndex + 1;
                                            sheet.addCell(new Label(colIndex, sheetRow2, simpleDateFormat.format(new Date(Long.valueOf(qunRes.getString("laTime")).longValue() * 1000))));
                                            colIndex = i2 + 1;
                                            sheet.addCell(new Label(i2, sheetRow2, res.getString("qunid")));
                                            if (!qunRes.getString("avatar").equals("")) {
                                                i2 = colIndex + 1;
                                                try {
                                                    sheet.addImage(new WritableImage((double) colIndex, (double) sheetRow2, 1.0d, 1.0d, Base64.decode(qunRes.getString("avatar"), 2)));
                                                } catch (Exception e2) {
                                                    Exception e22 = e2;
                                                    break;
                                                }
                                            }
                                            sheetRow2++;
                                        case 50:
                                            if (string.equals("2")) {
                                                obj = 2;
                                            }
                                            colIndex = 0 + 1;
                                            wxidIndex++;
                                            sheet.addCell(new Label(0, sheetRow2, wxidIndex + ""));
                                            i2 = colIndex + 1;
                                            sheet.addCell(new Label(colIndex, sheetRow2, qunRes.getString("nick")));
                                            colIndex = i2 + 1;
                                            sheet.addCell(new Label(i2, sheetRow2, qunRes.getString("phone")));
                                            i2 = colIndex + 1;
                                            sheet.addCell(new Label(colIndex, sheetRow2, qunRes.getString("wxid")));
                                            colIndex = i2 + 1;
                                            sheet.addCell(new Label(i2, sheetRow2, sexName));
                                            i2 = colIndex + 1;
                                            sheet.addCell(new Label(colIndex, sheetRow2, simpleDateFormat.format(new Date(Long.valueOf(qunRes.getString("laTime")).longValue() * 1000))));
                                            colIndex = i2 + 1;
                                            sheet.addCell(new Label(i2, sheetRow2, res.getString("qunid")));
                                            if (!qunRes.getString("avatar").equals("")) {
                                            }
                                            sheetRow2++;
                                            break;
                                    }
                                    colIndex = 0 + 1;
                                    try {
                                        wxidIndex++;
                                        sheet.addCell(new Label(0, sheetRow2, wxidIndex + ""));
                                        i2 = colIndex + 1;
                                        sheet.addCell(new Label(colIndex, sheetRow2, qunRes.getString("nick")));
                                        colIndex = i2 + 1;
                                        sheet.addCell(new Label(i2, sheetRow2, qunRes.getString("phone")));
                                        i2 = colIndex + 1;
                                        sheet.addCell(new Label(colIndex, sheetRow2, qunRes.getString("wxid")));
                                        colIndex = i2 + 1;
                                        sheet.addCell(new Label(i2, sheetRow2, sexName));
                                        i2 = colIndex + 1;
                                        sheet.addCell(new Label(colIndex, sheetRow2, simpleDateFormat.format(new Date(Long.valueOf(qunRes.getString("laTime")).longValue() * 1000))));
                                        colIndex = i2 + 1;
                                        sheet.addCell(new Label(i2, sheetRow2, res.getString("qunid")));
                                        if (!qunRes.getString("avatar").equals("")) {
                                        }
                                        sheetRow2++;
                                    } catch (Exception e3) {
                                        e = e3;
                                        i2 = colIndex;
                                    } catch (Throwable th) {
                                    }
                                }
                                sheetRow2++;
                                if (request.getParameter("excelType").equals("已拉完的群")) {
                                    stmt = conn.prepareStatement("update addQun set isGeneral = 1 where qunid != '' and qunid = ?");
                                    stmt.setString(1, res.getString("qunid"));
                                    stmt.executeUpdate();
                                    sheetRow = sheetRow2;
                                } else {
                                    sheetRow = sheetRow2;
                                }
                            }
                        }
                    wwb.write();
                    wwb.close();
                    resJo.put("excelFileName", excelFileName);
                    resJo.put("res", "success");
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (Exception e4) {
                        }
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                    if (res != null) {
                        res.close();
                    }
                    if (qunRes != null) {
                        qunRes.close();
                    }
                } catch (Exception e5) {
                    e = e5;
                    try {
                        e.printStackTrace();
                        resJo.put("errInfo", e.getMessage());
                        getServletContext().log("web getSn err: " + e.getMessage());
                        resJo.put("res", "fail");
                        if (conn != null) {
                            try {
                                conn.close();
                            } catch (Exception e6) {
                            }
                        }
                        if (stmt != null) {
                            stmt.close();
                        }
                        if (res != null) {
                            res.close();
                        }
                        if (qunRes != null) {
                            qunRes.close();
                        }
                        pw.println(resJo);
                        pw.close();
                        return;
                    } catch (Throwable th2) {
                    }
                }
                pw.println(resJo);
                pw.close();
                return;
            }
        }
        resJo.put("res", "fail");
        resJo.put("errInfo", "请先登录");
        pw.println(resJo);
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}