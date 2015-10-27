package com.jiechic.android.architecutre.service.servlet;

import org.json.JSONArray;
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
public class GoodsServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Observable.create(new Observable.OnSubscribe<Param>() {
                              @Override
                              public void call(Subscriber<? super Param> subscriber) {
                                  String param_page = req.getParameter("page");
                                  String param_pageSize = req.getParameter("pageSize");
                                  //判断参数
                                  Integer page = 1;
                                  if (param_page != null && !param_page.isEmpty()) {
                                      try {
                                          page = Integer.parseInt(param_page);
                                      } catch (NumberFormatException e) {
                                          page = 1;
                                      }
                                  }
                                  Integer pageSize = 10;
                                  if (param_page != null && !param_page.isEmpty()) {
                                      try {
                                          pageSize = Integer.parseInt(param_pageSize);
                                      } catch (NumberFormatException e) {
                                          pageSize = 10;
                                      }
                                  }
                                  Cookie[] cookies = req.getCookies();
                                  Integer userId = 0;
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
                                  param.setPage(page);
                                  param.setPageSize(pageSize);
                                  param.setUserId(userId);
                                  subscriber.onNext(param);
                                  subscriber.onCompleted();
                              }
                          }
        ).flatMap(param -> Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(Subscriber<? super JSONObject> subscriber) {
                PreparedStatement prestmt = null;
                try {
                    //No changes has been made in the database yet, so now we will commit
                    //the changes.
                    JSONObject jsonObject = new JSONObject();

                    //查询是否管理员
                    boolean onManager = false;
                    prestmt = conn.prepareStatement(
                            "SELECT onManager FROM user where id=? ;");
                    prestmt.setInt(1, param.getUserId());
                    ResultSet resultSet = prestmt.executeQuery();
                    if (resultSet.next()) {
                        onManager = resultSet.getBoolean(1);
                    } else {
                        onManager = false;
                    }
                    resultSet.close();
                    prestmt.close();

                    //若是管理员则查询所有数据,非管理员则只查询在售的数据
                    //先查询总数
                    int count = 0;
                    prestmt = conn.prepareStatement("SELECT count(1) FROM goods WHERE  onSale=1 OR onSale=? ;");
                    prestmt.setBoolean(1, !onManager);
                    resultSet = prestmt.executeQuery();
                    if (resultSet.next()) {
                        count = resultSet.getInt(1);
                    }
                    resultSet.close();
                    prestmt.close();
                    //构造数据总数
                    jsonObject.put("total", count);


                    //查询当页数据
                    JSONArray jsonArray = new JSONArray();

                    int offet = (param.getPage() - 1) * param.getPageSize();
                    int pageSize = param.getPageSize();

                    prestmt = conn.prepareStatement(
                            "SELECT id,name, count,weight, marketPrice,shopPrice,onSale,introduction,commendCount,createTime FROM goods WHERE  onSale=1  OR  onSale=? ORDER BY createTime DESC LIMIT " + offet + "," + pageSize + ";");
                    prestmt.setBoolean(1, !onManager);

                    resultSet = prestmt.executeQuery();
                    while (resultSet.next()) {
                        JSONObject object = new JSONObject();
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
                        jsonArray.put(object);
                    }
                    jsonObject.put("list", jsonArray);
                    resultSet.close();
                    prestmt.close();
                    subscriber.onNext(jsonObject);
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
        private int userId;
        private Integer page;
        private Integer pageSize;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }


    }
}
