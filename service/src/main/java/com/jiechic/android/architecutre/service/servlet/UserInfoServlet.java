package com.jiechic.android.architecutre.service.servlet;

import org.json.JSONObject;
import rx.Observable;
import rx.Subscriber;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by <a href="http://www.jiechic.com" target="_blank">jiechic</a> on 15/9/29.
 */
public class UserInfoServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Observable.create(new Observable.OnSubscribe<Integer>() {
                              @Override
                              public void call(Subscriber<? super Integer> subscriber) {
                                  int userId = 0;
                                  Cookie[] cookies = req.getCookies();
                                  if (cookies != null) {
                                      for (Cookie cookie : cookies) {
                                          if ("id".equals(cookie.getName())) {
                                              if (cookieHashMap.containsKey(cookie.getValue())) {
                                                  userId = Integer.parseInt(cookie.getValue());
                                                  break;
                                              }
                                          }
                                      }
                                  }
                                  subscriber.onNext(userId);
                                  subscriber.onCompleted();
                              }
                          }
        ).flatMap(userId -> Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(Subscriber<? super JSONObject> subscriber) {
                PreparedStatement prestmt = null;
                try {
                    prestmt = conn.prepareStatement("SELECT id,name, nickname,sex, birthday,qq,email,phone FROM user_info WHERE  id=? ;");
                    prestmt.setInt(1, userId);
                    ResultSet resultSet = prestmt.executeQuery();
                    JSONObject object = new JSONObject();
                    if (resultSet.next()) {
                        object.put("id", resultSet.getInt("id"));
                        object.put("name", resultSet.getString("name"));
                        object.put("nickname", resultSet.getString("nickname"));
                        object.put("sex", resultSet.getInt("sex"));
                        object.put("birthday", resultSet.getString("birthday"));
                        object.put("qq", resultSet.getString("qq"));
                        object.put("email", resultSet.getString("email"));
                        object.put("phone", resultSet.getString("phone"));
                    }
                    resultSet.close();
                    prestmt.close();
                    subscriber.onNext(object);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        conn.rollback();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    subscriber.onError(new Throwable("Server have something wrong!"));
                }
            }
        })).subscribe(new Subscriber<JSONObject>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                try {
                    resp.getWriter().write(ResultHandler.Fail(-1, e.getMessage()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onNext(JSONObject jsonObject) {
                try {
                    resp.getWriter().write(ResultHandler.Success(jsonObject));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    class Param {
        private int goodId;
        private int userId;

        public int getGoodId() {
            return goodId;
        }

        public void setGoodId(int goodId) {
            this.goodId = goodId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }
}
