package com.jiechic.android.architecture.domain.entity.data;

/**
 * Created by jiechic on 15/10/30.
 */
public class Good {
    private int id;
    private String name;
    private int count;
    private double marketPrice;
    private double shopPrice;
    private String introduction;
    private double weight;
    private int commendCount;
    private boolean onSale;
    private long createTIme;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public double getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(double shopPrice) {
        this.shopPrice = shopPrice;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getCommendCount() {
        return commendCount;
    }

    public void setCommendCount(int commendCount) {
        this.commendCount = commendCount;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

    public long getCreateTIme() {
        return createTIme;
    }

    public void setCreateTIme(long createTIme) {
        this.createTIme = createTIme;
    }
}