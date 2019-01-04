package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.ExamEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityBirthdayCardBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 政治生日卡
 */
public class BirthdayCardActivity extends BaseSlideActivity {

    private ActivityBirthdayCardBinding binding;
    private String startTime = "1970-01-01";
    private String today,userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_birthday_card);
        init();
    }


    private void init(){
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("政治生日卡");
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        startTime = MyApplication.getInstance().getUserInfo().getJoiningPartyOrganizationDate();
        today = MyTools.day_today();
        userName = TextUtils.isEmpty(MyApplication.getInstance().getUserInfo().getName())?"用户":MyApplication.getInstance().getUserInfo().getName();
        setMultiColorText();
//        getData();
    }


    private int getTime(String date,int tag){
        String[] times = date.split("-");
        return Integer.parseInt(times[tag]);
    }

    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("", "");
        OkHttpClientManager.postAsyn(URLS.MY_BIRTHDAY_CARD, new OkHttpClientManager.ResultCallback<BaseList<UserInfo>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<UserInfo> response) {
                LogUtil.showLog("政治生日卡 ："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
//                    UserInfo userInfo = response.getData();
//                    if(userInfo!=null){
//                        startTime = userInfo.getBecomingFullMemberDate();
//                        userName = userInfo.getName();
//                        setMultiColorText();
//                    }
                }else{
                    MyTools.showToast(BirthdayCardActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }


    private void setMultiColorText(){
        long startTimestamp = MyTools.getTimeStamp(startTime,"yyyy-MM-dd");
        long endTimestamp = MyTools.getTimeStamp(today,"yyyy-MM-dd");
        long days = (endTimestamp - startTimestamp) / (1000 * 60 * 60 * 24 );
        String content = userName+"："+getTime(startTime,0)+"年"+getTime(startTime,1)+"月"+getTime(startTime,2)+"日，您加入了中国共产党，今天是"+getTime(today,0)+"年"+getTime(today,1)+"月"+getTime(today,2)+"日，您已成为党员"+days+"天";
        SpannableString spannableString = new SpannableString(content);
        //设置颜色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0F8DD6")), 0, userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//蓝色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#585756")), userName.length(), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//灰色
        binding.tvContent.setText(spannableString);
    }
}
