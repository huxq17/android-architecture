package com.jiechic.android.architecutre.service.servlet;

import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscriber;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

/**
 * Created by <a href="http://www.jiechic.com" target="_blank">jiechic</a> on 15/9/29.
 */
public class AddGoodServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Observable.create(
                new Observable.OnSubscribe<Param>() {
                    @Override
                    public void call(Subscriber<? super Param> subscriber) {
                        String param_name = req.getParameter("name");
                        String param_count = req.getParameter("count");
                        String param_weight = req.getParameter("weight");
                        String param_marketPrice = req.getParameter("marketPrice");
                        String param_shopPrice = req.getParameter("shopPrice");
                        String param_onSale = req.getParameter("onSale");
                        String param_introduction = req.getParameter("introduction");
                        String param_commendCount = req.getParameter("commendCount");

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

                        param.setUserId(userId);

                        //判断参数
                        String name = "";
                        if (param_name == null || param_name.isEmpty()) {
                            subscriber.onError(new Throwable("request param name"));
                            return;
                        } else {
                            name = param_name;
                        }
                        Integer count = 0;
                        if (param_count == null || param_count.isEmpty()) {
                            subscriber.onError(new Throwable("request param count"));
                            return;
                        } else if (param_count != null) {
                            try {
                                count = Integer.parseInt(param_count);
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param count must be Integer"));
                                return;
                            }
                        }
                        Double weight = 0.0;
                        if (param_weight == null || param_weight.isEmpty()) {
                            subscriber.onError(new Throwable("request param weight"));
                            return;
                        } else {
                            try {
                                weight = Double.parseDouble(param_weight);
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param weight must be Double"));
                                return;
                            }
                        }
                        Double markPrice = 0.0;
                        if (param_marketPrice == null || param_marketPrice.isEmpty()) {
                            subscriber.onError(new Throwable("request param marketPrice"));
                            return;
                        } else {
                            try {
                                markPrice = Double.parseDouble(param_marketPrice);
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param marketPrice must be Double"));
                                return;
                            }
                        }
                        Double shopPrice = 0.0;
                        if (param_shopPrice == null || param_shopPrice.isEmpty()) {
                            subscriber.onError(new Throwable("request param shopPrice"));
                            return;
                        } else {
                            try {
                                shopPrice = Double.parseDouble(param_shopPrice);
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param shopPrice must be Double"));
                                return;
                            }
                        }
                        Boolean onSale = false;
                        if (param_onSale == null || param_onSale.isEmpty()) {
                            subscriber.onError(new Throwable("request param onSale"));
                            return;
                        } else {
                            try {
                                onSale = Boolean.parseBoolean(param_onSale);
                                if ("1".equals(param_onSale)) {
                                    onSale = true;
                                }
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param onSale must be Boolean"));
                                return;
                            }
                        }
                        String introduction = "";
                        if (param_introduction == null || param_introduction.isEmpty()) {
                            subscriber.onError(new Throwable("request param introduction"));
                            return;
                        } else {
                            introduction = param_introduction;
                        }
                        Integer commendCount = 0;
                        if (param_commendCount == null || param_commendCount.isEmpty()) {
                            subscriber.onError(new Throwable("request param commendCount"));
                            return;
                        } else {
                            try {
                                commendCount = Integer.parseInt(param_commendCount);
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param shopPrice must be Integer"));
                                return;
                            }
                        }

                        param.setName(name);
                        param.setCount(count);
                        param.setWeight(weight);
                        param.setMarketPrice(markPrice);
                        param.setShopPrice(shopPrice);
                        param.setOnSale(onSale);
                        param.setIntroduction(introduction);
                        param.setCommendCount(commendCount);
                        subscriber.onNext(param);
                        subscriber.onCompleted();
                    }
                }

        ).flatMap(param -> Observable.create(
                        new Observable.OnSubscribe<Param>() {
                            @Override
                            public void call(Subscriber<? super Param> subscriber) {
                                PreparedStatement prestmt = null;
                                try {
                                    prestmt = conn.prepareStatement("SELECT onManager FROM user WHERE  id=? ;");
                                    prestmt.setInt(1, param.getUserId());
                                    ResultSet resultSet = prestmt.executeQuery();
                                    if (resultSet.next() && resultSet.getBoolean(1)) {
                                        subscriber.onNext(param);
                                        subscriber.onCompleted();
                                    } else {
                                        subscriber.onError(new Throwable("you don't have premission to add goods"));
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                )
        ).flatMap(param -> Observable.create(
                        new Observable.OnSubscribe<JSONObject>() {
                            @Override
                            public void call(Subscriber<? super JSONObject> subscriber) {
                                PreparedStatement prestmt = null;
                                try {
                                    //No changes has been made in the database yet, so now we will commit
                                    //the changes.
                                    prestmt = conn.prepareStatement(
                                            "INSERT INTO goods (name, count,weight, marketPrice,shopPrice,onSale,introduction,commendCount) VALUES (?,?,?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);

                                    prestmt.setString(1, param.getName());
                                    prestmt.setInt(2, param.getCount());
                                    prestmt.setDouble(3, param.getWeight());
                                    prestmt.setDouble(4, param.getMarketPrice());
                                    prestmt.setDouble(5, param.getShopPrice());
                                    prestmt.setBoolean(6, param.getOnSale());
                                    prestmt.setString(7, param.getIntroduction());
                                    prestmt.setInt(8, param.getCommendCount());
                                    prestmt.executeUpdate();
                                    ResultSet resultSet = prestmt.getGeneratedKeys();
                                    int id = 0;
                                    if (resultSet.next()) {
                                        id = resultSet.getInt(1);
                                    }
                                    resultSet.close();
                                    prestmt.close();
                                    prestmt = conn.prepareStatement("SELECT createTime FROM goods WHERE id=?;");
                                    prestmt.setInt(1, id);
                                    resultSet = prestmt.executeQuery();
                                    Timestamp timestamp = null;
                                    if (resultSet.next()) {
                                        timestamp = resultSet.getTimestamp("createTime");
                                    }
                                    resultSet.close();
                                    prestmt.close();

                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("id", id);
                                        jsonObject.put("name", param.getName());
                                        jsonObject.put("count", param.getCount());
                                        jsonObject.put("weight", param.getWeight());
                                        jsonObject.put("marketPrice", param.getMarketPrice());
                                        jsonObject.put("shopPrice", param.getShopPrice());
                                        jsonObject.put("onSale", param.getOnSale());
                                        jsonObject.put("introduction", param.getIntroduction());
                                        jsonObject.put("commendCount", param.getCommendCount());
                                        jsonObject.put("createTime", timestamp == null ? 0 : timestamp.getTime());
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
                                } finally {
                                    try {
                                        conn.setAutoCommit(true);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
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
        private Integer userId;
        private String name;
        private Integer count;
        private Double marketPrice;
        private Double shopPrice;
        private String introduction;
        private Double weight;
        private Integer commendCount;
        private Boolean onSale;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Double getMarketPrice() {
            return marketPrice;
        }

        public void setMarketPrice(Double marketPrice) {
            this.marketPrice = marketPrice;
        }

        public Double getShopPrice() {
            return shopPrice;
        }

        public void setShopPrice(Double shopPrice) {
            this.shopPrice = shopPrice;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public Integer getCommendCount() {
            return commendCount;
        }

        public void setCommendCount(Integer commendCount) {
            this.commendCount = commendCount;
        }

        public Boolean getOnSale() {
            return onSale;
        }

        public void setOnSale(Boolean onSale) {
            this.onSale = onSale;
        }
    }
}
