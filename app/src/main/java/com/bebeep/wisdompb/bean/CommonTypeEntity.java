package com.bebeep.wisdompb.bean;

import java.io.Serializable;

public class CommonTypeEntity implements Serializable{

    private String id;
    private String title;


    private int isCorrect; //是否为正确答案  0否 1是
    private boolean isChecked;//用户是否已经选择了这个答案



    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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
