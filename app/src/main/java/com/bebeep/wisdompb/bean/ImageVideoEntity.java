package com.bebeep.wisdompb.bean;


import java.io.Serializable;

/**
 * 拍照或者录视频时候用来展示
 */
public class ImageVideoEntity implements Serializable{
    private int flag;//1图片（地址）2视频 3置空，用来显示添加图标
    private String url;
    private String videoPath;

    public ImageVideoEntity(int flag) {
        this.flag = flag;
    }

    public ImageVideoEntity(int flag, String url) {
        this.flag = flag;
        this.url = url;
    }

    public ImageVideoEntity(int flag, String url, String videoPath) {
        this.flag = flag;
        this.url = url;
        this.videoPath = videoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
