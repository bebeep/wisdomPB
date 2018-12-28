package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityMemberInfoBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.HashMap;


/**
 * 党员档案
 */
public class MemberInfoActivity extends BaseSlideActivity implements View.OnClickListener{
    private ActivityMemberInfoBinding binding;

    private String id;
    private UserInfo userInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_member_info);
        init();
    }

    private void init(){
        id = getIntent().getStringExtra("id");
        getData();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("党员档案");
        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setImageResource(R.drawable.icon_menu7);
    }

    private void initUI(){
        if(userInfo == null) return;
        PicassoUtil.setImageUrl(this,binding.rimgHead, URLS.IMAGE_PRE+userInfo.getPhoto(),R.drawable.icon_head,60,60);
        binding.tvName.setText(userInfo.getName());
        binding.tvPhoneNum.setText(userInfo.getPhone());
        binding.ivSex.setImageResource(userInfo.getSex() == 1?R.drawable.icon_sex_man:R.drawable.icon_sex_woman);
        binding.tvBranch.setText(userInfo.getOffice());
        binding.tvPosition.setText(userInfo.getPartyPosts());
        binding.tvTime1.setText(userInfo.getJoiningPartyOrganizationDate());
        binding.tvTime2.setText(userInfo.getBecomingFullMemberDate());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_title_right:
                MyTools.showToast(MemberInfoActivity.this,"查看自己资料");
                break;
            case R.id.fl_head://修改头像

                break;
            case R.id.tv_dial://拨打电话
                if(TextUtils.isEmpty(userInfo.getPhone())){
                    MyTools.showToast(this,"无效的电话号码");
                    return;
                }
                call(userInfo.getPhone());
                break;
        }
    }



    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("id",id);
        LogUtil.showLog("header:"+header);
        LogUtil.showLog("map:"+map);
        OkHttpClientManager.postAsyn(URLS.ADDRESSBOOK_DETAILS, new OkHttpClientManager.ResultCallback<BaseObject<UserInfo>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<UserInfo> response) {
                LogUtil.showLog("通讯录详情："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    userInfo = response.getData();
                    initUI();
                }else{
                    MyTools.showToast(MemberInfoActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }


    private void call(String phoneNum){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }
}
