package com.bebeep.wisdompb;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.readpage.util.Config;
import com.bebeep.readpage.util.PageFactory;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.bebeep.wisdompb.util.URLS;
import com.google.gson.Gson;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.litepal.LitePalApplication;

import java.io.File;
import java.io.FileOutputStream;

public class MyApplication extends Application {

    public static final int ACTIVITY_BACK_CODE = 9527;
    public static MyApplication instance;
    public static Gson gson;
    public static Context context;
    public static boolean showLog = true;
    public static String pageSize = "20";
    public static String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "wisdomPB/";
    public static String FILE_TEMP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "wisdomPB/temp/";
    public static final String AUTHORIZATION = "Authorization";
    public static final String FILEKEY = "file";
    public static final String ALIAS_NAME = "userId";
    public static final String LOGIN_NAME = "513321197311070085";
    public static final String LOGIN_PASSWORD = "070085";
    public static final String YPAY_PKG_NAME = "com.chinatelecom.bestpayclient";//翼支付的包名


    /**
     *
     打开  首页 0
     打开 政治生日卡 页面   1
     打开 三会一课 页面     2
     打开 活动列表 页面     3
     打开  我提起 页面      4
     打开 在线考试  页面    5
     打开 我的消息 页面     6
     打开 我的发现（自己发布的所有发现）  界面    7
     打开  个人积分明细 界面            8
     打开 专题教育 界面    9
     打开 党内公示 界面    10
     打开 党建相册 界面    11
     ***/

    //123456789

    public static MyApplication getInstance() {
        if(instance == null){
            instance = new MyApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gson = new Gson();
        context = getApplicationContext();
        instance = this;

        File filePath =new File(FILE_PATH);
        File fileTempPath = new File(FILE_TEMP_PATH);
        if(!filePath.exists())filePath.mkdirs();
        if(!fileTempPath.exists())fileTempPath.mkdirs();

        LitePalApplication.initialize(this);
        Config.createConfig(this);
        PageFactory.createPageFactory(this);

        initUmeng();
    }


    private void initUmeng(){
        UMConfigure.init(this, URLS.UMENG_APPKEY, "Umeng", UMConfigure.DEVICE_TYPE_PHONE, URLS.UMENG_MESSAGE_SECRET);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setNotificaitonOnForeground(true);//如果应用在前台的时候，开发者可以自定义配置是否显示通知
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                // AkvqclgrRUHMcM383r1iGqj8EGVX3DOWTqGiGcSLrwbs 真机
                // AlZ8_0-IDFCc0k_4mMvP06LoMxyCk8rYkYCi4sp7S8HS 模拟器 alias 26
                LogUtil.showLog("注册成功：deviceToken：-------->  " + deviceToken);
            }
            @Override
            public void onFailure(String s, String s1) {
                LogUtil.showLog("注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        mPushAgent.setMessageHandler(messageHandler);
    }

    UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
        @Override
        public void dealWithCustomAction(Context context, UMessage msg) {
            LogUtil.showLog("click:"+gson.toJson(msg));
        }
    };


    UmengMessageHandler messageHandler = new UmengMessageHandler() {
        /**
         * 通知的回调方法
         * @param context
         * @param msg
         */
        @Override
        public void dealWithNotificationMessage(Context context, UMessage msg) {
            //调用super则会走通知展示流程，不调用super则不展示通知
            super.dealWithNotificationMessage(context, msg);
            LogUtil.showLog("dealWithNotificationMessage UMessage:"+ gson.toJson(msg));
        }

        @Override
        public Notification getNotification(Context context, UMessage msg) {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
                Notification.Builder builder = new Notification.Builder(context, "channel_id");
                builder.setSmallIcon(R.drawable.icon_logo)
                        .setWhen(System.currentTimeMillis())
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_logo))
                        .setContentTitle(msg.title)
                        .setContentText(msg.text)
                        .setAutoCancel(true);
                return builder.build();
            } else {
                Notification.Builder builder = new Notification.Builder(context);
                builder.setSmallIcon(R.drawable.icon_logo)
                        .setWhen(System.currentTimeMillis())
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_logo))
                        .setContentTitle(msg.title)
                        .setContentText(msg.text)
                        .setAutoCancel(true);
                return builder.build();
            }


//            switch (msg.builder_id) {
//                case 1:
//                    Notification.Builder builder = new Notification.Builder(context);
//                    RemoteViews myNotificationView = new RemoteViews(context.getPackageName(),R.layout.upush_notification);
//                    myNotificationView.setTextViewText(R.id.notification_title, msg.title);
//                    myNotificationView.setTextViewText(R.id.notification_text, msg.text);
//                    myNotificationView.setImageViewBitmap(R.id.notification_large_icon,getLargeIcon(context, msg));
//                    myNotificationView.setImageViewResource(R.id.notification_small_icon,getSmallIconId(context, msg));
//                    builder.setContent(myNotificationView)
//                            .setSmallIcon(getSmallIconId(context, msg))
//                            .setTicker(msg.ticker)
//                            .setAutoCancel(true);
//                    return builder.getNotification();
//                default:
//                    //默认为0，若填写的builder_id并不存在，也使用默认。
//                    return super.getNotification(context, msg);

        }

    };




    //获取用户信息
    public UserInfo getUserInfo(){
        String userJson = PreferenceUtils.getPrefString("userInfo","");
        if(!TextUtils.isEmpty(userJson)){
            return gson.fromJson(userJson,UserInfo.class);
        }
        return new UserInfo();
    }


    //获取access_token
    public String getAccessToken(){
        return PreferenceUtils.getPrefString("access_token","");
    }

    //获取refresh_token
    public String getRefreshToken(){
        return PreferenceUtils.getPrefString("refresh_token","");
    }
}
