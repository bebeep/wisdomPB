package com.bebeep.wisdompb.bean;

import java.io.Serializable;
import java.util.List;

public class TestingItemEntity implements Serializable{
    private String id; //主题ID
    private int index;//序号
    private String itemBankId;  //题目id
    private String title; //题目名称
    private String fractionNum; //题目分数
    private int type; //题目类型；0 单选题;1 多选题 ;2 判断题 ;3 简答题
    private boolean hasChecked;//用户是否已经答题；
    private boolean showAnwsers;//是否显示正确答案
    private boolean right;//用户答案是否正确；
    private List<CommonTypeEntity> itemBankAnswerList; //答案


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isHasChecked() {
        return hasChecked;
    }

    public void setHasChecked(boolean hasChecked) {
        this.hasChecked = hasChecked;
    }

    public boolean isShowAnwsers() {
        return showAnwsers;
    }

    public void setShowAnwsers(boolean showAnwsers) {
        this.showAnwsers = showAnwsers;
    }

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<CommonTypeEntity> getItemBankAnswerList() {
        return itemBankAnswerList;
    }

    public void setItemBankAnswerList(List<CommonTypeEntity> itemBankAnswerList) {
        this.itemBankAnswerList = itemBankAnswerList;
    }
}
