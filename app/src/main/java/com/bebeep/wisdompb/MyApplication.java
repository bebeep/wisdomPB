package com.bebeep.wisdompb;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.google.gson.Gson;

import java.io.File;

public class MyApplication extends Application {

    public static final int ACTIVITY_BACK_CODE = 9527;
    public static MyApplication instance;
    public static Gson gson;
    public static Context context;
    public static boolean showLog = true;
    public static String pageSize = "20";
    public static String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "wisdomPB/";
    public static final String AUTHORIZATION = "Authorization";
    public static final String FILEKEY = "file";

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
    }


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
