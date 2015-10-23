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
public class LoginServlet extends BaseServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Observable.create(new Observable.OnSubscribe<Param>() {
            //检测参数
            @Override
            public void call(Subscriber<? super Param> subscriber) {
                String param_login = req.getParameter("login");
                String param_password = req.getParameter("password");
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
                subscriber.onNext(param);
                subscriber.onCompleted();
            }
        }).flatMap(param -> Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //查验账户
                try {
                    PreparedStatement prestmt = conn.prepareStatement("SELECT * FROM user where login=?");
                    prestmt.setString(1, param.getLogin());
                    ResultSet resultSet = prestmt.executeQuery();
                    if (resultSet.next()) {
                        String password = resultSet.getString("password");
                        if (!param.getPassword().equals(password)) {
                            subscriber.onError(new Throwable("Account Password is wrong!"));
                        } else {
                            subscriber.onNext(resultSet.getString("id"));
                            subscriber.onCompleted();
                        }
                    } else {
                        subscriber.onError(new Throwable("Account is not register!"));
                    }
                    resultSet.close();
                    prestmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    subscriber.onError(new Throwable("Server have something wrong!"));
                }
            }
        })
        ).flatMap(s -> Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(Subscriber<? super JSONObject> subscriber) {
                //查询返回账户信息
                try {
                    PreparedStatement prestmt = conn.prepareStatement("SELECT * FROM user_info where id=?");
                    prestmt.setString(1, s);
                    ResultSet resultSet = prestmt.executeQuery();
                    if (resultSet.next()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", resultSet.getString("id"));
                        jsonObject.put("name", resultSet.getString("name"));
                        jsonObject.put("nickname", resultSet.getString("nickname"));
                        subscriber.onNext(jsonObject);
                    } else {
                        subscriber.onNext(new JSONObject());
                    }
                    subscriber.onCompleted();
                    resultSet.close();
                    prestmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    subscriber.onError(new Throwable("Server have something wrong!"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    subscriber.onError(new Throwable("Server have something wrong!"));
                }
            }
        })
        ).subscribe(new Subscriber<JSONObject>() {
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
    }
}
