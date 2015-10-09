package com.jiechic.android.architecutre.service.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by <a href="http://www.jiechic.com" target="_blank">jiechic</a> on 15/9/29.
 */
public class BaseServlet extends HttpServlet {

    protected static Connection conn;
    protected static Statement stmt;

    @Override
    public void init() throws ServletException {
        super.init();
        if (!isConnected()) {
            connectDB();
        }
    }

    protected boolean isConnected() {
        try {
            if (conn != null && !conn.isClosed() && stmt != null && !stmt.isClosed()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void connectDB() {
        try {
            //调用Class.forName()方法加载驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("成功加载MySQL驱动！");
        } catch (ClassNotFoundException e1) {
            System.out.println("找不到MySQL驱动!");
            e1.printStackTrace();
        }

        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            String url = "jdbc:mysql://db4free.net:3306/chamqion";    //JDBC的URL
            //调用DriverManager对象的getConnection()方法，获得一个Connection对象
            conn = DriverManager.getConnection(url, "chamqion", "2812911");

            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
            stmt = conn.createStatement(); //创建Statement对象

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
