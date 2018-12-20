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

    //上传文件
    public static final String UPLOAD = FILE_UPLOAD + "/file";


    /**
     * 首页
     */
    //广告
    public static final String ADS =  HOST + "/gd/data";

    //获取新闻类型
    public static final String HOST_TYPE =  HOST + "/home/news/typedata";

    //获取新闻列表
    public static final String HOST_LIST =  HOST + "/home/news/data";

    //获取新闻详情
    public static final String HOST_DETAILS =  HOST + "/home/news/info";

    //获取新闻评论
    public static final String HOST_COMMENT =  HOST + "/home/news/comment/getChildren";

    //提交新闻评论
    public static final String HOST_COMMENT_SUBMIT =  HOST + "/home/news/comment/release";

    //删除新闻评论
    public static final String HOST_COMMENT_DELETE =  HOST + "/home/news/comment/delete";

    //点赞
    public static final String HOST_COMMENT_ZAN =  HOST + "/home/news/giveUp";

    //收藏
    public static final String HOST_COMMENT_COLLECT =  HOST + "/home/news/collection";




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

    //获取新闻详情
    public static final String SPECIAL_EDU_DETAILS =  HOST + "/thematiceducation/news/info";

    //获取新闻评论
    public static final String SPECIAL_EDU_COMMENT =  HOST + "/thematiceducation/news/comment/getChildren";

    //提交新闻评论
    public static final String SPECIAL_EDU_COMMENT_SUBMIT =  HOST + "/thematiceducation/news/comment/release";

    //删除新闻评论
    public static final String SPECIAL_EDU_COMMENT_DELETE =  HOST + "/thematiceducation/news/comment/delete";

    //点赞
    public static final String SPECIAL_EDU_COMMENT_ZAN =  HOST + "/thematiceducation/news/giveUp";

    //收藏
    public static final String SPECIAL_EDU_COMMENT_COLLECT =  HOST + "/thematiceducation/news/collection";

    /**
     * 党内公示
     */
    //党内公示类型
    public static final String PUBLIC_SHOW_TYPE = HOST + "";

    //党内公示列表
    public static final String PUBLIC_SHOW_LIST = HOST + "/within/news/data";

    //党内公示详情
    public static final String PUBLIC_SHOW_DETAILS =  HOST + "/within/news/info";

    //获取党内公示评论
    public static final String PUBLIC_SHOW_COMMENT =  HOST + "/within/news/comment/getChildren";

    //提交党内公示评论
    public static final String PUBLIC_SHOW_COMMENT_SUBMIT =  HOST + "/within/news/comment/release";

    //删除党内公示评论
    public static final String PUBLIC_SHOW_COMMENT_DELETE =  HOST + "/within/news/comment/delete";

    //点赞
    public static final String PUBLIC_SHOW_COMMENT_ZAN =  HOST + "/within/news/giveUp";

    //收藏
    public static final String PUBLIC_SHOW_COMMENT_COLLECT =  HOST + "/within/news/collection";




    /**
     * 在线考试
     */
    //待考列表
    public static final String EXAM_LIST = HOST + "/itembank/tested_data";

    //考试详情
    public static final String EXAM_DETAILS = HOST + "/itembank/info";

    //获取题目信息
    public static final String EXAM_TESTING = HOST + "/itembank/startAnswer";

    /**
     * 三会一课
     */
    //会议列表
    public static final String MEETING_LIST = HOST + "/meeting/data";

    //会议列表
    public static final String MEETING_DETAILS = HOST + "/meeting/info";

    //我的会议
    public static final String MY_MEETING = HOST + "/meeting/my/data";

    //预约会议
    public static final String MEETING_ORDER = HOST + "/meeting/insert";

    //预约会议
    public static final String MEETING_GET_MEMBER = HOST + "/meeting/getOfficeData";

    //请假
    public static final String MEETING_LEAVE = HOST + "/meeting/leave/insert";

    //参加
    public static final String MEETING_JOIN = HOST + "/meeting/participate";


    /**
     * 发现
     */
    //列表
    public static final String DISCOVER_LIST = HOST + "/crcle/friends/data";

    //发布
    public static final String DISCOVER_RELEASE = HOST + "/crcle/friends/release";

    //获取评论
    public static final String DISCOVER_COMMENT_GET = HOST + "/crclefriends/comment/getChildren";

    //删除评论
    public static final String DISCOVER_COMMENT_DELETE = HOST + "/crclefriends/comment/delete";

    //提交评论
    public static final String DISCOVER_COMMENT_SUBMIT = HOST + "/crclefriends/comment/release";
}
