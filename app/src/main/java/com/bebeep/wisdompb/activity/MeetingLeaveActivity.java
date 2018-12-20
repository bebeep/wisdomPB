package com.bebeep.wisdompb.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.databinding.ActivityMeetingLeaveBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.HashMap;


/**
 * 填写请假
 */
public class MeetingLeaveActivity extends BaseEditActivity implements View.OnClickListener{
    private ActivityMeetingLeaveBinding binding;

    private String id="";
    private CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_meeting_leave);
        init();
    }

    private void init(){
        customProgressDialog = new CustomProgressDialog(this);
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("请假申请");
        binding.tvNam.setText(MyApplication.getInstance().getUserInfo().getName());
        id = getIntent().getStringExtra("id");
        binding.etReason.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_submit:
                if(TextUtils.isEmpty(binding.etReason.getText().toString())){
                    MyTools.showToast(this,"请填写请假事由");
                    return;
                }
                leave();
                break;
        }

    }


    private void leave(){
        customProgressDialog.show();
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("meetingId",id);
        map.put("reasonForLeave", binding.etReason.getText().toString());
        OkHttpClientManager.postAsyn(URLS.MEETING_LEAVE, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                customProgressDialog.cancel();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                customProgressDialog.cancel();
                LogUtil.showLog("请假："+MyApplication.gson.toJson(response));
                MyTools.showToast(MeetingLeaveActivity.this, response.getMsg());
                if(response.isSuccess()){
                    setResult(RESULT_OK);
                    finish();
                }else{
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            binding.tvTextNum.setText(s.length()+"/100");
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

}
