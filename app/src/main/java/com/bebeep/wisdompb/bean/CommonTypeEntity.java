package com.bebeep.wisdompb.bean;

public class CommonTypeEntity {

    private String id;
    private String title;


    private int isCorrect; //是否为正确答案  0否 1是
    private boolean result; //用户答题结果


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(int isCorrect) {
        this.isCorrect = isCorrect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
