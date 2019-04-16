package com.bebeep.wisdompb.bean;

import java.util.List;

public class MeetingMinitesEntity {
    private String id;// 1,
    private String imgsrcs;// 2018001231/312312.png,2018001231/312312.png,
    private String name;// 姓名
    private String contents;// 内容,
    private String remarks;//备注,
    private String updateDate;//2018-02-02 11;//11
    private List<UserInfo> userList;//已参与用户列表


    public List<UserInfo> getUserList() {
        return userList;
    }

    public void setUserList(List<UserInfo> userList) {
        this.userList = userList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgsrcs() {
        return imgsrcs;
    }

    public void setImgsrcs(String imgsrcs) {
        this.imgsrcs = imgsrcs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
