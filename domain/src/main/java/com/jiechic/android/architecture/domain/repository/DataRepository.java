package com.jiechic.android.architecture.domain.repository;

import com.jiechic.android.architecture.domain.entity.data.Good;
import com.jiechic.android.architecture.domain.entity.data.User;
import com.jiechic.android.architecture.domain.entity.request.Login;
import com.jiechic.android.architecture.domain.entity.response.GoodList;
import rx.Observable;

/**
 * Created by jiechic on 15/10/30.
 */
public interface DataRepository {
    //获取用户信息
    Observable<User> userInfo();

    Observable<User> userLogin(Login login);

    Observable<User> userUpdate(User user);

    Observable<Good> goodInfo(int id);

    Observable<GoodList> goodList(int page);

    Observable<Good> goodUpdate(Good good);

    Observable<Good> goodCreate(Good good);
}
