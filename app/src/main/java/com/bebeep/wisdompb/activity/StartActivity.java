package com.bebeep.wisdompb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.LoginEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.HashMap;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        new Handler().postDelayed(new Loading(), 600);
    }

    private class Loading implements Runnable {
        @Override
        public void run() {
            if(TextUtils.isEmpty(MyApplication.getInstance().getAccessToken())){
                startActivity(new Intent().setClass(StartActivity.this,LoginActivity.class));
                finish();
            }else {
                //刷新token
                refreshToken();
            }
        }
    }


    //刷新token
    public void refreshToken(){
        HashMap map = new HashMap();
        map.put("refresh_token", MyApplication.getInstance().getRefreshToken());
        Log.e("TAG","刷新token map="+ map.toString());
        OkHttpClientManager.postAsyn(URLS.REFRESH_TOKEN, new OkHttpClientManager.ResultCallback<BaseObject<LoginEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                startActivity(new Intent().setClass(StartActivity.this,LoginActivity.class));
                finish();
            }
            @Override
            public void onResponse(BaseObject<LoginEntity> response) {
                Log.e("TAG","刷新token json="+ response);
                if(response.isSuccess()){
                    LoginEntity entity = response.getData();
                    PreferenceUtils.setPrefString("access_token",entity.getAccess_token());
                    PreferenceUtils.setPrefString("refresh_token", entity.getRefresh_token());
                    PreferenceUtils.setSettingLong("timestamp", System.currentTimeMillis());
                    getUserInfo(entity.getAccess_token());
                }else{
                    startActivity(new Intent().setClass(StartActivity.this,LoginActivity.class));
                    finish();
                }
            }
        },null,map);
    }

    private void getUserInfo(String token){
        HashMap header = new HashMap();
        header.put("Authorization",token);
        HashMap map = new HashMap();
        map.put("","");
        OkHttpClientManager.postAsyn(URLS.USERINFO, new OkHttpClientManager.ResultCallback<BaseObject<UserInfo>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                startActivity(new Intent().setClass(StartActivity.this,LoginActivity.class));
                finish();
            }
            @Override
            public void onResponse(BaseObject<UserInfo> response) {
                Log.e("TAG","getUserInfo json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    UserInfo entity = response.getData();
                    PreferenceUtils.setPrefString("userInfo", MyApplication.gson.toJson(entity));
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();
                }else{
                    MyTools.showToast(StartActivity.this, response.getMsg());
                    startActivity(new Intent().setClass(StartActivity.this,LoginActivity.class));
                    finish();
                }
            }
        },header,map);
    }




    public  String statusMsg(Exception e, int code){
        if(!MyTools.getNetStatus(this)){ //断网
            return "请检查网络设置";
        }else {
            if(code == 1){
                return "未登录";
            }
//            else if(e.getCause().equals(SocketTimeoutException.class)){ //超时
//                return "请求超时";
//            }
            else{ //其他-"请求错误"
                return "请求错误";
            }
        }
    }
}
