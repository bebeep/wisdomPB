package com.bebeep.wisdompb.bean;

public class CollectEntity {
    private String id;//1,
    private String themeId;//1,
    private String themeImgsrcs;//20183123/312312.png,
    private String themeTitle;//主题谧钕,
    private int type;//类型，0首页新闻；1专题教育；2党内公示；3 活动；
    private String createDate;//2018-12-12 11;//11;//11


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
