package com.bebeep.wisdompb.activity;

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
import com.bebeep.wisdompb.base.BaseAppCompatActivity;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.LoginEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityLoginBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import java.util.HashMap;

/**
 * 登录
 */
public class LoginActivity extends BaseEditActivity implements View.OnClickListener{


    private ActivityLoginBinding binding;
    private CustomProgressDialog dialog;
    private CustomDialog customDialog;

    private int tag = 0;//0跳转到首页，1直接关闭页面

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog = new CustomProgressDialog(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        init();
    }

    private void init(){

        initDialog();
        tag = getIntent().getIntExtra("tag",0);
        dialog = new CustomProgressDialog(this);
        binding.setVariable(BR.onClickListener,this);
        binding.title.tvTitle.setText("登录");

//        binding.etUsername.setText(MyApplication.LOGIN_NAME);
//        binding.etPassword.setText(MyApplication.LOGIN_PASSWORD);
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
//                MyTools.showToast(this,"请联系后台管理人员恢复密码");
                startActivity(new Intent(this,ForgetPasswordActivity.class));
                break;
        }
    }


    private boolean checkInput(){
        if(TextUtils.isEmpty(binding.etUsername.getText())){
            MyTools.showToast(this,"请输入准确的身份证号");
            return false;
        }else if(TextUtils.isEmpty(binding.etPassword.getText())){
            MyTools.showToast(this,"请输入密码");
            return false;
        }
        return true;
    }


    private void login(){
        dialog.show();
        HashMap map = new HashMap();
        map.put("username", binding.etUsername.getText().toString());
        map.put("password", binding.etPassword.getText().toString());
        OkHttpClientManager.postAsyn(URLS.LOGIN, new OkHttpClientManager.ResultCallback<BaseObject<LoginEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                dialog.cancel();
                MyTools.showToast(LoginActivity.this, "无法连接服务器");
            }
            @Override
            public void onResponse(BaseObject<LoginEntity> response) {
                LogUtil.showLog("登录："+response);
                if(response.isSuccess()){
                    LoginEntity entity = response.getData();
                    PreferenceUtils.setPrefString("access_token",entity.getAccess_token());
                    PreferenceUtils.setPrefString("refresh_token",entity.getRefresh_token());
                    PreferenceUtils.setSettingLong("timestamp", System.currentTimeMillis());
                    getUserInfo(entity.getAccess_token());
                }else  {
                    dialog.cancel();
                    MyTools.showToast(LoginActivity.this, response.getMsg());
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
                MyTools.showToast(LoginActivity.this, "获取用户信息失败");
                dialog.cancel();
            }
            @Override
            public void onResponse(BaseObject<UserInfo> response) {
                dialog.cancel();
                Log.e("TAG","getUserInfo json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    UserInfo entity = response.getData();
                    PreferenceUtils.setPrefString("userInfo", MyApplication.gson.toJson(entity));
                    setAlias(entity.getUserId());
                    if(tag == 0)startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    else if(tag == 1) setResult(RESULT_OK);
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


    private void setAlias(String userId){
        PushAgent.getInstance(this).addAlias(userId, MyApplication.ALIAS_NAME, new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                LogUtil.showLog("setAlias:"+message);
            }
        });
    }

}
