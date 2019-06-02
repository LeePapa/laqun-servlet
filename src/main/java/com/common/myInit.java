package com.common;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class myInit implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("start init servelt");
        ServletContext ctx = sce.getServletContext();
        utils.webPath = sce.getServletContext().getRealPath("/") + "files/";
        String[] dirArr = new String[]{"phoneFile"};
        for (String mkdir : dirArr) {
            utils.mkdir(mkdir);
        }
        config.init();
        DbUtils.initDataSource();
        //缓存全部手机到utils.snHttpTimeMap变量，保存手机访问接口时间
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            conn = utils.getConnection();
            stmt = conn.prepareStatement("select sn from sn");
            res = stmt.executeQuery();
            int tempSnNum = 0;
            while (res.next()) {
                utils.snHttpTimeMap.put(res.getString("sn"), "");
                tempSnNum++;
            }
            ctx.log("缓存了" + tempSnNum + "条sn");
        }catch (Exception e) {
            ctx.log("缓存sn时发生错误:\n" + utils.getExceptionMsg(e));
        }finally {
            DbUtils.closeResultRes(res);
            DbUtils.closePreStmt(stmt);
            DbUtils.closeConnect(conn);
        }

        System.out.println("init is success");
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}