package com.bebeep.wisdompb.bean;

/**
 * 目录
 */

public class CatalogEntity {

    /**
     * 名字
     */
    private String name;

    /**
     * item type
     */
    private int itemType;

    public CatalogEntity(String name, int itemType) {
        this.name = name;
        this.itemType = itemType;
    }

    public CatalogEntity(String name) {
      this(name, 11);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

}
