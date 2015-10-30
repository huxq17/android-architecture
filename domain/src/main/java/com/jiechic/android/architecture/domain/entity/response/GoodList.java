package com.jiechic.android.architecture.domain.entity.response;

import com.jiechic.android.architecture.domain.entity.data.Good;

import java.util.List;

/**
 * Created by jiechic on 15/10/30.
 */
public class GoodList {
    private List<Good> list;
    private int count;

    public List<Good> getList() {
        return list;
    }

    public void setList(List<Good> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
