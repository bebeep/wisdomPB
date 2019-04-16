package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.MeetingEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityMeetingMinutesBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * Created by Bebeep
 * Time 2018/11/22 19:56
 * Email 424468648@qq.com
 * Tips 会议纪要
 */

public class MeetingMinutesActivity extends BaseSlideActivity implements View.OnClickListener,
        OnPullListener,SwipeRefreshLayout.OnRefreshListener {
    private ActivityMeetingMinutesBinding binding;

    private List<MeetingEntity> list = new ArrayList<>();
    private CommonAdapter adapter;


    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null) getData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_meeting_minutes);
        init();
    }

    private void init(){
        binding.setVariable(BR.onClickListener,this);
        initAdapter();
        getData();
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("会议纪要");

    }



    private void initAdapter(){
        adapter = new CommonAdapter<MeetingEntity>(this,R.layout.item_f3,list){
            @Override
            protected void convert(ViewHolder holder, final MeetingEntity entity, int position) {
                String startTime = entity.getStartTime();
                if(!TextUtils.isEmpty(startTime) && startTime.length() >= 16){
                    String month = startTime.substring(0,7);
                    String day = startTime.substring(8,10);
                    holder.setText(R.id.tv_month,month);
                    holder.setText(R.id.tv_day,day);
                }
                holder.setText(R.id.tv_week,entity.getWeek());
                holder.setText(R.id.tv_title,entity.getTheme());
                holder.setText(R.id.tv_content,entity.getStartTime().substring(5,entity.getStartTime().length()) +"~"+entity.getEndTime().substring(5,entity.getEndTime().length())+"  "+entity.getAddress());

                if(entity.getIfRecord() == 0) {
                    holder.setText(R.id.tv_state,"未记录");
                    holder.setBackgroundRes(R.id.tv_state, R.drawable.bg_rec_2dp_red);
                }else if(entity.getIfRecord() == 1) {
                    holder.setText(R.id.tv_state,"已记录");
                    holder.setBackgroundRes(R.id.tv_state, R.drawable.bg_rec_2dp_green);
                }

                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(entity.getMinutesMeetingType() == 1){
                            if(entity.getIfRecord() == 0){//未记录-跳转到发布会议纪要
                                startActivity(new Intent(MeetingMinutesActivity.this,ReleaseMeetingMinitesActivity.class).putExtra("id",entity.getId()));
                            }else if(entity.getIfRecord() == 1){//已记录-跳转到会议纪要详情
                                startActivity(new Intent(MeetingMinutesActivity.this,MeetingMinutesDetailActivity.class).putExtra("id",entity.getId()).putExtra("title","会议纪要"));
                            }
                        }else {
//                            startActivity(new Intent(MeetingMinutesActivity.this,MeetingMinutesDetailActivity.class).putExtra("id",entity.getId()).putExtra("title","会议纪要"));

                            if(entity.getIfRecord() == 0){//未记录-提示
                                MyTools.showToast(MeetingMinutesActivity.this,"该会议还未记录会议纪要，请稍后查看");
                            }else if(entity.getIfRecord() == 1){//已记录-跳转到会议纪要详情
                                startActivity(new Intent(MeetingMinutesActivity.this,MeetingMinutesDetailActivity.class).putExtra("id",entity.getId()).putExtra("title","会议纪要"));
                            }
                        }
                    }
                });

            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }


    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("","");
        OkHttpClientManager.postAsyn(URLS.MEETING_MINUTES, new OkHttpClientManager.ResultCallback<BaseList<MeetingEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<MeetingEntity> response) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                LogUtil.showLog("会议纪要："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(MeetingMinutesActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
        },header,map);
    }

    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        },500);
    }

    @Override
    public void onRefresh(AbsRefreshLayout listLoader) {

    }
    @Override
    public void onLoading(AbsRefreshLayout listLoader) {
        binding.nrl.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },500);
    }
}
