package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bebeep.wisdompb.bean.NewsEntity;
import com.bebeep.wisdompb.databinding.ActivityPublicShowBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 党内公示
 */
public class PublicShowActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityPublicShowBinding binding;
    private CommonAdapter adapter;
    private List<NewsEntity> list = new ArrayList<>();

    private int pageNo = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_public_show);
        init();
    }

    private void init(){
        initAdapter();
        getData();
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("党内公示");
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setImageResource(R.drawable.icon_search);
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);

        binding.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1:

                        break;
                    case R.id.rb2:

                        break;
                }
            }
        });

        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.title.ivTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PublicShowActivity.this, SearchActivity.class).putExtra("type",5));
            }
        });
    }

    private void initAdapter(){
        adapter = new CommonAdapter<NewsEntity>(this,R.layout.item_public_show,list){
            @Override
            protected void convert(ViewHolder holder, NewsEntity entity, int position) {
                ImageView iv = holder.getView(R.id.iv_head);
                holder.setImageUrl(iv,URLS.IMAGE_PRE+entity.getPictureAddress(),R.drawable.default_error,80,60);
                holder.setText(R.id.tv_title,entity.getTitle());
                holder.setText(R.id.tv_time,entity.getUpdateDate());
                holder.setText(R.id.tv_time,entity.getUpdateDate());
                holder.setVisible(R.id.iv_link,TextUtils.equals(entity.getWhetherUrlAddress(),"1"));

            }
        };
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                NewsEntity entity = list.get(position);
                if(TextUtils.equals(entity.getWhetherUrlAddress(),"1")){
                    startActivity(new Intent(PublicShowActivity.this,WebViewActivity.class).putExtra("title",entity.getTitle()).putExtra("url",entity.getUrl()));
                }else startActivity(new Intent(PublicShowActivity.this,NewsDetailActivity.class).putExtra("title",entity.getTitle()).putExtra("id",entity.getId()).putExtra("tag",3));
            }
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }


    /**
     * 获取新闻列表
     */
    private void getData(){
        HashMap header = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("pageNo", String.valueOf(pageNo));
        map.put("pageSize", "20");
        OkHttpClientManager.postAsyn(URLS.PUBLIC_SHOW_LIST, new OkHttpClientManager.ResultCallback<BaseList<NewsEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.nrl.onLoadFinished();
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
                binding.tvEmpty.setVisibility(list == null || list.size() ==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<NewsEntity> response) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                Log.e("TAG","获取新闻列表 json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    if(pageNo == 1)list = response.getData();
                    else {
                        if(response.getData() == null || response.getData().size() ==0) MyTools.showToast(PublicShowActivity.this,"没有更多内容了");
                        else list.addAll(response.getData());
                    }
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(PublicShowActivity.this, response.getMsg());
                }
                binding.tvEmpty.setVisibility(list == null || list.size() ==0?View.VISIBLE:View.GONE);
            }
        },header,map);
    }

    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
               pageNo=1;
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
                pageNo++;getData();
            }
        },500);
    }
}
