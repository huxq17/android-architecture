package com.jiechic.android.architecutre.service.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by <a href="http://www.jiechic.com" target="_blank">jiechic</a> on 15/9/29.
 */
public class BaseServlet extends HttpServlet {

    protected static Connection conn;
    protected static Map<String, Cookie> cookieHashMap = new HashMap<>();

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
        String[] path = req.getRequestURI().split("/");
        if ("login".equals(path[path.length - 1]) || "register".equals(path[path.length - 1])) {
            super.service(req, resp);
        } else {
            Cookie[] cookies = req.getCookies();
            boolean isLogin=false;
            if (cookies!=null){
                for (Cookie cookie:cookies){
                    if ("id".equals(cookie.getName())){
                        if (cookieHashMap.containsKey(cookie.getValue())){
                            isLogin=true;
                            super.service(req, resp);
                            break;
                        }
                    }
                }
            }
            if (!isLogin){
                resp.getWriter().write(ResultHandler.Fail(255,"Account is not login"));
            }
        }
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

            Statement stat = conn.createStatement();
            //用户表
            stat.execute("CREATE TABLE if not exists user\n" +
                    "(\n" +
                    "id           int(11) UNSIGNED primary key not null auto_increment,\n" +
                    "login        varchar(32) COMMENT '登陆名',\n" +
                    "password     varchar(36) COMMENT '密码',\n" +
                    "onManager    tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否管理员',\n" +
                    "createTime   timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间' \n" +
                    ");");
            //用户信息表
            stat.execute("CREATE TABLE if not exists user_info\n" +
                    "(\n" +
                    "id           int(11) UNSIGNED primary key not null ,\n" +
                    "name         VARCHAR(32) COMMENT '姓名',\n" +
                    "nickname     VARCHAR(32) COMMENT '昵称',\n" +
                    "sex          int(4) DEFAULT 0 COMMENT '性别:0 未知，1 男，2 女',\n" +
                    "birthday     VARCHAR(16) COMMENT '生日，格式 yyyy-MM-dd',\n" +
                    "qq           VARCHAR(32) COMMENT 'QQ号',\n" +
                    "email        VARCHAR (64) COMMENT '邮件地址',\n" +
                    "phone        VARCHAR (16) COMMENT '电话号码'\n" +
                    ");");


            stat.execute("CREATE TABLE if not exists goods\n" +
                    "(\n" +
                    "   id             int(11) UNSIGNED primary key not null auto_increment,\n" +
                    "   name           varchar(64) COMMENT '商品名',\n" +
                    "   count          int(11)  COMMENT '商品数量',\n" +
                    "   marketPrice    double(4, 2) COMMENT '市场价格',\n" +
                    "   shopPrice      double(4, 2) COMMENT '本店价格',\n" +
                    "   introduction   varchar(1024) COMMENT '商品简介',\n" +
                    "   weight         double(4, 2) COMMENT '商品重量 kg',\n" +
                    "   commendCount   int(11) UNSIGNED COMMENT '评论数',\n" +
                    "   onSale         tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否正在销售',\n" +
                    "   createTime     timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
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
