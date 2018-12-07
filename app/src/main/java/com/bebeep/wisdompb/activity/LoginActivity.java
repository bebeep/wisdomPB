package com.bebeep.wisdompb.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.databinding.library.baseAdapters.BR;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.views.CustomDialog;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.LoginEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityLoginBinding;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.HashMap;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{


    private ActivityLoginBinding binding;
    private CustomProgressDialog dialog;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        init();
    }

    private void init(){
        initDialog();
        dialog = new CustomProgressDialog(this);
        binding.setVariable(BR.onClickListener,this);
        binding.title.tvTitle.setText("登录");

        binding.etUsername.setText("51332119750823002X");
        binding.etPassword.setText("23002X");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_login:
                if(checkInput()) login();
                break;
            case R.id.ll_forget://忘记密码
//                if(customDialog!=null) customDialog.show();
//                else initDialog();
                MyTools.showToast(this,"请联系后台管理人员恢复密码");
                break;
        }
    }


    private boolean checkInput(){
        if(TextUtils.isEmpty(binding.etUsername.getText())){
            MyTools.showToast(this,"请输入用户名");
            return false;
        }else if(TextUtils.isEmpty(binding.etPassword.getText())){
            MyTools.showToast(this,"请输入密码");
            return false;
        }
        return true;
    }


    private void login(){
        HashMap map = new HashMap();
        map.put("username", binding.etUsername.getText().toString());
        map.put("password", binding.etPassword.getText().toString());
        OkHttpClientManager.postAsyn(URLS.LOGIN, new OkHttpClientManager.ResultCallback<BaseObject<LoginEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<LoginEntity> response) {
                if(response.isSuccess()){
                    LoginEntity entity = response.getData();
                    PreferenceUtils.setPrefString("access_token",entity.getAccess_token());
                    PreferenceUtils.setPrefString("refresh_token",entity.getRefresh_token());
                    PreferenceUtils.setSettingLong("timestamp", System.currentTimeMillis());
                    getUserInfo(entity.getAccess_token());
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
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<UserInfo> response) {
                Log.e("TAG","getUserInfo json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    UserInfo entity = response.getData();
                    PreferenceUtils.setPrefString("userInfo", MyApplication.gson.toJson(entity));
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }else{
                    MyTools.showToast(LoginActivity.this, response.getMsg());
                }
            }
        },header,map);
    }

    private void initDialog(){
        customDialog = new CustomDialog.Builder(this)
                .setMessage("请联系后台管理人员恢复密码")
                .setSingleButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.cancel();
                    }
                })
                .createSingleButtonDialog();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件

                return false;
            } else {
                //使EditText触发一次失去焦点事件
                v.setFocusable(false);
                v.setFocusableInTouchMode(true);
                return true;
            }
        }
        return false;
    }
}
