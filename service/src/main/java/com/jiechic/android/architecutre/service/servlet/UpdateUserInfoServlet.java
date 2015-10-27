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
public class UpdateUserInfoServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Observable.create(
                new Observable.OnSubscribe<Param>() {
                    @Override
                    public void call(Subscriber<? super Param> subscriber) {
                        String param_name = req.getParameter("name");
                        String param_nickname = req.getParameter("nickName");
                        String param_sex = req.getParameter("sex");
                        String param_birthday = req.getParameter("birthday");
                        String param_qq = req.getParameter("qq");
                        String param_email = req.getParameter("email");
                        String param_phone = req.getParameter("phone");

                        Param param = new Param();
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
                        param.setId(userId);

                        if (param_name != null && !param_name.isEmpty()) {
                            param.setName(param_name);
                        }

                        if (param_nickname != null && !param_nickname.isEmpty()) {
                            param.setNickName(param_nickname);
                        }

                        if (param_sex != null && !param_sex.isEmpty()) {
                            try {
                                param.setSex(Integer.parseInt(param_sex));
                            } catch (NumberFormatException e) {
                                param.setSex(0);
                            }
                        }

                        if (param_birthday != null && !param_birthday.isEmpty()) {
                            param.setBirthday(param_birthday);
                        }

                        if (param_qq != null && !param_qq.isEmpty()) {
                            param.setQq(param_qq);
                        }

                        if (param_email != null && !param_email.isEmpty()) {
                            param.setEmail(param_email);
                        }

                        if (param_phone != null && !param_phone.isEmpty()) {
                            param.setPhone(param_phone);
                        }

                        subscriber.onNext(param);
                        subscriber.onCompleted();
                    }
                }
        ).flatMap(param -> Observable.create(
                new Observable.OnSubscribe<JSONObject>() {
                    @Override
                    public void call(Subscriber<? super JSONObject> subscriber) {
                        PreparedStatement prestmt = null;
                        try {
                            prestmt = conn.prepareStatement("SELECT id,name, nickname,sex, birthday,qq,email,phone FROM user_info WHERE  id=? ;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                            prestmt.setInt(1, param.getId());
                            ResultSet resultSet = prestmt.executeQuery();
                            JSONObject object = new JSONObject();
                            if (resultSet.next()) {
                                if (param.getName() != null && !param.getName().isEmpty()) {
                                    resultSet.updateString("name", param.getName());
                                }

                                if (param.getNickName() != null && !param.getNickName().isEmpty()) {
                                    resultSet.updateString("nickName", param.getNickName());
                                }

                                if (param.getSex() != 0) {
                                    resultSet.updateInt("sex", param.getSex());
                                }

                                if (param.getBirthday() != null && !param.getBirthday().isEmpty()) {
                                    resultSet.updateString("birthday", param.getBirthday());
                                }

                                if (param.getQq() != null && !param.getQq().isEmpty()) {
                                    resultSet.updateString("qq", param.getQq());
                                }

                                if (param.getEmail() != null && !param.getEmail().isEmpty()) {
                                    resultSet.updateString("email", param.getEmail());
                                }

                                if (param.getPhone() != null && !param.getPhone().isEmpty()) {
                                    resultSet.updateString("phone", param.getPhone());
                                }
                                resultSet.updateRow();

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
                }
        )).subscribe(new Subscriber<JSONObject>() {
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
        private int id;
        private String name;
        private String nickName;
        private int sex = 0;
        private String birthday;
        private String qq;
        private String email;
        private String phone;

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

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getQq() {
            return qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
