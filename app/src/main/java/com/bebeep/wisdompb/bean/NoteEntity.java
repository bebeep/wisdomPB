package com.bebeep.wisdompb.bean;

public class NoteEntity {
    private String title;
    private String content;
    private String noteContent;
    private boolean choosed;
    private String author;//作者
    private String id;
    private String imgsrc; //封面图
    private String count; //数量

    private String bookChaptersId;// 章节id
    private String bookChaptersName;// 章节名称,
    private String choiceContent;// 选择的笔记内容,  content 填写的笔记内容
    private String color;// red


    public String getBookChaptersId() {
        return bookChaptersId;
    }

    public void setBookChaptersId(String bookChaptersId) {
        this.bookChaptersId = bookChaptersId;
    }

    public String getBookChaptersName() {
        return bookChaptersName;
    }

    public void setBookChaptersName(String bookChaptersName) {
        this.bookChaptersName = bookChaptersName;
    }

    public String getChoiceContent() {
        return choiceContent;
    }

    public void setChoiceContent(String choiceContent) {
        this.choiceContent = choiceContent;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public boolean isChoosed() {
        return choosed;
    }

    public void setChoosed(boolean choosed) {
        this.choosed = choosed;
    }
}
