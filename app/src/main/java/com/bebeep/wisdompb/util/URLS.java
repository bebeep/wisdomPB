package com.bebeep.wisdompb.util;

public class URLS {


    public static final String UMENG_APPKEY = "5c3551cdf1f5569920000489";

    public static final String UMENG_MESSAGE_SECRET = "93199e9ce2e1b51f4748216c1055873b";

//    public static final String PAY_URLS = "http://u6.gg/dEVKs";
    public static final String PAY_URLS = "https://a.app.qq.com/o/simple.jsp?pkgname=com.chinatelecom.bestpayclient&g_f=991653";


    /**
     * 甘孜机关党建
     */
    //数据地址
    public static final String  HOST = "http://140.246.178.64:400/api";
    //图片上传地址
    public static final String FILE_UPLOAD = "http://140.246.161.230:7070/upload";
    //图片访问地址
    public static final String IMAGE_PRE  = "http://140.246.161.230:400/resource/";

    /**
     * 甘孜县智慧党建
     */
//    //数据地址
//    public static final String  HOST = "http://140.246.151.222:400/api";
//    //图片上传地址
//    public static final String FILE_UPLOAD = "http://140.246.151.222:7070/upload";
//    //图片访问地址
//    public static final String IMAGE_PRE  = "http://140.246.151.222:400/resource/";


    //刷新token
    public static final String REFRESH_TOKEN =  HOST + "/refresh_token";

    //登录
    public static final String LOGIN =  HOST + "/login";

    //用户信息
    public static final String USERINFO =  HOST + "/user/info";

    //修改头像
    public static final String UPDATE_HEAD =  HOST + "/user/updatePhoto";

    //上传文件
    public static final String UPLOAD = FILE_UPLOAD + "/file";



    //获取评论
    public static final String COMMENT_GET =  HOST + "/comment/getChildren";

    //提交评论
    public static final String COMMENT_SUBMIT =  HOST + "/comment/release";

    //删除评论
    public static final String COMMENT_DEL =  HOST + "/comment/delete";

    //点赞
    public static final String ZAN =  HOST + "/give/giveUp";

    //收藏
    public static final String COLLECT =  HOST + "/collection/insert";

    //关于我们
    public static final String ABOUT =  HOST + "/about/preview?id=1";

    //免责声明
    public static final String NOTICE =  HOST + "/about/preview?id=2";

    //版本更新
    public static final String VERSION =  HOST + "/version?type=0";

    //党费缴纳跳转
    public static final String CHARGE =  "http://u6.gg/dEVKs";

    //消息数量
    public static final String NEWS_NUM =  HOST + "/notice/announcement/my/read/count";

    //消息已读
    public static final String NEWS_READ =  HOST + "/notice/announcement/my/edit/read";

    //弹窗消息
    public static final String HOST_DIALOG =  HOST + "/notice/announcement/my/msg";



    /**
     * 首页
     */
    //广告
    public static final String ADS =  HOST + "/gd/data";

    //广告详情
    public static final String ADS_DETAILS =  HOST + "/gd/info";

    //获取新闻类型
    public static final String HOST_TYPE =  HOST + "/home/news/typedata";

    //获取新闻列表
    public static final String HOST_LIST =  HOST + "/home/news/data";

    //获取新闻详情
    public static final String HOST_DETAILS =  HOST + "/home/news/info";


    /**
     * 个人中心
     */
    //我的书架
    public static final String MY_BOOK =  HOST + "/books/my/booksdata";

    //我的书架
    public static final String MY_BOOK_DEL =  HOST + "/books/bookshelf/cancel";

    //我的笔记
    public static final String MY_NOTE_LIST =  HOST + "/books/chapters/note/my/note";

    //我的笔记-子列表
    public static final String MY_NOTE_DETAILLIST =  HOST + "/books/chapters/note/my/note/list";

    //我的积分-获取我的个人积分和排名
    public static final String MY_JIFEN_DETAIL_PERSON =  HOST + "/user/ranking/rownum";

    //我的积分-获取我的支部积分和排名
    public static final String MY_JIFEN_DETAIL_BRANCH =  HOST + "/office/ranking/rownum";

    //我的积分-个人积分排行榜
    public static final String MY_JIFEN_PERSONAL =  HOST + "/user/ranking/list";

    //我的积分-支部积分排行榜
    public static final String MY_JIFEN_BRANCH =  HOST + "/office/ranking/list";

    //我的积分-个人积分明细
    public static final String MY_JIFEN_DETAILS1 =  HOST + "/integral/record/my/data";

    //我的积分-党支部积分明细
    public static final String MY_JIFEN_DETAILS2 =  HOST + "/integral/record/office/data";

    //意见反馈
    public static final String MY_TICKING =  HOST + "/feedback/save";

    //政治生日卡
    public static final String MY_BIRTHDAY_CARD = HOST + "/politics/birthday/card/my/data";

    //我提交的
    public static final String MY_SUBMIT = HOST + "/feedback/data";

