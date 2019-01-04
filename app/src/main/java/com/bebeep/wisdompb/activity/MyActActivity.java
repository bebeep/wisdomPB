package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.OrgActEntity;
import com.bebeep.wisdompb.bean.SubmitEntity;
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
 * 我的活动
 */
public class MyActActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityMyExamBinding binding;
    private CommonAdapter adapter;
    private List<OrgActEntity> list = new ArrayList<>();

    private int pageNo = 1;

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
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("我的活动");

        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAdapter(){
        adapter = new CommonAdapter<OrgActEntity>(this,R.layout.item_party_act,list){
            @Override
            protected void convert(ViewHolder holder, final OrgActEntity entity, int position) {
                holder.setText(R.id.tv_title, entity.getTitle());
                holder.setText(R.id.tv_time, entity.getStartTime() + " —— "+entity.getEndTime());
                holder.setText(R.id.tv_zan_num, entity.getDzQuantity());
                holder.setText(R.id.tv_scan_num, entity.getReadingQuantity());
                holder.setBackgroundRes(R.id.tv_join, TextUtils.equals("1",entity.getIsParticipate())?R.drawable.bg_tv_send_gray:R.drawable.bg_btn_join);
                holder.setText(R.id.tv_join,TextUtils.equals("1",entity.getIsParticipate())?"已参与":"我要参与");
                holder.setImageResource(R.id.iv_zan, TextUtils.equals(entity.getIsDz(),"1")?R.drawable.icon_zan_yellow:R.drawable.icon_zan_gallery);
                String[] imgs = entity.getImgsrcs().split(",");
                if(imgs!=null){
                    switch (imgs.length){
                        case 0:
                            holder.setVisible(R.id.ll_1pic, false);
                            holder.setVisible(R.id.ll_2pic, false);
                            holder.setVisible(R.id.ll_3pic, false);
                            break;
                        case 1:
                            holder.setVisible(R.id.ll_1pic, true);
                            holder.setVisible(R.id.ll_2pic, false);
                            holder.setVisible(R.id.ll_3pic, false);
                            holder.setImageUrl((ImageView)holder.getView(R.id.iv_1pic_1),URLS.IMAGE_PRE+ imgs[0],R.drawable.default_error,250,120);
                            break;
                        case 2:
                            holder.setVisible(R.id.ll_1pic, false);
                            holder.setVisible(R.id.ll_2pic, true);
                            holder.setVisible(R.id.ll_3pic, false);
                            holder.setImageUrl((ImageView)holder.getView(R.id.iv_2pic_1),URLS.IMAGE_PRE+ imgs[0],R.drawable.default_error,150,100);
                            holder.setImageUrl((ImageView)holder.getView(R.id.iv_2pic_2),URLS.IMAGE_PRE+ imgs[1],R.drawable.default_error,150,100);
                            break;
                        case 3:
                            holder.setVisible(R.id.ll_1pic, false);
                            holder.setVisible(R.id.ll_2pic, false);
                            holder.setVisible(R.id.ll_3pic, true);
                            holder.setImageUrl((ImageView)holder.getView(R.id.iv_3pic_1),URLS.IMAGE_PRE+ imgs[0],R.drawable.default_error,100,70);
                            holder.setImageUrl((ImageView)holder.getView(R.id.iv_3pic_2),URLS.IMAGE_PRE+ imgs[1],R.drawable.default_error,100,70);
                            holder.setImageUrl((ImageView)holder.getView(R.id.iv_3pic_3),URLS.IMAGE_PRE+ imgs[2],R.drawable.default_error,100,70);
                            break;
                    }
                }



                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MyActActivity.this,PartyActDetailsActivity.class).putExtra("id",entity.getActivityId()));
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
        map.put("pageNo",String.valueOf(pageNo));
        map.put("pageSize",MyApplication.pageSize);
        OkHttpClientManager.postAsyn(URLS.MY_ACT, new OkHttpClientManager.ResultCallback<BaseList<OrgActEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<OrgActEntity> response) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                LogUtil.showLog("我的活动："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    if(pageNo==1) list = response.getData();
                    else {
                        if(response.getData()==null || response.getData().size() ==0) MyTools.showToast(MyActActivity.this,"没有更多内容了");
                        else list.addAll(response.getData());
                    }
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(MyActActivity.this, response.getMsg());
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
                pageNo ++ ;
                getData();
            }
        },500);
    }
}
