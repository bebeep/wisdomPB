package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.CollectEntity;
import com.bebeep.wisdompb.bean.CommonTypeEntity;
import com.bebeep.wisdompb.databinding.ActivityMyCollectionBinding;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 我的收藏
 */
public class MyCollectionActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityMyCollectionBinding binding;
    private CommonAdapter adapter;
    private List<CollectEntity> list = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)getData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_collection);
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
        binding.title.tvTitle.setText("我的收藏");

        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAdapter(){
        adapter = new CommonAdapter<CollectEntity>(this,R.layout.item_my_collection,list){
            @Override
            protected void convert(ViewHolder holder, final CollectEntity entity, int position) {
                holder.setText(R.id.tv_title, entity.getThemeTitle());
                holder.setText(R.id.tv_time,entity.getCreateDate());
                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (entity.getType()){
                            case 0://首页新闻
                                startActivity(new Intent(MyCollectionActivity.this,NewsDetailActivity.class).putExtra("title",entity.getThemeTitle()).putExtra("id",entity.getThemeId()).putExtra("tag",1));
                                break;
                            case 1://专题教育
                                startActivity(new Intent(MyCollectionActivity.this,NewsDetailActivity.class).putExtra("title",entity.getThemeTitle()).putExtra("id",entity.getThemeId()).putExtra("tag",2));
                                break;
                            case 2://党内公示
                                startActivity(new Intent(MyCollectionActivity.this,NewsDetailActivity.class).putExtra("title",entity.getThemeTitle()).putExtra("id",entity.getThemeId()).putExtra("tag",3));
                                break;
                            case 3://活动
                                startActivity(new Intent(MyCollectionActivity.this,PartyActDetailsActivity.class).putExtra("id",entity.getThemeId()));
                                break;
                        }
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }



    private void getData() {
        HashMap header = new HashMap(), map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("type", "");
        OkHttpClientManager.postAsyn(URLS.MY_COLLECT, new OkHttpClientManager.ResultCallback<BaseList<CollectEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e, code);
                binding.srl.setRefreshing(false);
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<CollectEntity> response) {
                binding.srl.setRefreshing(false);
                Log.e("TAG", "我的收藏 response: " + MyApplication.gson.toJson(response));
                if (response.isSuccess()) {
                    list = response.getData();
                    adapter.refresh(list);
                } else {
                    MyTools.showToast(MyCollectionActivity.this,response.getMsg());
                    if (response.getErrorCode() == 1) refreshToken();
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



