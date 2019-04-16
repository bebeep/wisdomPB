package com.bebeep.wisdompb.bean;

import java.util.List;

public class MeetingEntity {
    private String id;
    private String startTime;
    private String endTime;
    private String address;
    private String theme;
    private String week;//星期几
    private String state;//状态 0 参加，1 请假  || 状态，0参加；1请假，默认为空；如果值为0，则提示已参加，按钮不能点击；如果为1，同上；如果为空，可以报名或者请假
    private String issue;//议题
    private String requirement;//会议要求
    private String enclosureUrl;//附件下载地址
    private String enclosureNmae;//附件名称，有就显示；没有则隐藏，
    private String participateNum;//参加人数
    private String leaveNum;//请假人数
    private int mettingJurisdictionType; //预约会议权限；0否；1有
    private int activitySignJurisdictionType;//活动签到权限；0否；1有
    private int mettingSignJurisdictionType;//会议签到权限；0否；1有
    private int minutesMeetingType;//会议纪要权限 0否 1有
    private List<UserInfo> meetingInFoUserBizList;//参与用户列表
    private int ifRecord;//会议纪要列表中是否已记录 0未记录；1已记录


    public int getMinutesMeetingType() {
        return minutesMeetingType;
    }

    public void setMinutesMeetingType(int minutesMeetingType) {
        this.minutesMeetingType = minutesMeetingType;
    }

    public int getIfRecord() {
        return ifRecord;
    }

    public void setIfRecord(int ifRecord) {
        this.ifRecord = ifRecord;
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

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getEnclosureUrl() {
        return enclosureUrl;
    }

    public void setEnclosureUrl(String enclosureUrl) {
        this.enclosureUrl = enclosureUrl;
    }

    public String getEnclosureNmae() {
        return enclosureNmae;
    }

    public void setEnclosureNmae(String enclosureNmae) {
        this.enclosureNmae = enclosureNmae;
    }

    public String getParticipateNum() {
        return participateNum;
    }

    public void setParticipateNum(String participateNum) {
        this.participateNum = participateNum;
    }

    public String getLeaveNum() {
        return leaveNum;
    }

    public void setLeaveNum(String leaveNum) {
        this.leaveNum = leaveNum;
    }

    public List<UserInfo> getMeetingInFoUserBizList() {
        return meetingInFoUserBizList;
    }

    public void setMeetingInFoUserBizList(List<UserInfo> meetingInFoUserBizList) {
        this.meetingInFoUserBizList = meetingInFoUserBizList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
