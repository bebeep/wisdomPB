package com.bebeep.wisdompb.bean;

public class LibraryListEntity {
    private String title;// 书名
    private String id;// 1,
    private String author;// 作者
    private String editorRecommendation;// 介绍
    private String booksCategoryName;// 类别
    private String imgsrc;// 封面图


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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEditorRecommendation() {
        return editorRecommendation;
    }

    public void setEditorRecommendation(String editorRecommendation) {
        this.editorRecommendation = editorRecommendation;
    }

    public String getBooksCategoryName() {
        return booksCategoryName;
    }

    public void setBooksCategoryName(String booksCategoryName) {
        this.booksCategoryName = booksCategoryName;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }
}
