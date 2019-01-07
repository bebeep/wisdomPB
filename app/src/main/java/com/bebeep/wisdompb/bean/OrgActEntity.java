package com.bebeep.wisdompb.bean;

import java.util.List;

/**
 * 党组织活动
 */
public class OrgActEntity {
    private String id;// 1,
    private String title;// 标题
    private String imgsrc;// 头像
    private String infoUrl;// 详情页
    private String numberPartyMembers;// 党员数量
    private String activityQuantity;// 活动数量

    private String startTime;// 开始时间
    private String endTime;// 结束时间
    private String imgsrcs;// 图片地址
    private String readingQuantity;// 阅读数量
    private String dzQuantity;//点赞数量
    private String isDz;// 是否点赞 0否1是
    private String isParticipate;// 是否参加 0否1是
    private String isCollection;// 是否收藏 0否1是

    private String activityId;//活动id
    private int mettingJurisdictionType; //预约会议权限；0否；1有
    private int activitySignJurisdictionType;//活动签到权限；0否；1有
    private int mettingSignJurisdictionType;//会议签到权限；0否；1有


    private List<UserInfo> activityFoUserBizList;


    public List<UserInfo> getActivityFoUserBizList() {
        return activityFoUserBizList;
    }

    public void setActivityFoUserBizList(List<UserInfo> activityFoUserBizList) {
        this.activityFoUserBizList = activityFoUserBizList;
    }


    public int getMettingJurisdictionType() {
        return mettingJurisdictionType;
    }

    public void setMettingJurisdictionType(int mettingJurisdictionType) {
        this.mettingJurisdictionType = mettingJurisdictionType;
    }

    public int getActivitySignJurisdictionType() {
        return activitySignJurisdictionType;
    }

    public void setActivitySignJurisdictionType(int activitySignJurisdictionType) {
        this.activitySignJurisdictionType = activitySignJurisdictionType;
    }

    public int getMettingSignJurisdictionType() {
        return mettingSignJurisdictionType;
    }

    public void setMettingSignJurisdictionType(int mettingSignJurisdictionType) {
        this.mettingSignJurisdictionType = mettingSignJurisdictionType;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getDzQuantity() {
        return dzQuantity;
    }

    public void setDzQuantity(String dzQuantity) {
        this.dzQuantity = dzQuantity;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getImgsrcs() {
        return imgsrcs;
    }

    public void setImgsrcs(String imgsrcs) {
        this.imgsrcs = imgsrcs;
    }

    public String getReadingQuantity() {
        return readingQuantity;
    }

    public void setReadingQuantity(String readingQuantity) {
        this.readingQuantity = readingQuantity;
    }

    public String getIsDz() {
        return isDz;
    }

    public void setIsDz(String isDz) {
        this.isDz = isDz;
    }

    public String getIsParticipate() {
        return isParticipate;
    }

    public void setIsParticipate(String isParticipate) {
        this.isParticipate = isParticipate;
    }

    public String getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(String isCollection) {
        this.isCollection = isCollection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getNumberPartyMembers() {
        return numberPartyMembers;
    }

    public void setNumberPartyMembers(String numberPartyMembers) {
        this.numberPartyMembers = numberPartyMembers;
    }

    public String getActivityQuantity() {
        return activityQuantity;
    }

    public void setActivityQuantity(String activityQuantity) {
        this.activityQuantity = activityQuantity;
    }
}
