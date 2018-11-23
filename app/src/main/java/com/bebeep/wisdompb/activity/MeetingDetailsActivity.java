package com.bebeep.wisdompb.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityMeetingDetailsBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 三会一课详情
 */
public class MeetingDetailsActivity extends SlideBackActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityMeetingDetailsBinding binding;

    private List<String> list = new ArrayList<>();
    private CommonAdapter adapter;


    private String str1 = "议题 " + "汇报11月份项目进度情况，总结经验与不足，落实下一月份项目计划";
    private String str2 = "参会要求 "+"正装、每个支部至少5人出席";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_meeting_details);
        init();
    }

    private void init(){
        initAdapter();
        binding.msv.smoothScrollTo(0,0);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("会议详情");
        binding.srl.setOnRefreshListener(this);
        splitTextColor(binding.tvMeetingTitle,str1,2);
        splitTextColor(binding.tvMeetingRequest,str2,4);
    }

    @SuppressLint("NewApi")
    private void initAdapter(){
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        adapter = new CommonAdapter<String>(this, R.layout.item_meeting_details,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {

            }
        };
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,5));
        binding.recyclerView.setAdapter(adapter);



        binding.msv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                binding.srl.setEnabled(scrollY == 0);
            }
        });
    }



    private void splitTextColor(TextView tv, String content, int splitIndex){
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#db3a32")), 0, splitIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#585756")), splitIndex,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spannableString);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_leave://请假
                startActivity(new Intent(this, MeetingLeaveActivity.class));
                break;
            case R.id.tv_join://参加
                startActivity(new Intent(this, MeetingJoinActivity.class));
                break;
        }
    }


    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                MyTools.showToast(MeetingDetailsActivity.this,"刷新成功");
                binding.srl.setRefreshing(false);
            }
        },400);
    }
}
