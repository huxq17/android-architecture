package com.jiechic.android.architecutre.service.servlet;

import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscriber;

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
        Observable.create(new Observable.OnSubscribe<Param>() {
            //检测参数
            @Override
            public void call(Subscriber<? super Param> subscriber) {
                String param_login = req.getParameter("login");
                String param_password = req.getParameter("password");
                String param_is_manager = req.getParameter("is_manager");
                //判断参数
                if (param_login == null || param_login.isEmpty()) {
                    subscriber.onError(new Throwable("request param login"));
                    return;
                }

                if (param_password == null || param_password.isEmpty()) {
                    subscriber.onError(new Throwable("request param password"));
                    return;
                }
                Param param = new Param();
                param.setLogin(param_login);
                param.setPassword(param_password);
                if ("1".equals(param_is_manager)) {
                    param.setIsManager(true);
                } else {
                    param.setIsManager(false);
                }
                subscriber.onNext(param);
                subscriber.onCompleted();
            }
        }).flatMap(param -> Observable.create(new Observable.OnSubscribe<Param>() {
            @Override
            public void call(Subscriber<? super Param> subscriber) {
                try {
                    PreparedStatement prestmt = conn.prepareStatement("SELECT * FROM user where login=?");
                    prestmt.setString(1, param.getLogin());
                    ResultSet resultSet = prestmt.executeQuery();
                    if (resultSet.next()) {
                        subscriber.onError(new Throwable("Account is allready register"));
                    } else {
                        subscriber.onNext(param);
                        subscriber.onCompleted();
                    }
                    resultSet.close();
                    prestmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    subscriber.onError(new Throwable("Server have something wrong!"));
                }
            }
        })).flatMap(param -> Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(Subscriber<? super JSONObject> subscriber) {
                PreparedStatement prestmt = null;
                try {
                    conn.setAutoCommit(false);
                    //No changes has been made in the database yet, so now we will commit
                    //the changes.
                    prestmt = conn.prepareStatement("INSERT INTO user (login, password, is_manager) VALUES (?,?,?);");
                    prestmt.setString(1, param.getLogin());
                    prestmt.setString(2, param.getPassword());
                    prestmt.setBoolean(3, param.isManager());
                    prestmt.executeUpdate();
                    prestmt.close();

                    prestmt = conn.prepareStatement("SELECT id FROM user WHERE login=?;");
                    prestmt.setString(1, param.getLogin());
                    ResultSet resultSet = prestmt.executeQuery();
                    resultSet.next();
                    int id = resultSet.getInt("id");
                    resultSet.close();
                    prestmt.close();

                    prestmt = conn.prepareStatement("INSERT INTO user_info (id,name) VALUES (?, ?);");
                    prestmt.setInt(1, id);
                    prestmt.setString(2, param.getLogin());
                    prestmt.executeUpdate();
                    prestmt.close();
                    conn.commit();

                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("id",id);
                        jsonObject.put("name",param.getLogin());
                        subscriber.onNext(jsonObject);
                        subscriber.onCompleted();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        subscriber.onNext(new JSONObject());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    try {
                        conn.rollback();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    subscriber.onError(new Throwable("Server have something wrong!"));
                }finally {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }
        })).subscribe(new Subscriber<JSONObject>() {
            @Override
            public void onCompleted() {

            }
            @Override
            public void onError(Throwable e) {
                try {
                    resp.getWriter().print(ResultHandler.Fail(-1,e.getMessage()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            @Override
            public void onNext(JSONObject jsonObject) {
                try {
                    resp.getWriter().print(ResultHandler.Success(jsonObject));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    class Param {
        private String login;
        private String password;
        private boolean isManager;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isManager() {
            return isManager;
        }

        public void setIsManager(boolean isManager) {
            this.isManager = isManager;
        }
    }
}
