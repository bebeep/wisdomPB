package com.bebeep.wisdompb.bean;

public class NewsEntity {

    private String title;//
    private String id;//
    private String whetherUrlAddress;// 是否URL地址，0否，1是；如果是1，那么直接跳转到外链接地址
    private String url;// 外链接地址
    private String pictureAddress;//
    private String updateDate;// 2017-01-01

    //详情
    private String readingQuantity;//阅读数量
    private int isDz; //是否点赞 0否 1是
    private int isCollection; //是否收藏 0否 1是
    private String infoUrl;//内容
    private String enclosureUrl;//附件下载地址
    private String enclosureNmae;//附件名称
    private String filesSize;//附件大小


    public String getFilesSize() {
        return filesSize;
    }

    public void setFilesSize(String filesSize) {
        this.filesSize = filesSize;
    }

    public String getReadingQuantity() {
        return readingQuantity;
    }

    public void setReadingQuantity(String readingQuantity) {
        this.readingQuantity = readingQuantity;
    }

    public int getIsDz() {
        return isDz;
    }

    public void setIsDz(int isDz) {
        this.isDz = isDz;
    }

    public int getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(int isCollection) {
        this.isCollection = isCollection;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getEnclosureUrl() {
        return enclosureUrl;
    }

    public void setEnclosureUrl(String enclosureUrl) {
        this.enclosureUrl = enclosureUrl;
    }

    public String getEnclosureNmae() {
        return enclosureNmae;
    }

    public void setEnclosureNmae(String enclosureNmae) {
        this.enclosureNmae = enclosureNmae;
    }

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

    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
