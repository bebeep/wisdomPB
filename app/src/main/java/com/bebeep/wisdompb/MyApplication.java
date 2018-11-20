package com.bebeep.wisdompb;

import android.app.Application;
import android.content.Context;

import com.bebeep.commontools.utils.MyTools;
import com.google.gson.Gson;

public class MyApplication extends Application {


    public static MyApplication instance;
    public static Gson gson;
    public static Context context;

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
        context = this;
    }



    /**
     * 接口请求错误时，判断各种情况
     * @param e
     */
    public static void statusMsg(Exception e,int code){
        if(!MyTools.getNetStatus(context)){ //断网
            MyTools.showToast(context,"网络已断开");
        }else {
            if(code == 1){
                MyTools.showToast(context,"解析错误");
            }else{ //其他-"请求错误"
                MyTools.showToast(context,"请求错误");
            }
        }
    }
}
