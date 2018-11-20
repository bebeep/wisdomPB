package com.bebeep.commontools.seclectcity.bean;

import java.io.Serializable;

/**
 * Created by Bebeep
 * Time 2018/3/9 14:41
 * Email 424468648@qq.com
 * Tips 国家列表
 */

public class CountryInfo implements Serializable {
    private String uid;
    private String name;
    private String sortLetters;//首字母


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
