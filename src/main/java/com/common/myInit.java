package com.common;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class myInit implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("start init servelt");
        ServletContext ctx = sce.getServletContext();
        utils.webPath = sce.getServletContext().getRealPath("/") + "files/";
        String[] dirArr = new String[]{"avatar", "backImg", "momentsImg", "phoneFile"};
        for (String mkdir : dirArr) {
            utils.mkdir(mkdir);
        }
        config.init();
        DbUtils.initDataSource();
        System.out.println("init is success");
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}