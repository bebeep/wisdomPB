package com.bebeep.wisdompb.bean;

/**
 * 评论
 */
public class CommentEntity {
    private String crcleFriendsId;//发现id
    private String id;//评论id
    private String content;//评论内容
    private String name;//评论人名字
    private String photo;//评论人头像
    private String repliedUserId;//被回复的id；
    private String repliedUserName;//被回复的用户姓名
    private String createDate;//评论时间


    public String getCrcleFriendsId() {
        return crcleFriendsId;
    }

    public void setCrcleFriendsId(String crcleFriendsId) {
        this.crcleFriendsId = crcleFriendsId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getRepliedUserId() {
        return repliedUserId;
    }

    public void setRepliedUserId(String repliedUserId) {
        this.repliedUserId = repliedUserId;
    }

    public String getRepliedUserName() {
        return repliedUserName;
    }

    public void setRepliedUserName(String repliedUserName) {
        this.repliedUserName = repliedUserName;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
