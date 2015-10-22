package com.jiechic.android.architecutre.service.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

/**
 * Created by <a href="http://www.jiechic.com" target="_blank">jiechic</a> on 15/9/29.
 */
public class BaseServlet extends HttpServlet {

    protected static Connection conn;

    @Override
    public void init() throws ServletException {
        super.init();
        if (!isConnected()) {
            connectDB();
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isConnected()) {
            connectDB();
        }
        super.service(req, resp);
    }

    protected boolean isConnected() {
        try {
            if (conn != null && !conn.isClosed()) {
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

            String url = "jdbc:mysql://localhost:3306/chamqion";    //JDBC的URL
            //调用DriverManager对象的getConnection()方法，获得一个Connection对象
            conn = DriverManager.getConnection(url, "chamqion", "1234567890");

            System.out.println("成功连接数据库");

            Statement stat=conn.createStatement();
            stat.execute("CREATE TABLE if not exists user\n" +
                    "(\n" +
                    "   id           int(11) UNSIGNED primary key not null auto_increment,\n" +
                    "   login        varchar(32),\n" +
                    "   name         varchar(32),\n" +
                    "   password     varchar(36),\n" +
                    "   is_manager   tinyint(1) NOT NULL DEFAULT 0,\n" +
                    "   create_time  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP \n" +
                    ");");

            stat.execute("CREATE TABLE if not exists goods\n" +
                    "(\n"+
                    "   id             int(11) UNSIGNED primary key not null auto_increment,\n" +
                    "   name           varchar(64),\n" +
                    "   count          int(10),\n" +
                    "   price          double(4, 2),\n" +
                    "   introduction   varchar(1024),\n" +
                    "   create_time    timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
                    ");");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
