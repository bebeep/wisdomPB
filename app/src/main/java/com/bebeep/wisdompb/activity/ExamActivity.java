package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.ExamEntity;
import com.bebeep.wisdompb.databinding.ActivityExamBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.HashMap;


/**
 * 在线考试
 */
public class ExamActivity extends BaseSlideActivity implements View.OnClickListener{

    private ActivityExamBinding binding;
    private String id;
    private ExamEntity entity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exam);
        init();
    }

    private void init(){
        id = getIntent().getStringExtra("id");
        getData();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("在线考试");

    }


    private void initUI(){
        if(entity == null) return;
        binding.tvTitle.setText(entity.getTitle());
        binding.tvTotalScore.setText(entity.getTotalScore()+"分");
        binding.tvPassScore.setText(entity.getPassingGrade()+"分");
        binding.tvTotalQuestion.setText(entity.getTotalNumberQuestions()+"题");
        binding.tvTime.setText(entity.getExaminationTime()+"分钟");
        binding.tvStartTime.setText(entity.getStartTime());
        binding.tvEndTime.setText(entity.getEndTime());
        binding.tvExplain.setText(entity.getExaminationNotes());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_start:
                if(entity!=null)startActivityForResult(new Intent(this,TestingActivity.class).putExtra("id",id)
                        .putExtra("title",entity.getTitle()).putExtra("totalScore",entity.getTotalScore()), MyApplication.ACTIVITY_BACK_CODE);
                break;
        }
    }


    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("id", id);
        OkHttpClientManager.postAsyn(URLS.EXAM_DETAILS, new OkHttpClientManager.ResultCallback<BaseObject<ExamEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<ExamEntity> response) {
                LogUtil.showLog("考试详情 ："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    entity = response.getData();
                    initUI();
                }else{
                    MyTools.showToast(ExamActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.showLog("examActivity resultCode:"+resultCode+"|requestCode"+requestCode);
        if(resultCode == RESULT_OK){
            if(requestCode == MyApplication.ACTIVITY_BACK_CODE){
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
