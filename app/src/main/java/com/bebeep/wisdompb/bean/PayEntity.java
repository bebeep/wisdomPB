package com.bebeep.wisdompb.bean;

public class PayEntity {

    private String content;// 请于下月之前缴费，谢谢配合,
    private String endTime;// 2019-02-28,
    private String money;// 11,
    private String projectNmae;// 02月党费缴纳通知,
    private String state;// 0,
    private String typeName;// 党费,
    private String yearMonth;// 2019-02
    private String sign;//支付签名


    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getProjectNmae() {
        return projectNmae;
    }

    public void setProjectNmae(String projectNmae) {
        this.projectNmae = projectNmae;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }
}
