package com.bebeep.wisdompb.bean;

public class GalleryEntity {
    private String title;// 43242342423,
    private String id;// 1,
    private String isDz;// 1,
    private String isCollection;// 1,
    private String imgsrcs;// 20181205/3123.png,
    private String imgsrc;// 20181205/3123.png,
    private String imgSize;
    private String readingQuantity;// 0,
    private String createDate;// 2017-01-01
    private String dzQuantity;//点赞数量


    public String getDzQuantity() {
        return dzQuantity;
    }

    public void setDzQuantity(String dzQuantity) {
        this.dzQuantity = dzQuantity;
    }

    public String getImgsrcs() {
        return imgsrcs;
    }

    public void setImgsrcs(String imgsrcs) {
        this.imgsrcs = imgsrcs;
    }

    public String getImgSize() {
        return imgSize;
    }

    public void setImgSize(String imgSize) {
        this.imgSize = imgSize;
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

    public String getIsDz() {
        return isDz;
    }

    public void setIsDz(String isDz) {
        this.isDz = isDz;
    }

    public String getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(String isCollection) {
        this.isCollection = isCollection;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getReadingQuantity() {
        return readingQuantity;
    }

    public void setReadingQuantity(String readingQuantity) {
        this.readingQuantity = readingQuantity;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
