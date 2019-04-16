package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.PayEntity;
import com.bebeep.wisdompb.bean.PayRecordEntity;
import com.bebeep.wisdompb.databinding.ActivityChargeBinding;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

public class ChargeActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityChargeBinding binding;
    private List<PayRecordEntity> list = new ArrayList<>();
    private CommonAdapter adapter;
    private int pageNo = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_charge);
        init();
    }


    private void init(){
        initAdapter();
        getData();
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("缴纳记录");
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAdapter(){
        adapter = new CommonAdapter<PayRecordEntity>(this,R.layout.item_charge,list){
            @Override
            protected void convert(ViewHolder holder, PayRecordEntity entity, int position) {
                holder.setText(R.id.tv_name,entity.getTitle());
                holder.setText(R.id.tv_num,"￥ "+entity.getAmountMoney());
                holder.setText(R.id.tv_tag, TextUtils.equals("0",entity.getType())?"线上支付":"线下支付");
                holder.setText(R.id.tv_time,entity.getCreateDate());
            }
        };

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    /**
     * 获取缴费记录
     */
    private void getData() {
        HashMap header = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("", "");
        OkHttpClientManager.postAsyn(URLS.PARTY_PAY_RECORD, new OkHttpClientManager.ResultCallback<BaseList<PayRecordEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
                statusMsg(e, code);
            }
            @Override
            public void onResponse(BaseList<PayRecordEntity> response) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                Log.e("TAG", "getData json=" + MyApplication.gson.toJson(response));
                if (response.isSuccess()) {
                    if(pageNo == 1)list = response.getData();
                    else{
                        if(response==null||response.getData()==null||response.getData().size()==0) MyTools.showToast(ChargeActivity.this,"没有更多记录了");
                        else list.addAll(response.getData());
                    }
                    adapter.refresh(list);
                } else {
                    MyTools.showToast(ChargeActivity.this, response.getMsg());
                }
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
        }, header, map);
    }

    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNo = 1;
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
                pageNo ++;
                getData();
            }
        },500);
    }
}
