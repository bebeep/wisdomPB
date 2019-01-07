package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.JifenEntity;
import com.bebeep.wisdompb.databinding.ActivityJifenDetailsBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;


/**
 * 积分明细
 */
public class JifenDetailsActivity extends BaseSlideActivity implements View.OnClickListener,OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityJifenDetailsBinding binding;
    private CommonAdapter adapter;
    private List<JifenEntity> list = new ArrayList<>();


    private int flag = 0,pageNo = 1; //0用户  1党支部


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_jifen_details);
        init();
    }

    private void init(){
        flag = getIntent().getIntExtra("flag",0);
        initAdapter();
        getJifenList();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("积分明细");
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }


    private void initAdapter(){
        adapter = new CommonAdapter<JifenEntity>(this,R.layout.item_jifen_details,list){
            @Override
            protected void convert(ViewHolder holder, JifenEntity entity, int position) {
                splitTextColor((TextView) holder.getView(R.id.tv_title),entity.getTypeLabel()+" "+entity.getContent(),entity.getTypeLabel().length());
                holder.setText(R.id.tv_time,entity.getCreateDate());
                holder.setText(R.id.tv_score, entity.getOperator()+entity.getFraction());
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }


    private void splitTextColor(TextView tv, String content, int splitIndex){
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#db3a32")), 0, splitIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#585756")), splitIndex,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spannableString);
    }

    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNo=1;
                getJifenList();
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
                pageNo ++ ;
                getJifenList();
            }
        },500);
    }



    private void getJifenList(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("pageNo",String.valueOf(pageNo));
        map.put("pageSize",MyApplication.pageSize);
        LogUtil.showLog("header:"+header);
        LogUtil.showLog("map:"+map);
        OkHttpClientManager.postAsyn(flag==0?URLS.MY_JIFEN_DETAILS1:URLS.MY_JIFEN_DETAILS2, new OkHttpClientManager.ResultCallback<BaseList<JifenEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.nrl.onLoadFinished();
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<JifenEntity> response) {
                binding.nrl.onLoadFinished();
                binding.srl.setRefreshing(false);
                LogUtil.showLog("积分排名："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    if(pageNo==1){
                        list = response.getData();
                    }else{
                        if(response.getData() == null || response.getData().size() ==0) MyTools.showToast(JifenDetailsActivity.this,"没有更多内容了");
                        else list.addAll(response.getData());
                    }
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(JifenDetailsActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
        },header,map);
    }
}
