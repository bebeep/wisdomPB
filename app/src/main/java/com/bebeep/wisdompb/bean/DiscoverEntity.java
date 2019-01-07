package com.bebeep.wisdompb.bean;

import java.util.List;

public class DiscoverEntity {
    private String name;// 姓名
    private String id;//
    private String photo;// 头像
    private String title;// 文字
    private String type;// 类型 0视频；1图片；2普通文本
    private String imgsrcs;// 图片地址，多个用英文逗号隔开，type==1时有效
    private String videoUrl;// 视频地址，type==0时有效
    private String createDate;// 发布日期
    private boolean showComment; //是否显示评论
    private String userId;

    private List<CommentEntity> commentList;//评论列表


    public List<CommentEntity> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentEntity> commentList) {
        this.commentList = commentList;
    }

    public boolean isShowComment() {
        return showComment;
    }

    public void setShowComment(boolean showComment) {
        this.showComment = showComment;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgsrcs() {
        return imgsrcs;
    }

    public void setImgsrcs(String imgsrcs) {
        this.imgsrcs = imgsrcs;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
