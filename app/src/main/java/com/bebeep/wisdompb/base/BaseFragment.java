package com.bebeep.wisdompb.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.wisdompb.activity.MainActivity;

import java.net.SocketTimeoutException;

public class BaseFragment extends Fragment {
    public MainActivity mainActivity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }




    public  String statusMsg(Exception e, int code){
        if(!MyTools.getNetStatus(getActivity())){ //断网
            return "请检查网络设置";
        }else {
            if(code == 1){
                return "未登录";
            }
            else if(e.getCause().equals(SocketTimeoutException.class)){ //超时
                return "请求超时";
            }
            else{ //其他-"请求错误"
                return "请求错误";
            }
        }
    }
}
