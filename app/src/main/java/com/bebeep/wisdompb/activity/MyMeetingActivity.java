package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.MeetingEntity;
import com.bebeep.wisdompb.databinding.ActivityMyExamBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;


/**
 * 我的会议
 */
public class MyMeetingActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityMyExamBinding binding;
    private CommonAdapter adapter;
    private List<MeetingEntity> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_exam);
        init();
    }


    private void init(){
        initAdapter();
        getData();
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("我的会议");

        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAdapter(){
        adapter = new CommonAdapter<MeetingEntity>(this,R.layout.item_my_collection,list){
            @Override
            protected void convert(ViewHolder holder, final MeetingEntity entity, int position) {
                holder.setText(R.id.tv_title,entity.getTheme());
                holder.setText(R.id.tv_time,entity.getStartTime());
                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MyMeetingActivity.this,MeetingDetailsActivity.class).putExtra("id",entity.getId()));
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("","");
        OkHttpClientManager.postAsyn(URLS.MY_MEETING, new OkHttpClientManager.ResultCallback<BaseList<MeetingEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.srl.setRefreshing(false);
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<MeetingEntity> response) {
                binding.srl.setRefreshing(false);
                LogUtil.showLog("我的会议 列表："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
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
                binding.nrl.onLoadFinished();
            }
        },500);
    }
}
