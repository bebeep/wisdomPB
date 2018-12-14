package com.bebeep.wisdompb.util;

import android.util.Log;

import com.bebeep.wisdompb.MyApplication;

public class LogUtil {

    public static void showLog(String msg){
        if(MyApplication.showLog)Log.e("TAG",msg);
    }

}
