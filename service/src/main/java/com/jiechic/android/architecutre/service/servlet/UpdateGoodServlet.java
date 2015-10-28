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
public class UpdateGoodServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Observable.create(
                new Observable.OnSubscribe<Param>() {
                    @Override
                    public void call(Subscriber<? super Param> subscriber) {
                        String param_id = req.getParameter("id");
                        String param_name = req.getParameter("name");
                        String param_count = req.getParameter("count");
                        String param_marketPrice = req.getParameter("marketPrice");
                        String param_shopPrice = req.getParameter("shopPrice");
                        String param_weight = req.getParameter("weight");
                        String param_commendCount = req.getParameter("commendCount");
                        String param_onSale = req.getParameter("onSale");

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

                        if (param_id != null && !param_id.isEmpty()) {
                            try {
                                param.setId(Integer.parseInt(param_id));
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param id muse be integer"));
                                return;
                            }
                        } else {
                            subscriber.onError(new Throwable("param must have id"));
                            return;
                        }

                        if (param_name != null && !param_name.isEmpty()) {
                            param.setName(param_name);
                        }

                        if (param_count != null && !param_count.isEmpty()) {
                            try {
                                param.setCount(Integer.parseInt(param_count));
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param count muse be integer"));
                                return;
                            }
                        }

                        if (param_marketPrice != null && !param_marketPrice.isEmpty()) {
                            try {
                                param.setMarketPrice(Double.parseDouble(param_marketPrice));
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param marketPrice muse be Double"));
                                return;
                            }
                        }

                        if (param_shopPrice != null && !param_shopPrice.isEmpty()) {
                            try {
                                param.setShopPrice(Double.parseDouble(param_shopPrice));
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param shopPrice muse be Double"));
                                return;
                            }
                        }

                        if (param_weight != null && !param_weight.isEmpty()) {
                            try {
                                param.setWeight(Double.parseDouble(param_weight));
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param weight muse be Double"));
                                return;
                            }
                        }

                        if (param_commendCount != null && !param_commendCount.isEmpty()) {
                            try {
                                param.setCommendCount(Integer.parseInt(param_commendCount));
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param commendCount muse be integer"));
                                return;
                            }
                        }

                        if (param_onSale != null && !param_onSale.isEmpty()) {
                            try {
                                param.setOnSale(Boolean.parseBoolean(param_onSale));
                                if ("1".equals(param_onSale)) {
                                    param.setOnSale(true);
                                }
                            } catch (NumberFormatException e) {
                                subscriber.onError(new Throwable("param commendCount muse be boolean"));
                                return;
                            }
                        }

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
                            prestmt = conn.prepareStatement("SELECT id,name,count,marketPrice,shopPrice,weight,commendCount,onSale FROM goods WHERE  id=? ;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                            prestmt.setInt(1, param.getId());
                            ResultSet resultSet = prestmt.executeQuery();
                            JSONObject object = new JSONObject();
                            if (resultSet.next()) {
                                if (param.getName() != null && !param.getName().isEmpty()) {
                                    resultSet.updateString("name", param.getName());
                                }

                                if (param.getCount() != null) {
                                    resultSet.updateInt("count", param.getCount());
                                }

                                if (param.getMarketPrice() != null) {
                                    resultSet.updateDouble("marketPrice", param.getMarketPrice());
                                }

                                if (param.getShopPrice() != null) {
                                    resultSet.updateDouble("shopPrice", param.getShopPrice());
                                }

                                if (param.getWeight() != null) {
                                    resultSet.updateDouble("weight", param.getWeight());
                                }

                                if (param.getCommendCount() != null) {
                                    resultSet.updateInt("commendCount", param.getCommendCount());
                                }

                                if (param.getOnSale() != null) {
                                    resultSet.updateBoolean("onSale", param.getOnSale());
                                }
                                resultSet.updateRow();

                                object.put("id", resultSet.getInt("id"));
                                object.put("name", resultSet.getString("name"));
                                object.put("count", resultSet.getInt("count"));
                                object.put("marketPrice", resultSet.getDouble("marketPrice"));
                                object.put("shopPrice", resultSet.getDouble("shopPrice"));
                                object.put("weight", resultSet.getDouble("weight"));
                                object.put("commendCount", resultSet.getInt("commendCount"));
                                object.put("onSale", resultSet.getBoolean("onSale"));
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
                }

        )).subscribe(
                new Subscriber<JSONObject>() {
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
                }

        );
    }

    class Param {
        private int userId;
        private int id;
        private String name;
        private Integer count;
        private Double marketPrice;
        private Double shopPrice;
        private String introduction;
        private Double weight;
        private Integer commendCount;
        private Boolean onSale;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
