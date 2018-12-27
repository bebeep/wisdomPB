package com.bebeep.wisdompb.bean;

public class ExamResultItemEntity {

    private int index;//序号
    private String itemBankId;//题目id
    private int isCorrect;//是否正确 0否 1是


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getItemBankId() {
        return itemBankId;
    }

    public void setItemBankId(String itemBankId) {
        this.itemBankId = itemBankId;
    }

    public int getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(int isCorrect) {
        this.isCorrect = isCorrect;
    }
}
