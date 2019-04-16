package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.VersionEntity;
import com.bebeep.wisdompb.databinding.ActivityUpdatePasswordBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.HashMap;

/**
 * 修改密码
 */
public class UpdatePasswordActivity extends BaseEditActivity {
    private ActivityUpdatePasswordBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_password);
        init();
    }

    private void init(){
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("修改密码");


        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        binding.tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(binding.etPswOld.getText().toString())){
                    MyTools.showToast(UpdatePasswordActivity.this,"请输入旧密码");
                    return;
                }else if(TextUtils.isEmpty(binding.etPswNew1.getText().toString())){
                    MyTools.showToast(UpdatePasswordActivity.this,"请输入新密码");
                    return;
                }else if(TextUtils.isEmpty(binding.etPswNew2.getText().toString())){
                    MyTools.showToast(UpdatePasswordActivity.this,"请再次输入新密码");
                    return;
                }else if(binding.etPswNew1.getText().toString().length()<3 || binding.etPswNew2.getText().toString().length()<3){
                    MyTools.showToast(UpdatePasswordActivity.this,"请输入3-20位以内的密码");
                    return;
                }else if(!TextUtils.equals(binding.etPswNew1.getText().toString(),binding.etPswNew2.getText().toString())){
                    MyTools.showToast(UpdatePasswordActivity.this,"两次密码输入不一致");
                    return;
                }else update();
            }
        });
    }


    private void update(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("currPassword",binding.etPswOld.getText().toString());
        map.put("newsPassword",binding.etPswNew1.getText().toString());
        OkHttpClientManager.postAsyn(URLS.UPDATE_PASSWORD, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("修改密码："+MyApplication.gson.toJson(response));
                MyTools.showToast(UpdatePasswordActivity.this, response.getMsg());
                if(response.isSuccess()){
                    if(response.getErrorCode() == -1)finish();
                }else{
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }
}
