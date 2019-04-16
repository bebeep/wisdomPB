package com.bebeep.wisdompb.bean;

/**
 * 评论
 */
public class CommentEntity {
    private String crcleFriendsId;//发现id
    private String id;//评论id
    private String content;//评论内容
    private String userId;//评论者用户id
    private String name;//评论人名字
    private String photo;//评论人头像
    private String repliedUserId;//被回复的id；
    private String repliedUserName;//被回复的用户姓名
    private String createDate;//评论时间
    private String themeId;//主题id
    private String themeImgsrcs; //缩略图
    private String themeTitle; //标题
    private String type; //主题类；0首页新闻；1专题教育；2党内公示；3发现;4活动;5图书评论


    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public String getThemeImgsrcs() {
        return themeImgsrcs;
    }

    public void setThemeImgsrcs(String themeImgsrcs) {
        this.themeImgsrcs = themeImgsrcs;
    }

    public String getThemeTitle() {
        return themeTitle;
    }

    public void setThemeTitle(String themeTitle) {
        this.themeTitle = themeTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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
