package com.bebeep.wisdompb.bean;

public class PayRecordEntity {
    private String amountMoney;// 100,
    private String createDate;// 2019-02-13 15;//31,
    private String title;// 2019年02月党费,
    private String type;// 状态；0线上；1线下


    public String getAmountMoney() {
        return amountMoney;
    }

    public void setAmountMoney(String amountMoney) {
        this.amountMoney = amountMoney;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
