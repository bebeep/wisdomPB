package com.bebeep.wisdompb.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.LoginEntity;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;
import com.umeng.message.PushAgent;

import java.net.SocketTimeoutException;
import java.util.HashMap;

public class BaseSlideActivity extends SlideBackActivity {

    private OnRefreshTokenListener onRefreshTokenListener;

    public interface OnRefreshTokenListener{
        void onRefreshTokenSuccess();
        void onRefreshTokenFail();
    }

    public void setOnRefreshTokenListener(OnRefreshTokenListener onRefreshTokenListener) {
        this.onRefreshTokenListener = onRefreshTokenListener;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();
    }

    public  String statusMsg(Exception e, int code){
        if(!MyTools.getNetStatus(this)){ //断网
            MyTools.showToast(this, "请检查网络设置");
            return "请检查网络设置";
        }else {
            if(code == 1){
//                MyTools.showToast(this, "未登录");
                return "未登录";
            }
//            else if(e.getCause().equals(SocketTimeoutException.class)){ //超时
//                return "请求超时";
//            }
            else{ //其他-"请求错误"
                MyTools.showToast(this, "请求错误");
                return "请求错误";
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
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<LoginEntity> response) {
                Log.e("TAG","刷新token json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    LoginEntity entity = response.getData();
                    PreferenceUtils.setPrefString("access_token",entity.getAccess_token());
                    PreferenceUtils.setPrefString("refresh_token", entity.getRefresh_token());
                    PreferenceUtils.setSettingLong("timestamp", System.currentTimeMillis());
                    if(onRefreshTokenListener!=null) onRefreshTokenListener.onRefreshTokenSuccess();
                }else{
                    if(onRefreshTokenListener!=null) onRefreshTokenListener.onRefreshTokenFail();
                }
            }
        },null,map);
    }
}
