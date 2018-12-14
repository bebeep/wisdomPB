package com.bebeep.wisdompb.bean;

import java.util.List;

public class SpecialEduEntity {
    private String title;//标题
    private String id;//1,
    private String whetherUrlAddress;// 是否为url地址：0否 1是
    private String url;//外链接地址，whetherUrlAddress == 1时有效
    private String pictureAddress;//图片地址
    private String updateDate;//更新日期


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWhetherUrlAddress() {
        return whetherUrlAddress;
    }

    public void setWhetherUrlAddress(String whetherUrlAddress) {
        this.whetherUrlAddress = whetherUrlAddress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }


    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }
}
