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
}
