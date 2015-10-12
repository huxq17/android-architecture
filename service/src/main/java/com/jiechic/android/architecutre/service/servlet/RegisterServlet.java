package com.jiechic.android.architecutre.service.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by <a href="http://www.jiechic.com" target="_blank">jiechic</a> on 15/9/29.
 */
public class RegisterServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String param_login = req.getParameter("login");
        String param_password = req.getParameter("password");
        String param_is_manager = req.getParameter("is_manager");
        //判断参数
        if (param_login == null || param_login.isEmpty()) {
            resp.getWriter().print(ResultHandler.Fail(-1, "request param login"));
            return;
        }

        if (param_password == null || param_password.isEmpty()) {
            resp.getWriter().print(ResultHandler.Fail(-1, "request param password"));
            return;
        }

        //参数无误，转换参数
        String login = param_login;
        String password = param_password;
        Boolean is_manager = false;
        if ("1".equals(param_is_manager)) {
            is_manager = true;
        }

        PreparedStatement prestmt = null;
        try {
            prestmt = conn.prepareStatement("SELECT * FROM user where login=?");
            prestmt.setString(1, login);
            ResultSet resultSet = prestmt.executeQuery();
            if (resultSet.next()) {
                resp.getWriter().print(ResultHandler.Fail(-1, "Account is allready register"));
                prestmt.close();
            } else {
                prestmt.close();
                prestmt = conn.prepareStatement("INSERT INTO user (login, password,is_manager) VALUES (?,?,?);");
                prestmt.setString(1, login);
                prestmt.setString(2, password);
                prestmt.setBoolean(3, is_manager);
                prestmt.execute();
                resp.getWriter().print(ResultHandler.Success(null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().print(ResultHandler.Fail(-1, e.getMessage()));
        }
    }
}
