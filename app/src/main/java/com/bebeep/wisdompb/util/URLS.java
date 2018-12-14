package com.bebeep.wisdompb.util;

public class URLS {


    //数据地址
    public static final String  HOST = "http://203.6.227.248:400/api";

    //图片上传地址
    public static final String FILE_UPLOAD = "http://203.6.227.248:7070/upload";

    //图片访问地址
    public static final String IMAGE_PRE  = "http://203.6.227.248:400/resource/";


    //刷新token
    public static final String REFRESH_TOKEN =  HOST + "/refresh_token";

    //登录
    public static final String LOGIN =  HOST + "/login";

    //用户信息
    public static final String USERINFO =  HOST + "/user/info";

    //广告
    public static final String ADS =  HOST + "/gd/data";

    //上传文件
    public static final String UPLOAD = FILE_UPLOAD + "/file";


    /**
     * 图书馆
     */
    //图书类型
    public static final String LIBRARY_TYPE = HOST + "/books/categorydata";
    //图书列表
    public static final String LIBRARY_LIST = HOST + "/books/data";


    /**
     * 专题教育
     */
    //专题教育类型
    public static final String SPECIAL_EDU_TYPE = HOST + "/thematiceducation/news/typedata";
    //轮播图
    public static final String SPECIAL_EDU_ADS = HOST + "/thematiceducation/gd/data";
    //专题教育列表
    public static final String SPECIAL_EDU_LIST = HOST + "/thematiceducation/news/data";


    /**
     * 发现
     */
    //列表
    public static final String DISCOVER_LIST = HOST + "/crcle/friends/data";

    //发布
    public static final String DISCOVER_RELEASE = HOST + "/crcle/friends/release";

    //获取评论
    public static final String DISCOVER_COMMENT_GET = HOST + "/crclefriends/comment/getChildren";

    //提交评论
    public static final String DISCOVER_COMMENT_SUBMIT = HOST + "/crclefriends/comment/release";
}
