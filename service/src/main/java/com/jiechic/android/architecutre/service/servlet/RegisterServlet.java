package com.jiechic.android.architecutre.service.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by <a href="http://www.jiechic.com" target="_blank">jiechic</a> on 15/9/29.
 */
public class RegisterServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String param_login=req.getParameter("login");
        String param_password=req.getParameter("password");
        String param_is_manager=req.getParameter("is_manager");

        if (param_login==null || param_login.isEmpty()){
            resp.getWriter().print("");
        }

        String login=param_login;
        String password=param_password;
        Boolean is_manager=false;
        if ("1".equals(param_is_manager)){
            is_manager=true;
        }

        PreparedStatement prestmt = null;
        try {



            prestmt = conn.prepareStatement("INSERT INTO user ('login', 'password','is_manager') VALUES (?,?,?);");
            prestmt.setString(1,login);
            prestmt.setString(2,password);
            prestmt.setBoolean(3,is_manager);
            prestmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        resp.getWriter().print("Hello world测试");
    }
}
