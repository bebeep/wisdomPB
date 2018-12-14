package com.bebeep.wisdompb.bean;

public class AdsEntity {
    private String title;
    private String url; //跳转页面
    private String pictureAddress;//图片地址

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }

    public AdsEntity(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }
}
