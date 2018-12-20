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
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.MeetingEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityMeetingDetailsBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 三会一课详情
 */
public class MeetingDetailsActivity extends BaseSlideActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityMeetingDetailsBinding binding;

    private List<UserInfo> list = new ArrayList<>();
    private CommonAdapter adapter;


    private String id = "";
    private String str1 = "议题 ";
    private String str2 = "参会要求 ";
    private CustomProgressDialog customProgressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_meeting_details);
        init();
    }

    private void init(){
        customProgressDialog = new CustomProgressDialog(this);
        initAdapter();
        id = getIntent().getStringExtra("id");
        getData();
        binding.msv.smoothScrollTo(0,0);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("会议详情");
        binding.srl.setOnRefreshListener(this);

    }

    private void initUI(MeetingEntity entity){
        if(entity == null) return;
        binding.tvTitle.setText(entity.getTheme());
        PicassoUtil.setImageUrl(this,binding.ivHead, URLS.IMAGE_PRE + MyApplication.getInstance().getUserInfo().getPhoto(),R.drawable.icon_head,40,40);
        binding.tvName.setText(MyApplication.getInstance().getUserInfo().getName());

        if(!TextUtils.isEmpty(entity.getStartTime())&& entity.getStartTime().length()>=16
                && !TextUtils.isEmpty(entity.getEndTime())&& entity.getEndTime().length()>=16){
            String startDay = entity.getStartTime().substring(0,10);
            String endDay = entity.getEndTime().substring(0,10);

            String startTime = entity.getStartTime().substring(11,16);
            String endTime = entity.getEndTime().substring(11,16);
            if(TextUtils.equals(startDay,endDay)){
                binding.tvTime.setText(startDay + " "+entity.getWeek()+" "+startTime+"~"+endTime);
            }else binding.tvTime.setText(entity.getStartTime() +"~"+ entity.getEndTime());
        }else binding.tvTime.setText(entity.getStartTime() +"~"+ entity.getEndTime());
        binding.tvRoom.setText(entity.getAddress());
        splitTextColor(binding.tvMeetingTitle,str1 + entity.getIssue(),2);
        splitTextColor(binding.tvMeetingRequest,str2 + entity.getRequirement(),4);
        binding.tvJoinedPerson.setText("已有"+entity.getParticipateNum()+"人参加");
        binding.tvLeavedPerson.setText(entity.getLeaveNum()+"人请假");
        list = entity.getMeetingInFoUserBizList();
        adapter.refresh(list);
        String state = entity.getState();
        if(TextUtils.equals(state, "0")){ //已参加
            binding.tvJoin.setText("已参加");
            binding.tvJoin.setTextColor(getResources().getColor(R.color.c_gray));
            binding.tvJoin.setClickable(false);
        }else if(TextUtils.equals(state, "1")){ //已请假
            binding.tvLeave.setText("已请假");
            binding.tvLeave.setTextColor(getResources().getColor(R.color.c_gray));
            binding.tvLeave.setClickable(false);
        }
    }

    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("id",id);
        OkHttpClientManager.postAsyn(URLS.MEETING_DETAILS, new OkHttpClientManager.ResultCallback<BaseObject<MeetingEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<MeetingEntity> response) {
                binding.srl.setRefreshing(false);
                LogUtil.showLog("会议详情："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    MeetingEntity entity = response.getData();
                    initUI(entity);
                }else{
                    MyTools.showToast(MeetingDetailsActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }

            }
        },header,map);
    }




    private void join(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("meetingId","");
        OkHttpClientManager.postAsyn(URLS.MEETING_JOIN, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                customProgressDialog.cancel();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                customProgressDialog.cancel();
                LogUtil.showLog("参加："+ MyApplication.gson.toJson(response));
                MyTools.showToast(MeetingDetailsActivity.this, response.getMsg());
                if(response.isSuccess()){
                    getData();
                }else{
                    if(response.getErrorCode() == 1)refreshToken();
                }

            }
        },header,map);
    }


    @SuppressLint("NewApi")
    private void initAdapter(){
        adapter = new CommonAdapter<UserInfo>(this, R.layout.item_meeting_details,list){
            @Override
            protected void convert(ViewHolder holder, UserInfo entity, int position) {
                holder.setImageUrl((ImageView) holder.getView(R.id.img_head),URLS.IMAGE_PRE+entity.getPhoto(),R.drawable.icon_head,60,60);
                holder.setText(R.id.tv_name,entity.getName());
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
                startActivityForResult(new Intent(this, MeetingLeaveActivity.class).putExtra("id",id),88);
                break;
            case R.id.tv_join://参加
//                startActivity(new Intent(this, MeetingJoinActivity.class));
                customProgressDialog.show();
                join();
                break;
        }
    }


    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
               getData();
            }
        },400);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 88 && resultCode == RESULT_OK){
            getData();
        }
    }
}
