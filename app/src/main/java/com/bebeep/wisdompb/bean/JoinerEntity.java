package com.bebeep.wisdompb.bean;

import java.util.List;

/**
 * 参会人员
 */
public class JoinerEntity {

    private String id;
    private String title;

    private String name;
    private String photo;

    private boolean checked;
    private boolean showChild;

    private List<JoinerEntity> childrens; //包含childrens id title userList
    private List<JoinerEntity> userList;   //包含id name photo


    public boolean isShowChild() {
        return showChild;
    }

    public void setShowChild(boolean showChild) {
        this.showChild = showChild;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public List<JoinerEntity> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<JoinerEntity> childrens) {
        this.childrens = childrens;
    }

    public List<JoinerEntity> getUserList() {
        return userList;
    }

    public void setUserList(List<JoinerEntity> userList) {
        this.userList = userList;
    }
}
