package com.bebeep.wisdompb.bean;

/**
 * 书籍详情
 */
public class BookEntity {

    private String title;//标题
    private String id;//1,
    private String author;//作者
    private int wordNumber;//字数
    private int lsItOver;//是否完结
    private String content;//简介
    private String editorRecommendation;//编辑推荐
    private String booksCategoryName;//类别名称,
    private int state;//状态 是否加入书架（0否；1是）
    private String imgsrc;//封面图
    private String bookChaptersId;//第一章id
    private String contentUrl;//书籍下载地址


    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getBookChaptersId() {
        return bookChaptersId;
    }

    public void setBookChaptersId(String bookChaptersId) {
        this.bookChaptersId = bookChaptersId;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getWordNumber() {
        return wordNumber;
    }

    public void setWordNumber(int wordNumber) {
        this.wordNumber = wordNumber;
    }

    public int getLsItOver() {
        return lsItOver;
    }

    public void setLsItOver(int lsItOver) {
        this.lsItOver = lsItOver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }
}
