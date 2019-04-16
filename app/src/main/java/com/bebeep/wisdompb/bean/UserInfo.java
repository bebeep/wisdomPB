package com.bebeep.wisdompb.bean;

public class UserInfo {
    private String id;
    private String loginName;// 513330194611300017,
    private String name;// 周永康,
    private int sex;// 性别  1男 2女
    private String photo;// 头像地址
    private String office;// 所属党支部
    private String joiningPartyOrganizationDate;// 入党日期
    private String becomingFullMemberDate;// 转为正式党员日期
    private boolean checked;
    private String partyPosts;//职务
    private String phone;//电话号码
    private String userId;
    private int mettingJurisdictionType; //预约会议权限；0否；1有
    private int activitySignJurisdictionType;//活动签到权限；0否；1有
    private int mettingSignJurisdictionType;//会议签到权限；0否；1有
    private int minutesMeetingType;//发布会议纪要权限；0否；1有
    private String signTime;//签到时间
    private String birthday;//生日
    private String education;//学历
    private String nation;//民族
    private String typeName;//党员类型，预备党员/正式党员
    private String type;//党员类型，预备党员/正式党员
    private String democraticAppraisal;//民族评议


    public int getMinutesMeetingType() {
        return minutesMeetingType;
    }

    public void setMinutesMeetingType(int minutesMeetingType) {
        this.minutesMeetingType = minutesMeetingType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDemocraticAppraisal() {
        return democraticAppraisal;
    }

    public void setDemocraticAppraisal(String democraticAppraisal) {
        this.democraticAppraisal = democraticAppraisal;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPartyPosts() {
        return partyPosts;
    }

    public void setPartyPosts(String partyPosts) {
        this.partyPosts = partyPosts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getJoiningPartyOrganizationDate() {
        return joiningPartyOrganizationDate;
    }

    public void setJoiningPartyOrganizationDate(String joiningPartyOrganizationDate) {
        this.joiningPartyOrganizationDate = joiningPartyOrganizationDate;
    }

    public String getBecomingFullMemberDate() {
        return becomingFullMemberDate;
    }

    public void setBecomingFullMemberDate(String becomingFullMemberDate) {
        this.becomingFullMemberDate = becomingFullMemberDate;
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
}
