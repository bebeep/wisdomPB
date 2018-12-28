package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.GalleryEntity;
import com.bebeep.wisdompb.bean.OrgActEntity;
import com.bebeep.wisdompb.databinding.ActivityPartyActBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;


/**
 * 党组织活动
 */
public class PartyActActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityPartyActBinding binding;
    private CommonAdapter adapter;
    private List<OrgActEntity> list = new ArrayList<>();
    private WebSettings settings;
    private OrgActEntity entity;

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null&&entity!=null && !TextUtils.isEmpty(entity.getId())) getActList();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_party_act);
        init();
    }

    private void init(){
        initAdapter();
        getOrgDetails();
        binding.title.tvTitle.setText("党组织活动");
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setImageResource(R.drawable.icon_search);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(false);
        binding.nrl.setOnLoadingListener(this);


        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        binding.title.ivTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTools.showToast(PartyActActivity.this,"search");
            }
        });


        binding.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1://简介
                        binding.nrl.setVisibility(View.GONE);
                        binding.webview.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb2://活动
                        binding.nrl.setVisibility(View.VISIBLE);
                        binding.webview.setVisibility(View.GONE);
                        if(list == null||list.size()==0) getActList();
                        break;
                }
            }
        });

    }

    private void initUI(){
        if(entity == null) return;
        binding.tvOrgName.setText(entity.getTitle());
        PicassoUtil.setImageUrl(this,binding.imgHead, URLS.IMAGE_PRE+entity.getImgsrc(),R.drawable.default_error,60,60);
        binding.tvMember.setText("党员  "+entity.getNumberPartyMembers());
        binding.tvActivities.setText("活动  "+entity.getActivityQuantity());
        initWeb(URLS.HOST + entity.getInfoUrl());
    }

    //加载webview
    private void initWeb(String url) {
        settings = binding.webview.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webview.loadUrl(url);
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }
        });
        binding.webview.setWebViewClient(new WebViewClient());
    }


    private void initAdapter(){

        adapter = new CommonAdapter<OrgActEntity>(this,R.layout.item_party_act,list){
            @Override
            protected void convert(ViewHolder holder, final OrgActEntity entity, int position) {
                holder.setText(R.id.tv_title, entity.getTitle());
                holder.setText(R.id.tv_time, entity.getStartTime() + " —— "+entity.getEndTime());
                holder.setText(R.id.tv_zan_num, entity.getDzQuantity());
                holder.setText(R.id.tv_scan_num, entity.getReadingQuantity());
                holder.setBackgroundRes(R.id.tv_join,TextUtils.equals("1",entity.getIsParticipate())?R.drawable.bg_tv_send_gray:R.drawable.bg_btn_join);
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
                        startActivity(new Intent(PartyActActivity.this,PartyActDetailsActivity.class).putExtra("id",entity.getId()));
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

    }


    private void getOrgDetails(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("","");
        LogUtil.showLog("header:"+header);
        LogUtil.showLog("map:"+map);
        OkHttpClientManager.postAsyn(URLS.ORG_DETAILS, new OkHttpClientManager.ResultCallback<BaseObject<OrgActEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<OrgActEntity> response) {
                binding.nrl.onLoadFinished();
                binding.srl.setRefreshing(false);
                LogUtil.showLog("党组织详情："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    entity = response.getData();
                    initUI();
                }else{
                    MyTools.showToast(PartyActActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }

    private void getActList(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("officeId",entity.getId());
        LogUtil.showLog("header:"+header);
        LogUtil.showLog("map:"+map);
        OkHttpClientManager.postAsyn(URLS.ORG_ACT_LIST, new OkHttpClientManager.ResultCallback<BaseList<OrgActEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
                binding.nrl.onLoadFinished();
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<OrgActEntity> response) {
                binding.nrl.onLoadFinished();
                binding.srl.setRefreshing(false);
                LogUtil.showLog("活动列表："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(PartyActActivity.this, response.getMsg());
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
                binding.srl.setRefreshing(false);
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
