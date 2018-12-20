package com.bebeep.wisdompb.bean;

import java.util.List;

public class TestingItemEntity {
    private String id; //主题ID
    private String itemBankId;  //题目id
    private String title; //题目名称
    private String fractionNum; //题目分数
    private String type; //题目类型；0 单选题;1 多选题 ;2 判断题 ;3 简答题
    private List<CommonTypeEntity> itemBankAnswerList; //答案


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemBankId() {
        return itemBankId;
    }

    public void setItemBankId(String itemBankId) {
        this.itemBankId = itemBankId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFractionNum() {
        return fractionNum;
    }

    public void setFractionNum(String fractionNum) {
        this.fractionNum = fractionNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<CommonTypeEntity> getItemBankAnswerList() {
        return itemBankAnswerList;
    }

    public void setItemBankAnswerList(List<CommonTypeEntity> itemBankAnswerList) {
        this.itemBankAnswerList = itemBankAnswerList;
    }
}
