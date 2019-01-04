package com.bebeep.wisdompb.bean;

public class SubmitEntity {
    private String title;// 标题
    private String id;// 1"",
    private String content;// 提交的内容
    private String imgsrcs;// 图片的内容
    private String replycontent;// 回复的内容
    private String createDate;// 2017-01-01 11;//11;//11,
    private String updateDate;// 2017-01-01 11;//11;//11


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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgsrcs() {
        return imgsrcs;
    }

    public void setImgsrcs(String imgsrcs) {
        this.imgsrcs = imgsrcs;
    }

    public String getReplycontent() {
        return replycontent;
    }

    public void setReplycontent(String replycontent) {
        this.replycontent = replycontent;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
