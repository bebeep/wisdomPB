package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.bean.AnswerEntity;
import com.bebeep.wisdompb.databinding.ActivityTestResultBinding;

import java.util.ArrayList;
import java.util.List;


/**
 * 答题结果
 */
public class TestResultActivity extends SlideBackActivity implements View.OnClickListener {

    private ActivityTestResultBinding binding;
    private List<AnswerEntity> list = new ArrayList<>();
    private CommonAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test_result);
        init();
    }

    private void init() {
        initAdapter();
        binding.setVariable(BR.onClickListener, this);
        binding.title.ivBack.setVisibility(View.VISIBLE);


    }

    private void initAdapter() {
        for (int i = 0; i < 7; i++) list.add(new AnswerEntity(true));
        list.add(new AnswerEntity(false));
        list.add(new AnswerEntity(true));
        list.add(new AnswerEntity(true));
        list.add(new AnswerEntity(true));
        adapter = new CommonAdapter<AnswerEntity>(this, R.layout.item_exam_result, list) {
            @Override
            protected void convert(ViewHolder holder, AnswerEntity entity, int position) {
                holder.setText(R.id.num, String.valueOf(position + 1));
                holder.setImageResource(R.id.iv_result, entity.isRight() ? R.drawable.icon_answer_right : R.drawable.icon_answer_wrong);
                holder.setBackgroundRes(R.id.num, entity.isRight() ? R.drawable.bg_line_2dp_green : R.drawable.bg_line_2dp_theme);
                holder.setTextColor(R.id.num, getResources().getColor(entity.isRight() ? R.color.green:R.color.theme_d));
            }
        };
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,8));
        binding.recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_check_answer://查看答案？？(返回首页)
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
