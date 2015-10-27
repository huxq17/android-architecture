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
public class GoodServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Observable.create(new Observable.OnSubscribe<Param>() {
                              @Override
                              public void call(Subscriber<? super Param> subscriber) {
                                  String param_id = req.getParameter("id");
                                  //判断参数
                                  int goodId = 0;
                                  if (param_id == null || param_id.isEmpty()) {
                                      subscriber.onError(new Throwable("request param id"));
                                  } else {
                                      try {
                                          goodId = Integer.parseInt(param_id);
                                      } catch (NumberFormatException e) {
                                          goodId = 0;
                                      }
                                  }
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

                                  Param param = new Param();
                                  param.setGoodId(goodId);
                                  param.setUserId(userId);
                                  subscriber.onNext(param);
                                  subscriber.onCompleted();
                              }
                          }
        ).flatMap(param -> Observable.create(new Observable.OnSubscribe<Param>() {
            @Override
            public void call(Subscriber<? super Param> subscriber) {
                PreparedStatement prestmt = null;
                try {
                    //No changes has been made in the database yet, so now we will commit
                    //the changes.
                    JSONObject jsonObject = new JSONObject();

                    //查询是否管理员
                    boolean onManager = false;
                    prestmt = conn.prepareStatement(
                            "SELECT count(1) FROM user,goods where user.id=? and goods.id=? and (user.onManager=1 OR (user.onManager=0 and onSale=1));");
                    prestmt.setInt(1, param.getUserId());
                    prestmt.setInt(2, param.getGoodId());
                    ResultSet resultSet = prestmt.executeQuery();
                    resultSet.next();
                    int count = resultSet.getInt(1);
                    if (count==0){
                        subscriber.onError(new Throwable("You didn't hava permissions to watch don't onSale Goods"));
                    }else{
                        subscriber.onNext(param);
                        subscriber.onCompleted();
                    }
                    resultSet.close();
                    prestmt.close();
                } catch (Exception e) {
                    subscriber.onError(new Throwable("Server have something wrong!"));
                }
            }
        })).flatMap(param -> Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(Subscriber<? super JSONObject> subscriber) {
                PreparedStatement prestmt = null;
                try {
                    prestmt = conn.prepareStatement("SELECT id,name, count,weight, marketPrice,shopPrice,onSale,introduction,commendCount,createTime FROM goods WHERE  id=? ;");
                    prestmt.setInt(1, param.getGoodId());
                    ResultSet resultSet = prestmt.executeQuery();
                    JSONObject object = new JSONObject();
                    if (resultSet.next()) {
                        object.put("id", resultSet.getInt("id"));
                        object.put("name", resultSet.getString("name"));
                        object.put("count", resultSet.getInt("count"));
                        object.put("weight", resultSet.getDouble("weight"));
                        object.put("marketPrice", resultSet.getDouble("marketPrice"));
                        object.put("shopPrice", resultSet.getDouble("shopPrice"));
                        object.put("onSale", resultSet.getBoolean("onSale"));
                        object.put("introduction", resultSet.getString("introduction"));
                        object.put("commendCount", resultSet.getInt("commendCount"));
                        object.put("createTime", resultSet.getTimestamp("createTime").getTime());
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
