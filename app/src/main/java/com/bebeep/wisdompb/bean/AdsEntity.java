package com.bebeep.wisdompb.bean;

public class AdsEntity {
    private String id;
    private String title;
    private String url; //跳转页面
    private String pictureAddress;//图片地址
    private String type;//0外链接（也就是直接跳转url,如果为空，则不能点击）；1本地连接(调广告详细接口地址);2 不跳转

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
