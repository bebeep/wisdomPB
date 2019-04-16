package com.bebeep.wisdompb.bean;

public class NoticeEntity {
    private String typeTitle;// 会议通知,
    private String id;// 1,
    private String content;// 有会议信息发布，注意查收,
    private int type;//类型；0后台发布；2党费提醒；3会议（点击跳转到 三会一课）；4活动（点击跳转到 活动列表）；
                         // 5政治生日卡提醒（点击跳转到 政治生日卡）；6意见反馈回复通知（点击条跳转到 我提前的）；7考试通知（点击跳转到 在线考试）；
    private String url;// 外链接地址，type=0时有效，如果url不为空，则打开网页；
    private String createDate;//
    private int ifRead;//是否已读 0否 1是


    public int getIfRead() {
        return ifRead;
    }

    public void setIfRead(int ifRead) {
        this.ifRead = ifRead;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