    //我的活动
    public static final String MY_ACT = HOST + "/office/activity/my/data";

    //我的评论
    public static final String MY_COMMENT = HOST + "/comment/my/comment";

    //我的收藏
    public static final String MY_COLLECT = HOST + "/collection/my/coll/list";

    //通知公告
    public static final String MY_NOTICE = HOST + "/notice/announcement/my/data";

    //删除通知公告
    public static final String MY_NOTICE_DEL = HOST + "/notice/announcement/delete";

    //修改密码
    public static final String UPDATE_PASSWORD = HOST + "/user/update/password";


    /**
     * 图书馆
     */
    //图书类型
    public static final String LIBRARY_TYPE = HOST + "/books/categorydata";

    //图书列表
    public static final String LIBRARY_LIST = HOST + "/books/data";

    //图书详情
    public static final String LIBRARY_BOOK_DETAILS = HOST + "/books/info";

    //加入书架
    public static final String LIBRARY_BOOK_ADD = HOST + "/books/bookshelf/insert";

    //目录列表
    public static final String LIBRARY_CATALOG_LIST = HOST + "/books/getBookChaptersData";

    //章节内容
    public static final String LIBRARY_CATALOG_CONTENT = HOST + "/books/getBookChaptersInfo";

    /**
     * 党组织活动
     */
    //党组织详情
    public static final String ORG_DETAILS = HOST + "/office/info";

    //党组织活动列表
    public static final String ORG_ACT_LIST = HOST + "/office/activity/data";

    //党组织活动详情
    public static final String ORG_ACT_DETAILS = HOST + "/office/activity/info";

    //参加活动
    public static final String ORG_ACT_JOIN = HOST + "/office/activity/participate";




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

    //广告详情
    public static final String SPECIAL_EDU_ADS_DETAILS =  HOST + "/thematiceducation/gd/info";

    /**
     * 党内公示
     */
    //党内公示类型
    public static final String PUBLIC_SHOW_TYPE = HOST + "";

    //党内公示列表
    public static final String PUBLIC_SHOW_LIST = HOST + "/within/news/data";

    //党内公示详情
    public static final String PUBLIC_SHOW_DETAILS =  HOST + "/within/news/info";


    /**
     * 党费缴纳
     */
    //缴费详情
    public static final String PARTY_PAY_DETAILS = HOST + "/apply/info";

    //获取支付签名
    public static final String PARTY_PAY_SIGN = HOST + "/apply/sign";

    //获取缴费记录
    public static final String PARTY_PAY_RECORD = HOST + "/apply/my/record";



    /**
     * 党建通讯录
     */
    //列表
    public static final String ADDRESSBOOK_LIST = HOST + "/user/party/construction/address/book";

    //详情
    public static final String ADDRESSBOOK_DETAILS = HOST + "/user/party/construction/address/book/info";

    /**
     * 党建相册
     */
    //列表
    public static final String PHOTO_LIST = HOST + "/album/data";

    //详情
    public static final String PHOTO_DETAILS = HOST + "/album/info";


    /**
     * 在线考试
     */
    //我的考试
    public static final String EXAM_MY_LIST = HOST + "/itembank/my/data";

    //待考列表
    public static final String EXAM_LIST = HOST + "/itembank/tested_data";

    //已考列表
    public static final String EXAMED_LIST = HOST + "/itembank/have_data";

    //考试详情
    public static final String EXAM_DETAILS = HOST + "/itembank/info";

    //获取题目信息
    public static final String EXAM_TESTING = HOST + "/itembank/startAnswer";

    //验证答案
    public static final String EXAM_CHECK_ANSWER = HOST + "/itembank/verifytheanswer";

    //获取考试结果
    public static final String EXAM_RESULT = HOST + "/itembank/calculation";


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

    //会议纪要
    public static final String MEETING_MINUTES = HOST + "/meeting/my/data";

    //会议纪要详情
    public static final String MEETING_MINUTES_DETAILS = HOST + "/meeting/summary/info";

    //获取二维码
    public static final String MEETING_GET_QRCODE = HOST + "/sign/get/news/qr";

    //签到列表-会议
    public static final String MEETING_SIGN_LIST = HOST + "/meeting/sign/list";

    //签到列表-活动
    public static final String ACT_SIGN_LIST = HOST + "/office/activity/sign/list";

    //签到
    public static final String ACT_SIGN = HOST + "/sign/scan";

    //获取会议纪要编辑内容
    public static final String MEETING_MINITES_EDIT = HOST + "/meeting/summary/getMeetingSummary";

    //发布会议纪要
    public static final String MEETING_MINITES_RELEASE = HOST + "/meeting/summary/insert";


    /**
     * 发现
     */
    //列表
    public static final String DISCOVER_LIST = HOST + "/crcle/friends/data";

    //发布
    public static final String DISCOVER_RELEASE = HOST + "/crcle/friends/release";

    //删除
    public static final String DISCOVER_DELETE = HOST + "/crcle/friends/delete";

}
