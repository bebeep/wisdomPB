package com.bebeep.commontools.seclectcity.bean;

import java.util.List;

/**
 * Created by Bebeep
 * Time 2018/3/9 15:01
 * Email 424468648@qq.com
 * Tips 字母+国家
 */

public class CharacterCountry {
    private String character; //字母
    private List<CountryInfo> countryList; //国家列表

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public List<CountryInfo> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<CountryInfo> countryList) {
        this.countryList = countryList;
    }
}
