package com.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DbUtils {
    static DataSource ds = null;
    static PoolProperties p = null;

    public static void initDataSource() {
        p = new PoolProperties();
        p.setUrl("jdbc:mysql://localhost:3306/laqun?serverTimezone=GMT");
        p.setDriverClassName("com.mysql.cj.jdbc.Driver");
        p.setUsername("laqun");
        p.setPassword("laqunQun");
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setInitialSize(10);
        p.setMaxWait(2000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        ds = new DataSource();
        ds.setPoolProperties(p);
    }

    public static Connection getConnect() {
        try {
            return ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnect(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void closePreStmt(PreparedStatement preStmt) {
        if (preStmt != null) {
            try {
                preStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeResultRes(ResultSet res) {
        if (res != null) {
            try {
                res.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}