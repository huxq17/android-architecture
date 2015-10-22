package com.jiechic.android.architecutre.service.servlet;

import org.json.JSONException;
import org.json.JSONObject;

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
public class LoginServlet extends BaseServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String param_login = req.getParameter("login");
        String param_password = req.getParameter("password");
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

        PreparedStatement prestmt = null;
        try {
            prestmt = conn.prepareStatement("SELECT * FROM user where login=?");
            prestmt.setString(1, login);
            ResultSet resultSet = prestmt.executeQuery();
            if (resultSet.next()) {
                String tempPassword=resultSet.getString("password");
                if (password.equals(tempPassword)){
                    String id=resultSet.getString("id");
                    resultSet.close();
                    prestmt.close();
                    prestmt = conn.prepareStatement("SELECT * FROM user_info where id=?");
                    prestmt.setString(1, id);
                    resultSet=prestmt.executeQuery();
                    if (resultSet.next()){
                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("id", resultSet.getString("id"));
                            jsonObject.put("name", resultSet.getString("name"));
                            jsonObject.put("nickname",resultSet.getString("nickname"));
                            resp.getWriter().print(ResultHandler.Success(jsonObject));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            resp.getWriter().print(ResultHandler.Success(null));
                        }
                    }else{
                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("id", id);
                            resp.getWriter().print(ResultHandler.Success(jsonObject));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            resp.getWriter().print(ResultHandler.Success(null));
                        }
                    }
                }else{
                    resp.getWriter().print(ResultHandler.Fail(-1, "Account Password is wrong"));
                }
                prestmt.close();
            } else {
                resp.getWriter().print(ResultHandler.Fail(-1, "Account is not register"));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().print(ResultHandler.Fail(-1, e.getMessage()));
        }
    }
}
