package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

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
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.JifenEntity;
import com.bebeep.wisdompb.databinding.ActivityMyJifenBinding;
import com.bebeep.wisdompb.util.ListDividerItemDecoration;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 我的积分
 */
public class MyJifenActivity extends BaseSlideActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener,
        SwipeRefreshLayout.OnRefreshListener,OnPullListener {

    private ActivityMyJifenBinding binding;
    private List<JifenEntity> list1 = new ArrayList<>(),list2 = new ArrayList<>();
    private CommonAdapter adapter1,adapter2;
    private int pageNo = 1;

    private String JIFEN_DETAIL=URLS.MY_JIFEN_DETAIL_PERSON,JIFEN_LIST= URLS.MY_JIFEN_PERSONAL;
    private JifenEntity entity1,entity2;
    private int currentChecked = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_jifen);
        init();
    }

    private void init(){
        initAdapter1();
        initAdapter2();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("我的积分");
//        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setImageResource(R.drawable.icon_myjifen_top_right);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
//        binding.nrl.setPullLoadEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.recyclerView2.setVisibility(View.GONE);
        binding.rg.setOnCheckedChangeListener(this);

        getJifenDetail();
        getJifenList();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_title_right:

                break;
            case R.id.ll_details:
                startActivity(new Intent(MyJifenActivity.this, JifenDetailsActivity.class).putExtra("flag",currentChecked));
                break;
        }
    }

    private void initAdapter1(){
        adapter1 = new CommonAdapter<JifenEntity>(this,R.layout.item_my_jifen,list1){
            @Override
            protected void convert(ViewHolder holder, JifenEntity entity, int position) {
                if(position == 0){
                    holder.setVisible(R.id.tv_range,false);
                    holder.setVisible(R.id.iv_range,true);
                    holder.setImageResource(R.id.iv_range,R.drawable.icon_jifen_range1);
                }else if(position == 1){
                    holder.setVisible(R.id.tv_range,false);
                    holder.setVisible(R.id.iv_range,true);
                    holder.setImageResource(R.id.iv_range,R.drawable.icon_jifen_range2);
                }else if(position == 2){
                    holder.setVisible(R.id.tv_range,false);
                    holder.setVisible(R.id.iv_range,true);
                    holder.setImageResource(R.id.iv_range,R.drawable.icon_jifen_range3);
                }else {
                    holder.setVisible(R.id.tv_range,true);
                    holder.setVisible(R.id.iv_range,false);
                    holder.setText(R.id.tv_range,""+(position+1));
                }
                holder.setText(R.id.tv_name,entity.getName());
                holder.setImageUrl((ImageView)holder.getView(R.id.rimg_head),URLS.IMAGE_PRE + entity.getPhoto(),R.drawable.icon_head,60,60);
                holder.setText(R.id.tv_group,entity.getOfficeName());
                holder.setText(R.id.tv_score,entity.getIntegral());
            }
        };
        binding.recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView1.setAdapter(adapter1);

        binding.recyclerView1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                binding.srl.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void initAdapter2(){
        adapter2 = new CommonAdapter<JifenEntity>(this,R.layout.item_my_jifen,list2){
            @Override
            protected void convert(ViewHolder holder, JifenEntity entity, int position) {
                if(position == 0){
                    holder.setVisible(R.id.tv_range,false);
                    holder.setVisible(R.id.iv_range,true);
                    holder.setImageResource(R.id.iv_range,R.drawable.icon_jifen_range1);
                }else if(position == 1){
                    holder.setVisible(R.id.tv_range,false);
                    holder.setVisible(R.id.iv_range,true);
                    holder.setImageResource(R.id.iv_range,R.drawable.icon_jifen_range2);
                }else if(position == 2){
                    holder.setVisible(R.id.tv_range,false);
                    holder.setVisible(R.id.iv_range,true);
                    holder.setImageResource(R.id.iv_range,R.drawable.icon_jifen_range3);
                }else {
                    holder.setVisible(R.id.tv_range,true);
                    holder.setVisible(R.id.iv_range,false);
                    holder.setText(R.id.tv_range,""+(position+1));
                }
                holder.setText(R.id.tv_name,entity.getName());
                holder.setImageUrl((ImageView)holder.getView(R.id.rimg_head),URLS.IMAGE_PRE + entity.getPhoto(),R.drawable.icon_head,60,60);
                holder.setText(R.id.tv_group,entity.getOfficeName());
                holder.setText(R.id.tv_score,entity.getIntegral());

            }
        };
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView2.setAdapter(adapter2);

        binding.recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                binding.srl.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void initUI(JifenEntity entity){
        if(entity!=null){
            binding.tvScore.setText(entity.getIntegral());
            binding.tvRange.setText(String.valueOf(entity.getRownum()));
        }
    }


    private void getJifenDetail(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("","");
        LogUtil.showLog("积分详情map:"+map);
        OkHttpClientManager.postAsyn(JIFEN_DETAIL, new OkHttpClientManager.ResultCallback<BaseObject<JifenEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<JifenEntity> response) {
                binding.srl.setRefreshing(false);
                if(response.isSuccess()){
                    if(currentChecked == 0){
                        LogUtil.showLog("我的个人积分："+ MyApplication.gson.toJson(response));
                        entity1 = response.getData();
                        initUI(entity1);
                    }else {
                        LogUtil.showLog("党组织积分："+ MyApplication.gson.toJson(response));
                        entity2 = response.getData();
                        initUI(entity2);
                    }
                }else{
                    MyTools.showToast(MyJifenActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }

    private void getJifenList(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("pageNo",String.valueOf(pageNo));
        map.put("pageSize",MyApplication.pageSize);
        LogUtil.showLog("积分列表map:"+map);
        OkHttpClientManager.postAsyn(JIFEN_LIST, new OkHttpClientManager.ResultCallback<BaseList<JifenEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.nrl.onLoadFinished();
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
                if(currentChecked == 0)binding.tvEmpty.setVisibility(list1==null||list1.size()==0?View.VISIBLE:View.GONE);
                else binding.tvEmpty.setVisibility(list2==null||list2.size()==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<JifenEntity> response) {
                binding.nrl.onLoadFinished();
                binding.srl.setRefreshing(false);
                LogUtil.showLog("积分列表："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    if(pageNo==1){
                        if(currentChecked == 0) list1 =response.getData();
                        else list2 = response.getData();
                    }else{
                        if(response.getData() == null || response.getData().size() ==0) MyTools.showToast(MyJifenActivity.this,"没有更多内容了");
                        else {
                            if(currentChecked == 0) list1.addAll(response.getData());
                            else list2.addAll(response.getData());
                        }
                    }
                    if(currentChecked == 0) adapter1.refresh(list1);
                    else adapter2.refresh(list2);
                }else{
                    MyTools.showToast(MyJifenActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
                if(currentChecked == 0)binding.tvEmpty.setVisibility(list1==null||list1.size()==0?View.VISIBLE:View.GONE);
                else binding.tvEmpty.setVisibility(list2==null||list2.size()==0?View.VISIBLE:View.GONE);
            }
        },header,map);
    }



    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb1:
                currentChecked = 0;
                JIFEN_DETAIL=URLS.MY_JIFEN_DETAIL_PERSON;
                JIFEN_LIST= URLS.MY_JIFEN_PERSONAL;
                if(entity1==null) getJifenDetail();
                if(list1==null||list1.size()==0)getJifenList();
                binding.recyclerView1.setVisibility(View.VISIBLE);
                binding.recyclerView2.setVisibility(View.GONE);
                binding.tvEmpty.setVisibility(list1==null||list1.size()==0?View.VISIBLE:View.GONE);
                break;
            case R.id.rb2:
                currentChecked = 1;
                JIFEN_DETAIL=URLS.MY_JIFEN_DETAIL_BRANCH;
                JIFEN_LIST= URLS.MY_JIFEN_BRANCH;
                if(entity2==null) getJifenDetail();
                if(list2==null||list2.size()==0)getJifenList();
                binding.recyclerView1.setVisibility(View.GONE);
                binding.recyclerView2.setVisibility(View.VISIBLE);
                binding.tvEmpty.setVisibility(list2==null||list2.size()==0?View.VISIBLE:View.GONE);
                break;
        }
    }


    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                getJifenDetail();
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
                pageNo++;
                getJifenList();
            }
        },500);
    }


}
