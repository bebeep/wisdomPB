package com.bebeep.wisdompb.bean;

import java.io.Serializable;

public class CommonTypeEntity implements Serializable{

    private String id;
    private String title;

    private String imgUrl;

    private int isCorrect; //是否为正确答案  0否 1是
//    private boolean isChecked;//用户是否已经选择了这个答案
    private boolean hasChecked;//用户是否已经选择了这个答案

    private int count;//消息数量

    private String imgSrc;
    private String content;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

//    public boolean isChecked() {
//        return isChecked;
//    }
//
//    public void setChecked(boolean checked) {
//        isChecked = checked;
//    }


    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isHasChecked() {
        return hasChecked;
    }

    public void setHasChecked(boolean hasChecked) {
        this.hasChecked = hasChecked;
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
