package com.jiechic.android.architecture.domain.entity.request;

/**
 * Created by jiechic on 15/10/30.
 */
public class Login {
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
