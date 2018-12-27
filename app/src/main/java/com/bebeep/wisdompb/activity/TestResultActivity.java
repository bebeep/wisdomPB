package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.AnswerEntity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.ExamResultEntity;
import com.bebeep.wisdompb.bean.ExamResultItemEntity;
import com.bebeep.wisdompb.bean.TestingEntity;
import com.bebeep.wisdompb.databinding.ActivityTestResultBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 答题结果
 */
public class TestResultActivity extends BaseEditActivity implements View.OnClickListener {

    private ActivityTestResultBinding binding;
    private List<ExamResultItemEntity> list = new ArrayList<>();
    private CommonAdapter adapter;
    private String templateId, title;//考试模板id；
    private ExamResultEntity entity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test_result);
        init();
    }

    private void init() {
        templateId = getIntent().getStringExtra("templateId");
        title = getIntent().getStringExtra("title");
        initAdapter();
        getData();
        binding.setVariable(BR.onClickListener, this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("考试成绩");


    }

    private void initUI() {
        binding.tvTitle.setText(title);
        binding.tvScore.setText(String.valueOf(entity.getTotalScore()));
        binding.tvPassScore.setText(String.valueOf("及格分数：" + entity.getPassingGrade() + "分"));
        binding.tvRemind.setText(entity.getTotalScore() >= entity.getPassingGrade() ? "恭喜你，通过考试" : "考试未合格");
        binding.tvTime.setText("考试用时：" + entity.getExamTime() + "分钟");
        list = entity.getItemBackList();
        adapter.refresh(list);
    }


    private void initAdapter() {
        adapter = new CommonAdapter<ExamResultItemEntity>(this, R.layout.item_exam_result, list) {
            @Override
            protected void convert(ViewHolder holder, ExamResultItemEntity entity, int position) {
                holder.setText(R.id.num, String.valueOf(entity.getIndex()));
                holder.setImageResource(R.id.iv_result, entity.getIsCorrect() == 1 ? R.drawable.icon_answer_right : R.drawable.icon_answer_wrong);
                holder.setBackgroundRes(R.id.num, entity.getIsCorrect() == 1 ? R.drawable.bg_line_2dp_green : R.drawable.bg_line_2dp_theme);
                holder.setTextColor(R.id.num, getResources().getColor(entity.getIsCorrect() == 1 ? R.color.green : R.color.theme_d));
            }
        };
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 8));
        binding.recyclerView.setAdapter(adapter);

    }


    private void getData() {
        HashMap header = new HashMap(), map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("templateId", templateId);
        LogUtil.showLog("header:" + header);
        LogUtil.showLog("map:" + map);
        OkHttpClientManager.postAsyn(URLS.EXAM_RESULT, new OkHttpClientManager.ResultCallback<BaseObject<ExamResultEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e, code);
            }

            @Override
            public void onResponse(BaseObject<ExamResultEntity> response) {
//                LogUtil.showLog("考试结果："+ response);
                LogUtil.showLog("考试结果：" + MyApplication.gson.toJson(response));
                if (response.isSuccess()) {
                    entity = response.getData();
                    if (entity != null) initUI();
                } else {
                    MyTools.showToast(TestResultActivity.this, response.getMsg());
                    if (response.getErrorCode() == 1) refreshToken();
                }
            }
        }, header, map);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.tv_check_answer://(返回首页)
                setResult(RESULT_OK);
                finish();
                break;
        }
    }


    @Override
    public void onBackPressed() {//返回
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.showLog("onKeyDown:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {//返回
            setResult(RESULT_OK);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
