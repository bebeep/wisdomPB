package com.bebeep.wisdompb.bean;

import java.util.List;

public class LeaderUserEntity {
    private String partyPosts;
    private List<UserInfo> userList;

    public String getPartyPosts() {
        return partyPosts;
    }

    public void setPartyPosts(String partyPosts) {
        this.partyPosts = partyPosts;
    }

    public List<UserInfo> getUserList() {
        return userList;
    }

    public void setUserList(List<UserInfo> userList) {
        this.userList = userList;
    }
}
