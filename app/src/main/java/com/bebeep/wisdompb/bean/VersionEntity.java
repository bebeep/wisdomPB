package com.bebeep.wisdompb.bean;

public class VersionEntity {
    private int versionsort;//1,
    private String versionnono;//1.1,
    private String url;//201312312/312312.apk,
    private String contents;//更新了若干问题


    public int getVersionsort() {
        return versionsort;
    }

    public void setVersionsort(int versionsort) {
        this.versionsort = versionsort;
    }

    public String getVersionnono() {
        return versionnono;
    }

    public void setVersionnono(String versionnono) {
        this.versionnono = versionnono;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
