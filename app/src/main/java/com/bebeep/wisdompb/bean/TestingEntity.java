package com.bebeep.wisdompb.bean;

import java.util.List;

public class TestingEntity {

    private String templateId; //考试模板id
    private String currItemBankId;//当前答题的题目id
    private String currAnswerIds;//当前题目用户选择的答案，多个用英文逗号隔开，默认为0
    private List<TestingItemEntity> bizList;//考试题目，显示题目列表的时候，建议随机展示


    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getCurrItemBankId() {
        return currItemBankId;
    }

    public void setCurrItemBankId(String currItemBankId) {
        this.currItemBankId = currItemBankId;
    }

    public String getCurrAnswerIds() {
        return currAnswerIds;
    }

    public void setCurrAnswerIds(String currAnswerIds) {
        this.currAnswerIds = currAnswerIds;
    }

    public List<TestingItemEntity> getBizList() {
        return bizList;
    }

    public void setBizList(List<TestingItemEntity> bizList) {
        this.bizList = bizList;
    }
}
